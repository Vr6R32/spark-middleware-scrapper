package com.spark.data.domain.currency_pair;

import com.spark.models.request.CurrencyPairRequest;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairResponse;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class CurrencyPairFacade {

    private final CurrencyPairService currencyPairService;

    public AvailableCurrencyPairsResponse getAvailableCurrencies() {
        return currencyPairService.getAvailableCurrencies();
    }

    public CurrencyPairResponse registerNewCurrencyPair(CurrencyPairRequest request) {
        return currencyPairService.registerNewCurrencyPair(request);
    }

    public CurrencyPairResponse deleteCurrencyPair(CurrencyPairRequest request) {
        return currencyPairService.deleteCurrencyPair(request);
    }

    public CurrencyPairResponse changeCurrencyPairData(CurrencyPairRequest request) {
        return currencyPairService.changeCurrencyPairData(request);
    }
}
