package com.spark.data.infrastructure.controller;

import com.spark.data.domain.currency_pair.CurrencyPairFacade;
import com.spark.data.domain.currency_pair_rate_history.CurrencyPairRateHistoryFacade;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairChartRateHistoryResponse;
import com.spark.models.response.CurrencyPairSingleRateHistoryResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/currencies")
record CurrencyPairUserController(CurrencyPairFacade currencyPairFacade, CurrencyPairRateHistoryFacade currencyPairRateHistoryFacade) implements UserApi {


    @GetMapping
    public AvailableCurrencyPairsResponse getAvailableCurrencies() {
        return currencyPairFacade.getAvailableCurrencies();
    }

    @GetMapping("/last/{symbol}")
    public CurrencyPairSingleRateHistoryResponse getLatestCurrencyPairRate(@PathVariable("symbol") String symbol,
                                                                           @RequestParam(defaultValue = "Europe/Warsaw", required = false, name = "userZoneId") String userZoneId) {
        return currencyPairRateHistoryFacade.getLatestCurrencyPairRate(symbol, userZoneId);
    }

    @GetMapping("/lastDay/{symbol}")
    public CurrencyPairChartRateHistoryResponse getCurrencyPairLast24hRateHistory(@PathVariable("symbol") String symbol,
                                                                                  @RequestParam(defaultValue = "Europe/Warsaw", required = false, name = "userZoneId") String userZoneId) {
        return currencyPairRateHistoryFacade.getCurrencyPairLast24hRateHistory(symbol, userZoneId);
    }

    @GetMapping("/lastAll")
    public List<CurrencyPairSingleRateHistoryResponse> getAvailableCurrencyPairsLatestCurrencyRate(@RequestParam(defaultValue = "Europe/Warsaw", required = false, name = "userZoneId") String userZoneId) {
        return currencyPairRateHistoryFacade.getAvailableCurrencyPairsLatestCurrencyRate(userZoneId);
    }

}
