package com.spark.websocket.domain.update_event;

import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WsUpdateEventFacade {

    private final WsUpdateEventService wsUpdateEventService;

    public void sendDataUpdateEventToUserSessions(ScrappedCurrencyUpdateRequest scrappedCurrencyUpdateRequest) {
        wsUpdateEventService.sendDataUpdateEventToUserSessions(scrappedCurrencyUpdateRequest);
    }
}
