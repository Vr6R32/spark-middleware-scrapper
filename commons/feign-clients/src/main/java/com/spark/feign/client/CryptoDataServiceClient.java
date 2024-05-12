package com.spark.feign.client;

import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairChartRateHistoryResponse;
import com.spark.models.response.CurrencyPairSingleRateHistoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(url = "localhost:9001", name = "crypto-data-service")
public interface CryptoDataServiceClient {

    @GetMapping("api/v1/currencies")
    AvailableCurrencyPairsResponse getAvailableCurrencies();

    @GetMapping("api/v1/currencies/last/{symbol}")
    CurrencyPairSingleRateHistoryResponse getLatestCurrencyPairRate(@PathVariable("symbol") String symbol, @RequestParam(defaultValue = "Europe/Warsaw", required = false, name = "userZoneId") String userZoneId);

    @GetMapping("api/v1/currencies/lastDay/{symbol}")
    CurrencyPairChartRateHistoryResponse getCurrencyPairLast24hRateHistory(@PathVariable("symbol") String symbol, @RequestParam(defaultValue = "Europe/Warsaw", required = false, name = "userZoneId") String userZoneId);

    @PostMapping("api/v1/management/currencies/scrapper/update")
    void pushScrappedCurrencySetForDataServiceUpdate(@RequestBody ScrappedCurrencyUpdateRequest request);
}
