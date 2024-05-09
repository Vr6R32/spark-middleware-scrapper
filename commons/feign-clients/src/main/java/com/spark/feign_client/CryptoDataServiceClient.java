package com.spark.feign_client;

import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(url = "localhost:9001", name = "crypto-data-service")
public interface CryptoDataServiceClient {

    @GetMapping("api/v1/currencies")
    AvailableCurrencyPairsResponse getAvailableCurrencies();

    @PostMapping("api/v1/management/currencies/scrapper/update")
    void pushScrappedCurrencySetForUpdate(@RequestBody ScrappedCurrencyUpdateRequest request);
}
