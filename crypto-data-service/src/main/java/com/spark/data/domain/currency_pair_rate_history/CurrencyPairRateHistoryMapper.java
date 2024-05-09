package com.spark.data.domain.currency_pair_rate_history;

import com.spark.models.response.CurrencyPairRateHistoryResponse;

import static com.spark.data.domain.currency_pair_rate_history.DateTimeZoneFormatter.convertUtcToUserZonedDateTime;

class CurrencyPairRateHistoryMapper {

    private CurrencyPairRateHistoryMapper() {
    }

    public static CurrencyPairRateHistoryResponse mapCurrencyPairRateHistoryToResponseDTO(CurrencyPairRateHistory currencyPairRateHistory, String userZoneId) {
        return new CurrencyPairRateHistoryResponse(
                currencyPairRateHistory.getSymbol(),
                currencyPairRateHistory.getValue(),
                convertUtcToUserZonedDateTime(currencyPairRateHistory.getTimestamp(), userZoneId));
    }
}
