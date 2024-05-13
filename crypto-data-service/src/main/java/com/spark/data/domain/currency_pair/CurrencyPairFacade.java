package com.spark.data.domain.currency_pair;

import com.spark.models.request.CurrencyPairCreateRequest;
import com.spark.models.request.CurrencyPairDeleteRequest;
import com.spark.models.request.CurrencyPairUpdateRequest;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairResponse;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class CurrencyPairFacade {

    private final CurrencyPairService currencyPairService;


    public AvailableCurrencyPairsResponse getAvailableCurrencies() {
        return currencyPairService.getAvailableCurrencies();
    }

    public CurrencyPairResponse registerNewCurrencyPair(CurrencyPairCreateRequest request) {
        return currencyPairService.registerNewCurrencyPair(request);
    }

    public CurrencyPairResponse changeCurrencyPairData(CurrencyPairUpdateRequest request) {
        return currencyPairService.changeCurrencyPairData(request);
    }

    public CurrencyPairResponse deleteCurrencyPair(CurrencyPairDeleteRequest request) {
        return currencyPairService.deleteCurrencyPair(request);
    }


}
