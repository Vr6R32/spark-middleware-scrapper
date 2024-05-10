package com.spark.websocket.domain.currency_pair;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WsCurrencyPairFacade {

    private final WsCurrencyPairService wsCurrencyPairService;

    public void fetchAndSendAvailableCurrenciesToSession(String sessionId) {
        wsCurrencyPairService.fetchAndSendAvailableCurrenciesToSession(sessionId);
    }

    public void fetchAndSendCurrencyPairLast24hRateHistoryToSession(String symbol, String sessionId) {
        wsCurrencyPairService.fetchAndSendCurrencyPairLast24hRateHistoryToSession(symbol, sessionId);
    }
}
