package com.spark.data.currency_pair_rate_history;

import com.spark.models.response.CurrencyPairRateHistoryResponse;

import static com.spark.data.currency_pair_rate_history.DateTimeZoneFormatter.convertUtcToZonedDateTime;

class CurrencyPairRateHistoryMapper {

    private CurrencyPairRateHistoryMapper() {
    }

    public static CurrencyPairRateHistoryResponse mapCurrencyPairRateHistoryToResponseDTO(CurrencyPairRateHistory currencyPairRateHistory, String userZoneId) {
        return new CurrencyPairRateHistoryResponse(
                currencyPairRateHistory.getSymbol(),
                currencyPairRateHistory.getValue(),
                convertUtcToZonedDateTime(currencyPairRateHistory.getTimestamp(), userZoneId));
    }
}
