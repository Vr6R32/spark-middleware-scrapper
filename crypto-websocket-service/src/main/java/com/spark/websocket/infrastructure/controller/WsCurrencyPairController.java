package com.spark.websocket.infrastructure.controller;

import com.spark.websocket.domain.currency_pair.WsCurrencyPairFacade;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Objects;


@Controller
public record WsCurrencyPairController(WsCurrencyPairFacade wsCurrencyPairFacade) {


    private static final String SESSION_TIME_ZONE_ATTRIBUTE = "timeZone";

    @MessageMapping("api/v1/currencies")
    public void getAvailableCurrencies(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        wsCurrencyPairFacade.fetchAndSendAvailableCurrenciesToSession(sessionId);
    }

    @MessageMapping("api/v1/last/{symbol}")
    public void getLatestCurrencyPairRate(@DestinationVariable("symbol") String symbol, StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String userTimeZone = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get(SESSION_TIME_ZONE_ATTRIBUTE);
        wsCurrencyPairFacade.fetchAndSendLatestCurrencyPairRateToSession(symbol,sessionId,userTimeZone);
    }

    @MessageMapping("api/v1/lastDay/{symbol}")
    public void getCurrencyPairLast24hRateHistory(@DestinationVariable("symbol") String symbol, StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String userTimeZone = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get(SESSION_TIME_ZONE_ATTRIBUTE);
        wsCurrencyPairFacade.fetchAndSendCurrencyPairLast24hRateHistoryToSession(symbol,sessionId,userTimeZone);
    }

    @MessageMapping("api/v1/lastAll")
    public void getAvailableCurrencyPairsLatestCurrencyRate(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String userTimeZone = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get(SESSION_TIME_ZONE_ATTRIBUTE);
        wsCurrencyPairFacade.fetchAndSendAvailableCurrencyPairsLatestCurrencyRateToSession(sessionId,userTimeZone);
    }

}
