package com.spark.data.domain.currency_pair_rate_history;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@Table(name = "currency_pair_rate_history")
@AllArgsConstructor
@RequiredArgsConstructor
class CurrencyPairRateHistory {

    @Id
    private Long id;
    private String symbol;
    @Column(precision = 12, scale = 8, nullable = false)
    private BigDecimal value;
    private Long timestamp;

}
