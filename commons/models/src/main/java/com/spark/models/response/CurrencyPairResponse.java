package com.spark.models.response;

import com.spark.models.model.CurrencyPairDTO;

public record CurrencyPairResponse(String message, CurrencyPairDTO currencyPair) {
    public static final String DELETED_RESPONSE = "SUCCESSFULLY_DELETED_CURRENCY_PAIR";
    public static final String CREATED_RESPONSE = "SUCCESSFULLY_CREATED_CURRENCY_PAIR";
    public static final String EDITED_RESPONSE = "SUCCESSFULLY_EDITED_CURRENCY_PAIR";

}
