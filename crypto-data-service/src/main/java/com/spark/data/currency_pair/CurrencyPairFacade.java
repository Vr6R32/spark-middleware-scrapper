package com.spark.data.currency_pair;

import com.spark.models.model.ScrappedCurrency;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairRateHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class CurrencyPairFacade {

    private final CurrencyPairService currencyPairService;

    public AvailableCurrencyPairsResponse getAvailableCurrencies() {
        return currencyPairService.getAvailableCurrencies();
    }

    @Transactional
    public void updateCurrencyPairRateHistory(Set<ScrappedCurrency> scrappedCurrencySet) {
        currencyPairService.updateCurrencyPairRateHistory(scrappedCurrencySet);
    }

    public CurrencyPairRateHistoryResponse getLatestCurrencyPairRate(String symbol, String userZoneId) {
        return currencyPairService.getLatestCurrencyPairRate(symbol, userZoneId);
    }

    public List<CurrencyPairRateHistoryResponse> getCurrencyPairLast24hRateHistory(String symbol, String userZoneId) {
        return currencyPairService.getCurrencyPairLast24hRateHistory(symbol, userZoneId);
    }

    public List<CurrencyPairRateHistoryResponse> getAvailableCurrencyPairsLatestCurrencyRate(String userZoneId) {
        return currencyPairService.getAvailableCurrencyPairsLatestCurrencyRate(userZoneId);
    }
}
