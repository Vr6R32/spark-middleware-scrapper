package com.spark.websocket.infrastructure.controller;

import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import com.spark.websocket.domain.update_event.WsUpdateEventFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/events")
public class WsUpdateEventController {

    private final WsUpdateEventFacade wsUpdateEventFacade;

    @PutMapping("scrapper/update")
    public void retrieveScrapperDataUpdate(@RequestBody ScrappedCurrencyUpdateRequest scrappedCurrencyUpdateRequest) {
        wsUpdateEventFacade.sendDataUpdateEventToUserSessions(scrappedCurrencyUpdateRequest);
    }

}
