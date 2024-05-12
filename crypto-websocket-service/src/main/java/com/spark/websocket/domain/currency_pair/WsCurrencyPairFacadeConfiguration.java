package com.spark.websocket.domain.currency_pair;

import com.spark.feign.client.CryptoDataServiceClient;
import com.spark.websocket.infrastructure.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
@RequiredArgsConstructor
class WsCurrencyPairFacadeConfiguration {

    private final CryptoDataServiceClient cryptoDataServiceClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;

    @Bean
    public WsCurrencyPairFacade wsCurrencyPairFacade() {
        WsCurrencyPairService wsCurrencyPairService = new WsCurrencyPairService(cryptoDataServiceClient, messagingTemplate, sessionManager);
        return new WsCurrencyPairFacade(wsCurrencyPairService);
    }
}
