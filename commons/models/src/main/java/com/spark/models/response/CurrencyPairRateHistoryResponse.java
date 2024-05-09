package com.spark.models.response;


import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record CurrencyPairRateHistoryResponse(String symbol, BigDecimal value, ZonedDateTime timestamp) {
}
