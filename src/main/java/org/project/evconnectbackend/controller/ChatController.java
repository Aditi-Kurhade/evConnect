package org.project.evconnectbackend.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.project.evconnectbackend.dto.chat.ChatMessageRequest;
import org.project.evconnectbackend.dto.chat.ChatMessageResponse;
import org.project.evconnectbackend.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.join")
    public void joinChat(@Payload ChatMessageRequest message, Principal principal) {
        log.info("User {} joined chat for booking {}", principal.getName(), message.getBookingId());
        
        // Create a system message that the user has joined
        ChatMessageRequest systemMessage = new ChatMessageRequest();
        systemMessage.setBookingId(message.getBookingId());
        systemMessage.setMessage(principal.getName() + " has joined the chat");
        systemMessage.setSenderName("System");
//        systemMessage.setType("SYSTEM");
        
        // Send the join message to the chat topic
        ChatMessageResponse response = chatService.sendMessage(principal.getName(), systemMessage);
        messagingTemplate.convertAndSend("/topic/chat/" + message.getBookingId(), response);
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest message, Principal principal) {
        log.info("Received message from {}: {}", principal.getName(), message.getMessage());
        
        // Use the existing chat service to handle message storage and delivery
        ChatMessageResponse response = chatService.sendMessage(principal.getName(), message);
        messagingTemplate.convertAndSend("/topic/chat/" + message.getBookingId(), response);
    }

    // New handler for messages sent to /app/chat/{bookingId}
    @MessageMapping("/chat/{bookingId}")
    public void handleDirectChatMessage(
            @DestinationVariable String bookingId,
            @Payload ChatMessageRequest message, 
            Principal principal) {
        
        log.info("Received direct message via /app/chat/{} from {}: {}", 
                bookingId, 
                principal != null ? principal.getName() : "anonymous", 
                message);
        
        // Set the booking ID from the path variable if not already set
        if (message.getBookingId() == null) {
            try {
                message.setBookingId(Long.valueOf(bookingId));
            } catch (NumberFormatException e) {
                log.error("Invalid booking ID format: {}", bookingId);
                messagingTemplate.convertAndSend(
                    "/topic/chat/" + bookingId + "/error", 
                    "Invalid booking ID format"
                );
                return;
            }
        }
        
        // Check if user is authenticated
        if (principal == null || "anonymous".equals(principal.getName())) {
            log.error("Unauthenticated user tried to send message for booking {}", bookingId);
            messagingTemplate.convertAndSend(
                "/topic/chat/" + bookingId + "/error", 
                "Authentication required to send messages"
            );
            return;
        }
        
        try {
            // Process and store the message
            ChatMessageResponse response = chatService.sendMessage(principal.getName(), message);
            
            // Broadcast the message to the topic
            messagingTemplate.convertAndSend("/topic/chat/" + bookingId, response);
            
            log.info("Message successfully processed and sent to topic for booking {}", bookingId);
        } catch (Exception e) {
            log.error("Error processing message for booking {}: {}", bookingId, e.getMessage(), e);
            
            // Send error response back to sender
            messagingTemplate.convertAndSendToUser(
                principal.getName(), 
                "/queue/errors", 
                "Error sending message: " + e.getMessage());
            
            // Also send to the topic so other clients know there was an error
            messagingTemplate.convertAndSend(
                "/topic/chat/" + bookingId + "/error", 
                "Error sending message: " + e.getMessage());
        }
    }

    @GetMapping("/messages/{bookingId}")
    public List<ChatMessageResponse> getMessages(@PathVariable String bookingId, Principal principal) {
        log.info("Fetching messages for booking {}", bookingId);
        // Convert String to Long for service call
        return chatService.getMessages(principal.getName(), Long.parseLong(bookingId));
    }
} 