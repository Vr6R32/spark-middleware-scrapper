package com.spark.models.response;


import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record CurrencyPairSingleRateHistoryResponse(String symbol, BigDecimal value, ZonedDateTime timestamp) {
}
