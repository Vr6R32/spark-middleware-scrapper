package com.spark.data.domain.currency_pair_rate_history;

import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import com.spark.models.response.CurrencyPairRateHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class CurrencyPairRateHistoryFacade {


    private final CurrencyPairRateHistoryService currencyPairRateHistoryService;

    @Transactional
    public void updateCurrencyPairRateHistory(ScrappedCurrencyUpdateRequest request) {
        currencyPairRateHistoryService.updateCurrencyPairRateHistory(request);
    }

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
