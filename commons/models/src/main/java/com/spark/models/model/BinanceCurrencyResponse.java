package com.spark.models.model;


import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BinanceCurrencyResponse(
        String symbol,
        String priceChange,
        String priceChangePercent,
        String weightedAvgPrice,
        BigDecimal openPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        BigDecimal lastPrice,
        String volume,
        String quoteVolume,
        long openTime,
        long closeTime,
        long firstId,
        long lastId,
        long count
) {
}