package com.spark.data.currency_pair;

public class CurrencyPairException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "SYMBOL IS NOT AVAILABLE";

    public CurrencyPairException() {
        super(DEFAULT_MESSAGE);
    }

}
