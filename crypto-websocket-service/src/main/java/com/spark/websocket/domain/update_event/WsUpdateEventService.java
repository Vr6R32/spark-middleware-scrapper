package com.spark.websocket.domain.update_event;

import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import com.spark.models.response.CurrencyPairSingleRateHistoryResponse;
import com.spark.websocket.infrastructure.model.WebSocketEventMessageResponse;
import com.spark.websocket.infrastructure.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import static com.spark.websocket.domain.update_event.ScrappedCurrencyMapper.mapScrappedCurrencyToCurrencyPairSingleRateHistoryResponse;
import static com.spark.websocket.infrastructure.model.MessageEventType.CURRENCY_PAIR_RATE_HISTORY_UPDATE;

@RequiredArgsConstructor
class WsUpdateEventService {

    private final WebSocketSessionManager webSocketSessionManager;
    private final SimpMessagingTemplate messagingTemplate;
    public void sendDataUpdateEventToUserSessions(ScrappedCurrencyUpdateRequest scrappedCurrencyUpdateRequest) {
        List<WebSocketSession> sessions = webSocketSessionManager.getAllActiveSessions();

        sessions.forEach(session -> {
            String timeZone = (String) session.getAttributes().get("timeZone");
            String symbol = (String) session.getAttributes().get("selectedSymbol");

            scrappedCurrencyUpdateRequest.scrappedCurrencySet().stream()
                    .filter(c -> c.symbol().equals(symbol))
                    .findFirst()
                    .ifPresent(scrappedCurrency -> {
                        CurrencyPairSingleRateHistoryResponse response = mapScrappedCurrencyToCurrencyPairSingleRateHistoryResponse(
                                scrappedCurrency, timeZone
                        );
                        sendMessageToSession(session.getId(), response);
                    });
        });
    }

    private void sendMessageToSession(String sessionId, CurrencyPairSingleRateHistoryResponse message) {
        WebSocketEventMessageResponse selectedCurrencyPairHistoryUpdate = new WebSocketEventMessageResponse(CURRENCY_PAIR_RATE_HISTORY_UPDATE, message);
        messagingTemplate.convertAndSend("/ws/session/" + sessionId, selectedCurrencyPairHistoryUpdate);
    }

}
