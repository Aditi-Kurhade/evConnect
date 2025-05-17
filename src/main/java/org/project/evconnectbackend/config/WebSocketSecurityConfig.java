package org.project.evconnectbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        // Explicitly allow all message types without security checks
        messages
            .simpTypeMatchers(SimpMessageType.CONNECT, 
                             SimpMessageType.DISCONNECT,
                             SimpMessageType.OTHER,
                             SimpMessageType.SUBSCRIBE,
                             SimpMessageType.UNSUBSCRIBE,
                             SimpMessageType.HEARTBEAT,
                             SimpMessageType.MESSAGE).permitAll()
            .nullDestMatcher().permitAll()
            .simpDestMatchers("/app/**").permitAll()
            .simpSubscribeDestMatchers("/topic/**").permitAll()
            .anyMessage().permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        // Completely disable CSRF protection for WebSockets
        return true;
    }
} 