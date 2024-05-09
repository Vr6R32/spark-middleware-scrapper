package com.spark.data.domain.currency_pair;

public class CurrencyPairException extends RuntimeException {

    public static final String SYMBOL_IS_NOT_AVAILABLE = "SYMBOL IS NOT AVAILABLE";
    public static final String SYMBOL_ALREADY_EXISTS = "SYMBOL ALREADY EXISTS";

    public CurrencyPairException(String message) {
        super(message);
    }

}
