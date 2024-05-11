package com.spark.websocket.infrastructure.configuration;

import com.spark.websocket.infrastructure.session.WebSocketSessionInterceptor;
import com.spark.websocket.infrastructure.session.InMemoryWebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WsConfiguration implements WebSocketMessageBrokerConfigurer {


    @Bean
    public InMemoryWebSocketSessionManager sessionManager() {
        return new InMemoryWebSocketSessionManager();
    }

    @Bean
    public WebSocketSessionInterceptor webSocketInterceptor(){
        return new WebSocketSessionInterceptor(sessionManager());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker( "/user", "/ws");
        registry.setApplicationDestinationPrefixes("/ws");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(1000);
        registry.addDecoratorFactory(webSocketInterceptor());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors();
    }
}