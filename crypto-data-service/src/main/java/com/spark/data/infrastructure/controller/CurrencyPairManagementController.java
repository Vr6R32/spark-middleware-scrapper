package com.spark.data.infrastructure.controller;

import com.spark.data.domain.currency_pair.CurrencyPairFacade;
import com.spark.data.domain.currency_pair_rate_history.CurrencyPairRateHistoryFacade;
import com.spark.models.request.*;
import com.spark.models.response.CurrencyPairResponse;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/management/currencies")
record CurrencyPairManagementController(CurrencyPairFacade currencyPairFacade, CurrencyPairRateHistoryFacade currencyPairRateHistoryFacade) implements ManagementApi {

    @PostMapping("/scrapper/update")
    public void updateCurrencyPairRateHistory(@RequestBody ScrappedCurrencyUpdateRequest request) {
        currencyPairRateHistoryFacade.updateCurrencyPairRateHistory(request);
    }

    @PostMapping
    public CurrencyPairResponse registerNewCurrencyPair(@RequestBody CurrencyPairCreateRequest request) {
        return currencyPairFacade.registerNewCurrencyPair(request);
    }

    @PutMapping
    public CurrencyPairResponse changeCurrencyPairData(@RequestBody CurrencyPairUpdateRequest request) {
        return currencyPairFacade.changeCurrencyPairData(request);
    }

    @DeleteMapping
    public CurrencyPairResponse deleteCurrencyPair(@RequestBody CurrencyPairDeleteRequest request) {
        return currencyPairFacade.deleteCurrencyPair(request);
    }

}
