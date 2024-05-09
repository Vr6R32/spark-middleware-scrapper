package com.spark.data.infrastructure.controller;

import com.spark.data.domain.currency_pair.CurrencyPairFacade;
import com.spark.data.domain.currency_pair_rate_history.CurrencyPairRateHistoryFacade;
import com.spark.models.request.CurrencyPairRequest;
import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import com.spark.models.response.CurrencyPairResponse;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/management/currencies")
public record CurrencyPairManagementController(CurrencyPairFacade currencyPairFacade, CurrencyPairRateHistoryFacade currencyPairRateHistoryFacade) {

    @PostMapping("/scrapper/update")
    void updateCurrencyPairRateHistory(@RequestBody ScrappedCurrencyUpdateRequest request) {
        currencyPairRateHistoryFacade.updateCurrencyPairRateHistory(request);
    }

    @PostMapping
    public CurrencyPairResponse registerNewCurrencyPair(@RequestBody CurrencyPairRequest request) {
        return currencyPairFacade.registerNewCurrencyPair(request);
    }

    @PutMapping
    public CurrencyPairResponse changeCurrencyPairData(@RequestBody CurrencyPairRequest request) {
        return currencyPairFacade.changeCurrencyPairData(request);
    }

    @DeleteMapping
    public CurrencyPairResponse deleteCurrencyPair(@RequestBody CurrencyPairRequest request) {
        return currencyPairFacade.deleteCurrencyPair(request);
    }

}
