package com.spark.models.model;

import lombok.Builder;

@Builder
public record CurrencyPairDTO(Long id, String symbol) {
}
