package org.project.evconnectbackend.service.impl;

import lombok.RequiredArgsConstructor;

import org.project.evconnectbackend.dto.chat.ChatMessageRequest;
import org.project.evconnectbackend.dto.chat.ChatMessageResponse;
import org.project.evconnectbackend.model.Booking;
import org.project.evconnectbackend.model.BookingStatus;
import org.project.evconnectbackend.model.ChatMessage;
import org.project.evconnectbackend.model.User;
import org.project.evconnectbackend.repository.BookingRepository;
import org.project.evconnectbackend.repository.ChatMessageRepository;
import org.project.evconnectbackend.repository.UserRepository;
import org.project.evconnectbackend.service.ChatService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(String email, ChatMessageRequest request) {
        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if the user is authorized to send messages for this booking
        if (!booking.getBorrower().getEmail().equals(email) && 
            !booking.getChargingStation().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to send messages for this booking");
        }

        // Check if the booking is active
        if (booking.getStatus() != BookingStatus.ACCEPTED &&
            booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("Cannot send messages for this booking");
        }

        ChatMessage message = new ChatMessage();
        message.setBooking(booking);
        message.setSender(sender);
        message.setMessage(request.getMessage());
        message.setRead(false);

        message = chatMessageRepository.save(message);

        // Send real-time notification to the other party
        String recipientEmail = booking.getBorrower().getEmail().equals(email) 
                ? booking.getChargingStation().getUser().getEmail() 
                : booking.getBorrower().getEmail();
        
        // In a real application, you would send a push notification here
        // For now, we'll just use WebSocket to send the message
        ChatMessageResponse response = convertToResponse(message);
        messagingTemplate.convertAndSend("/topic/chat/" + booking.getId(), response);

        return response;
    }

    @Override
    @Transactional
    public List<ChatMessageResponse> getMessages(String email, Long bookingId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if the user is authorized to view messages for this booking
        if (!booking.getBorrower().getEmail().equals(email) && 
            !booking.getChargingStation().getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to view messages for this booking");
        }

        // Mark unread messages as read
        List<ChatMessage> unreadMessages = chatMessageRepository.findByBookingIdAndIsReadFalseAndSenderEmailNot(bookingId, email);
        for (ChatMessage message : unreadMessages) {
            message.setRead(true);
            chatMessageRepository.save(message);
        }

        return chatMessageRepository.findByBookingIdOrderByCreatedAtAsc(bookingId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    protected ChatMessageResponse convertToResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId().toString());
        response.setBookingId(message.getBooking().getId().toString());
        response.setSenderId(message.getSender().getId().toString());
        response.setSenderName(message.getSender().getName());
        response.setMessage(message.getMessage());
        response.setRead(message.isRead());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }
} 