package com.spark.models.response;

import com.spark.models.model.ChartRateHistory;

import java.util.List;

public record CurrencyPairChartRateHistoryResponse(String symbol, List<ChartRateHistory> rateHistory) {
}
