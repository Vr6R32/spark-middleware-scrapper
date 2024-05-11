package com.spark.websocket.domain.currency_pair;

import com.spark.feign_client.CryptoDataServiceClient;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairChartRateHistoryResponse;
import com.spark.websocket.infrastructure.model.WebSocketEventMessageResponse;
import com.spark.websocket.infrastructure.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static com.spark.websocket.infrastructure.model.MessageEventType.AVAILABLE_CURRENCIES;
import static com.spark.websocket.infrastructure.model.MessageEventType.LAST_24H_CURRENCY_PAIR_RATE_HISTORY;

@RequiredArgsConstructor
class WsCurrencyPairService {

    private final CryptoDataServiceClient cryptoDataServiceClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;

    public void fetchAndSendAvailableCurrenciesToSession(String sessionId) {
        AvailableCurrencyPairsResponse availableCurrencies = cryptoDataServiceClient.getAvailableCurrencies();
        WebSocketEventMessageResponse webSocketEventMessageResponse = new WebSocketEventMessageResponse(AVAILABLE_CURRENCIES, availableCurrencies);
        messagingTemplate.convertAndSend("/ws/session/" + sessionId, webSocketEventMessageResponse);
    }

    public void fetchAndSendCurrencyPairLast24hRateHistoryToSession(String symbol, String sessionId, String userTimeZone) {
        CurrencyPairChartRateHistoryResponse currencyPairLast24hRateHistory = cryptoDataServiceClient.getCurrencyPairLast24hRateHistory(symbol, userTimeZone);
        WebSocketEventMessageResponse webSocketEventMessageResponse = new WebSocketEventMessageResponse(LAST_24H_CURRENCY_PAIR_RATE_HISTORY, currencyPairLast24hRateHistory);
        sessionManager.setSessionAttribute(sessionId, "selectedSymbol", symbol);
        messagingTemplate.convertAndSend("/ws/session/" + sessionId, webSocketEventMessageResponse);
    }
}
