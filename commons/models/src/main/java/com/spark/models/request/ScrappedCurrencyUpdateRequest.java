package com.spark.models.request;

import com.spark.models.model.ScrappedCurrency;

import java.util.Set;

public record ScrappedCurrencyUpdateRequest(Set<ScrappedCurrency> scrappedCurrencySet, int availableCurrencyPairs) {
}
