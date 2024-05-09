package com.spark.models.response;

import com.spark.models.model.CurrencyPairDTO;

import java.util.Set;

public record AvailableCurrencyPairsResponse(Set<CurrencyPairDTO> currencies) {
}
