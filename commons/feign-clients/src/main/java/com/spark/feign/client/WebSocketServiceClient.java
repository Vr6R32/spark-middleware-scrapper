package com.spark.feign.client;

import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "websocket-service")
public interface WebSocketServiceClient {

    @PostMapping("api/v1/events/scrapper/update")
    void pushScrappedDataForUpdateToWebSocketSessions(@RequestBody ScrappedCurrencyUpdateRequest scrappedCurrencyUpdateRequest);
}
