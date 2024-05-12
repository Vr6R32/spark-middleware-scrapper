package com.spark.websocket.domain.currency_pair;

import com.spark.feign.client.CryptoDataServiceClient;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairChartRateHistoryResponse;
import com.spark.models.response.CurrencyPairSingleRateHistoryResponse;
import com.spark.websocket.infrastructure.model.WebSocketEventMessageResponse;
import com.spark.websocket.infrastructure.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static com.spark.websocket.infrastructure.model.MessageEventType.*;

@RequiredArgsConstructor
class WsCurrencyPairService {

    private final CryptoDataServiceClient cryptoDataServiceClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;
    private static final String WS_SESSION_URL = "/ws/session/";

    public void fetchAndSendAvailableCurrenciesToSession(String sessionId) {
        AvailableCurrencyPairsResponse availableCurrencies = cryptoDataServiceClient.getAvailableCurrencies();
        WebSocketEventMessageResponse webSocketEventMessageResponse = new WebSocketEventMessageResponse(AVAILABLE_CURRENCIES, availableCurrencies);
        messagingTemplate.convertAndSend(WS_SESSION_URL + sessionId, webSocketEventMessageResponse);
    }

    public void fetchAndSendLatestCurrencyPairRateToSession(String symbol, String sessionId, String userTimeZone) {
        CurrencyPairSingleRateHistoryResponse latestCurrencyPairRate = cryptoDataServiceClient.getLatestCurrencyPairRate(symbol, userTimeZone);
        WebSocketEventMessageResponse webSocketEventMessageResponse = new WebSocketEventMessageResponse(LATEST_CURRENCY_PAIR_RATE, latestCurrencyPairRate);
        messagingTemplate.convertAndSend(WS_SESSION_URL + sessionId, webSocketEventMessageResponse);
    }

    public void fetchAndSendCurrencyPairLast24hRateHistoryToSession(String symbol, String sessionId, String userTimeZone) {
        CurrencyPairChartRateHistoryResponse currencyPairLast24hRateHistory = cryptoDataServiceClient.getCurrencyPairLast24hRateHistory(symbol, userTimeZone);
        WebSocketEventMessageResponse webSocketEventMessageResponse = new WebSocketEventMessageResponse(LAST_24H_CURRENCY_PAIR_RATE_HISTORY, currencyPairLast24hRateHistory);
        sessionManager.setSessionAttribute(sessionId, "selectedSymbol", symbol);
        messagingTemplate.convertAndSend(WS_SESSION_URL + sessionId, webSocketEventMessageResponse);
    }

    public void fetchAndSendAvailableCurrencyPairsLatestCurrencyRateToSession(String sessionId, String userTimeZone) {
        List<CurrencyPairSingleRateHistoryResponse> availableCurrencyPairsLatestCurrencyRate = cryptoDataServiceClient.getAvailableCurrencyPairsLatestCurrencyRate(userTimeZone);
        WebSocketEventMessageResponse webSocketEventMessageResponse = new WebSocketEventMessageResponse(AVAILABLE_CURRENCIES_LATEST_RATE_HISTORY, availableCurrencyPairsLatestCurrencyRate);
        messagingTemplate.convertAndSend(WS_SESSION_URL + sessionId, webSocketEventMessageResponse);
    }
}
