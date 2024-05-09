package com.spark.data.domain.currency_pair_rate_history;

import com.spark.models.model.ChartRateHistory;
import com.spark.models.response.CurrencyPairSingleRateHistoryResponse;

import static com.spark.data.domain.currency_pair_rate_history.DateTimeZoneFormatter.convertUtcToUserZonedDateTime;

class CurrencyPairRateHistoryMapper {

    private CurrencyPairRateHistoryMapper() {
    }

    public static CurrencyPairSingleRateHistoryResponse mapCurrencyPairRateHistoryToResponseDTO(CurrencyPairRateHistory currencyPairRateHistory, String userZoneId) {
        return new CurrencyPairSingleRateHistoryResponse(
                currencyPairRateHistory.getSymbol(),
                currencyPairRateHistory.getValue(),
                convertUtcToUserZonedDateTime(currencyPairRateHistory.getTimestamp(), userZoneId));
    }

    public static ChartRateHistory mapCurrencyPairRateHistoryToChartResponse(CurrencyPairRateHistory currencyPairRateHistory, String userZoneId) {
        return new ChartRateHistory(
                currencyPairRateHistory.getValue(),
                convertUtcToUserZonedDateTime(currencyPairRateHistory.getTimestamp(), userZoneId));
    }
}
