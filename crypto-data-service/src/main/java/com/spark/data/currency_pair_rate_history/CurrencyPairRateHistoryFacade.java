package com.spark.data.currency_pair_rate_history;

import com.spark.models.response.CurrencyPairRateHistoryResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CurrencyPairRateHistoryFacade {


    private final CurrencyPairRateHistoryService currencyPairRateHistoryService;

    public CurrencyPairRateHistoryResponse getLatestCurrencyPairRate(String symbol, String userZoneId) {
        return currencyPairRateHistoryService.getLatestCurrencyPairRate(symbol, userZoneId);
    }

    public List<CurrencyPairRateHistoryResponse> getCurrencyPairLast24hRateHistory(String symbol, String userZoneId) {
        return currencyPairRateHistoryService.getCurrencyPairLast24hRateHistory(symbol, userZoneId);
    }

    public List<CurrencyPairRateHistoryResponse> getAvailableCurrencyPairsLatestCurrencyRate(String userZoneId) {
        return currencyPairRateHistoryService.getAvailableCurrencyPairsLatestCurrencyRate(userZoneId);
    }
}
