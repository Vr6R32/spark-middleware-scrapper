package com.spark.websocket.domain.currency_pair;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WsCurrencyPairFacade {

    private final WsCurrencyPairService wsCurrencyPairService;

    public void fetchAndSendAvailableCurrenciesToSession(String sessionId) {
        wsCurrencyPairService.fetchAndSendAvailableCurrenciesToSession(sessionId);
    }

    public void fetchAndSendLatestCurrencyPairRateToSession(String symbol, String sessionId, String userTimeZone) {
        wsCurrencyPairService.fetchAndSendLatestCurrencyPairRateToSession(symbol, sessionId, userTimeZone);
    }

    public void fetchAndSendCurrencyPairLast24hRateHistoryToSession(String symbol, String sessionId, String userTimeZone) {
        wsCurrencyPairService.fetchAndSendCurrencyPairLast24hRateHistoryToSession(symbol, sessionId, userTimeZone);
    }

    public void fetchAndSendAvailableCurrencyPairsLatestCurrencyRateToSession(String sessionId, String userTimeZone) {
        wsCurrencyPairService.fetchAndSendAvailableCurrencyPairsLatestCurrencyRateToSession(sessionId, userTimeZone);
    }
}
