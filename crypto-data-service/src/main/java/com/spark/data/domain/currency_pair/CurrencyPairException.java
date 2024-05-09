package com.spark.data.domain.currency_pair;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CurrencyPairException extends RuntimeException {

    public static final String SYMBOL_IS_NOT_AVAILABLE = "SYMBOL IS NOT AVAILABLE";
    public static final String SYMBOL_ALREADY_EXISTS = "SYMBOL ALREADY EXISTS";
    private final String symbol;

    public CurrencyPairException(String message, String symbol) {
        super(message);
        this.symbol = symbol;
    }

}
