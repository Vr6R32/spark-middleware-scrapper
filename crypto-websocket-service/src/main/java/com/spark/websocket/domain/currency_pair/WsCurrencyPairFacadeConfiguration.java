package com.spark.websocket.domain.currency_pair;

import com.spark.feign_client.CryptoDataServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
@RequiredArgsConstructor
class WsCurrencyPairFacadeConfiguration {

    private final SimpMessagingTemplate messagingTemplate;
    private final CryptoDataServiceClient cryptoDataServiceClient;

    @Bean
    public WsCurrencyPairFacade wsCurrencyPairFacade() {
        WsCurrencyPairService wsCurrencyPairService = new WsCurrencyPairService(messagingTemplate,cryptoDataServiceClient);
        return new WsCurrencyPairFacade(wsCurrencyPairService);
    }
}
