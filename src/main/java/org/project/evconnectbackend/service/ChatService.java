package org.project.evconnectbackend.service;

import org.project.evconnectbackend.dto.chat.ChatMessageRequest;
import org.project.evconnectbackend.dto.chat.ChatMessageResponse;

import java.util.List;

public interface ChatService {
    ChatMessageResponse sendMessage(String email, ChatMessageRequest request);
    List<ChatMessageResponse> getMessages(String email, Long bookingId);
} 