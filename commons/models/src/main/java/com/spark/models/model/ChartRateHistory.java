package com.spark.models.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ChartRateHistory(BigDecimal value, ZonedDateTime timestamp) {
}
