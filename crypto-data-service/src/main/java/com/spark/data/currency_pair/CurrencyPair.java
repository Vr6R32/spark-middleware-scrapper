package com.spark.data.currency_pair;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "currency_pair")
class CurrencyPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;

}
