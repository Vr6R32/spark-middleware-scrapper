package com.spark.data.currency_pair_rate_history;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "currency_pair_rate_history")
class CurrencyPairRateHistory {

    @Id
    private Long id;
    private String symbol;
    @Column(precision = 12, scale = 8, nullable = false)
    private BigDecimal value;
    private Long timestamp;

}
