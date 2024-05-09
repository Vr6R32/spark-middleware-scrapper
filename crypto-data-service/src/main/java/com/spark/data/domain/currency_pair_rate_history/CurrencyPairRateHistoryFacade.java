package com.spark.data.domain.currency_pair_rate_history;

import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import com.spark.models.response.CurrencyPairChartRateHistoryResponse;
import com.spark.models.response.CurrencyPairSingleRateHistoryResponse;
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

    public CurrencyPairSingleRateHistoryResponse getLatestCurrencyPairRate(String symbol, String userZoneId) {
        return currencyPairRateHistoryService.getLatestCurrencyPairRate(symbol, userZoneId);
    }

    public CurrencyPairChartRateHistoryResponse getCurrencyPairLast24hRateHistory(String symbol, String userZoneId) {
        return currencyPairRateHistoryService.getCurrencyPairLast24hRateHistory(symbol, userZoneId);
    }

    public List<CurrencyPairSingleRateHistoryResponse> getAvailableCurrencyPairsLatestCurrencyRate(String userZoneId) {
        return currencyPairRateHistoryService.getAvailableCurrencyPairsLatestCurrencyRate(userZoneId);
    }
}
