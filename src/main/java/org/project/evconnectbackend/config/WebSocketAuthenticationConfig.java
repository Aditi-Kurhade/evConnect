package org.project.evconnectbackend.config;

import org.project.evconnectbackend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String bearerToken = accessor.getFirstNativeHeader("Authorization");
                    log.debug("Received WebSocket connection with Authorization: {}", bearerToken);
                    
                    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                        String token = bearerToken.substring(7);
                        
                        try {
                            if (jwtTokenProvider.validateToken(token)) {
                                String username = jwtTokenProvider.getUsernameFromJWT(token);
                                log.debug("Extracted username from JWT: {}", username);
                                
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                
                                Authentication auth = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                                
                                accessor.setUser(auth);
                                log.debug("WebSocket connection authenticated for user: {}", username);
                            } else {
                                log.warn("Invalid JWT token in WebSocket connection");
                            }
                        } catch (Exception e) {
                            log.error("Error validating JWT token: {}", e.getMessage(), e);
                        }
                    } else {
                        log.warn("No valid JWT token found in WebSocket connection");
                    }
                }
                
                return message;
            }
        });
    }
} 