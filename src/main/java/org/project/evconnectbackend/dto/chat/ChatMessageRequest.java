package org.project.evconnectbackend.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatMessageRequest {
    @NotNull
    private Long bookingId;

    @NotBlank
        private String message;

    private String senderName;
} 