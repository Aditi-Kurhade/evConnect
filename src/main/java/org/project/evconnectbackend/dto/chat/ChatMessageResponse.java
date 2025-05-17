package org.project.evconnectbackend.dto.chat;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String id;
    private String bookingId;
    private String senderId;
    private String senderName;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
    private String type;
} 