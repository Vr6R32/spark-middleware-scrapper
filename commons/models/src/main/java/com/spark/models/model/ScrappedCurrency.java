package com.spark.models.model;

import java.math.BigDecimal;

public record ScrappedCurrency(String symbol, BigDecimal lastPrice, Long timestamp) {
}
