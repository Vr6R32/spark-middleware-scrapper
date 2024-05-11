package com.spark.websocket.infrastructure.controller;

import com.spark.websocket.domain.currency_pair.WsCurrencyPairFacade;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Objects;


@Controller
public record WsCurrencyPairController(WsCurrencyPairFacade wsCurrencyPairFacade) {


    @MessageMapping("api/v1/currencies")
    public void getAvailableCurrencies(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        wsCurrencyPairFacade.fetchAndSendAvailableCurrenciesToSession(sessionId);
    }

    @MessageMapping("api/v1/lastDay/{symbol}")
    public void getCurrencyPairLast24hRateHistory(@DestinationVariable("symbol") String symbol, StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String userTimeZone = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("timeZone");
        wsCurrencyPairFacade.fetchAndSendCurrencyPairLast24hRateHistoryToSession(symbol,sessionId,userTimeZone);
    }


}
