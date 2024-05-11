package com.spark.websocket.domain.update_event;

import com.spark.websocket.infrastructure.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
@RequiredArgsConstructor
class WsUpdateEventFacadeConfiguration {

    private final WebSocketSessionManager webSocketSessionManager;
    private final SimpMessagingTemplate messagingTemplate;

    @Bean
    public WsUpdateEventFacade wsUpdateEventFacade() {
        WsUpdateEventService updateEventService = new WsUpdateEventService(webSocketSessionManager,messagingTemplate);
        return new WsUpdateEventFacade(updateEventService);
    }
}
