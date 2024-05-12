package com.spark.websocket.domain.update_event;

import com.spark.models.model.ScrappedCurrency;
import com.spark.models.response.CurrencyPairSingleRateHistoryResponse;

import static com.spark.utils.DateTimeZoneFormatter.convertUtcToUserZonedDateTime;

public class ScrappedCurrencyMapper {

    private ScrappedCurrencyMapper() {
    }

    /**
     * Maps a ScrappedCurrency object to a CurrencyPairSingleRateHistoryResponse.
     * @param scrappedCurrency Currency data retrieved from an external source.
     * @param userZoneId Identifier of the user's time zone needed to convert UTC timestamp value
     * @return A CurrencyPairSingleRateHistoryResponse object with requested currency data.
     */

    public static CurrencyPairSingleRateHistoryResponse mapScrappedCurrencyToCurrencyPairSingleRateHistoryResponse(ScrappedCurrency scrappedCurrency, String userZoneId) {
        return new CurrencyPairSingleRateHistoryResponse(
                scrappedCurrency.symbol(),
                scrappedCurrency.lastPrice(),
                convertUtcToUserZonedDateTime(scrappedCurrency.timestamp(), userZoneId)
        );
    }
}