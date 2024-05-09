package com.spark.feign_client;

import com.spark.models.model.ScrappedCurrency;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Set;


@FeignClient(url = "localhost:9001",name = "crypto-data-service")
public interface CryptoDataServiceClient {

    @GetMapping("api/v1/currencies")
    AvailableCurrencyPairsResponse getAvailableCurrencies();

    @PutMapping("api/v1/currencies/update")
    void pushScrappedCurrencySetForUpdate(Set<ScrappedCurrency> scrappedCurrencySet);
}
