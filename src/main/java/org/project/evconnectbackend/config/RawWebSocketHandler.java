package org.project.evconnectbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class RawWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = Logger.getLogger(RawWebSocketHandler.class.getName());
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> bookingSubscriptions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocket connection established: " + session.getId());
        sessions.put(session.getId(), session);
        
        // Send a welcome message
        String message = "{\"type\":\"CONNECTED\",\"message\":\"Connected successfully\"}";
        session.sendMessage(new TextMessage(message));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Received message: " + payload);

        try {
            Map<String, Object> messageData = objectMapper.readValue(payload, Map.class);
            String type = (String) messageData.get("type");
            String bookingId = (String) messageData.get("bookingId");

            if ("JOIN".equals(type) && bookingId != null) {
                // Add session to booking room
                bookingSubscriptions.computeIfAbsent(bookingId, k -> new ConcurrentSkipListSet<>())
                                  .add(session.getId());
                
                String response = "{\"type\":\"JOINED\",\"bookingId\":\"" + bookingId + "\"}";
                session.sendMessage(new TextMessage(response));
            } else if ("CHAT".equals(type) && bookingId != null) {
                // Broadcast to all sessions in the booking room
                String content = objectMapper.writeValueAsString(messageData);
                broadcastToBooking(bookingId, content);
            }
        } catch (Exception e) {
            logger.warning("Error processing message: " + e.getMessage());
            session.sendMessage(new TextMessage("{\"type\":\"ERROR\",\"message\":\"Invalid message format\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("WebSocket connection closed: " + session.getId());
        
        // Remove session from all booking subscriptions
        String sessionId = session.getId();
        bookingSubscriptions.values().forEach(subscribers -> subscribers.remove(sessionId));
        
        // Clean up empty booking rooms
        bookingSubscriptions.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        
        sessions.remove(sessionId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.warning("Transport error: " + exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
        sessions.remove(session.getId());
    }

    private void broadcastToBooking(String bookingId, String message) {
        Set<String> subscribers = bookingSubscriptions.get(bookingId);
        if (subscribers != null) {
            subscribers.forEach(sessionId -> {
                WebSocketSession session = sessions.get(sessionId);
                if (session != null && session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (Exception e) {
                        logger.warning("Error sending message to session " + sessionId + ": " + e.getMessage());
                    }
                }
            });
        }
    }
} 