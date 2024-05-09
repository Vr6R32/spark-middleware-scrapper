package com.spark.models.request;

import com.spark.models.model.CurrencyPairDTO;

public record CurrencyPairUpdateRequest(String symbol, CurrencyPairDTO currencyPair) {
}
