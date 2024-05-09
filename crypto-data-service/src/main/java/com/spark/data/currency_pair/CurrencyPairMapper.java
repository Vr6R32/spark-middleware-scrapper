package com.spark.data.currency_pair;

import com.spark.models.model.CurrencyPairDTO;

class CurrencyPairMapper {

    private CurrencyPairMapper() {
    }

    public static CurrencyPairDTO mapCurrencyPairToDTO(CurrencyPair currency) {
        return new CurrencyPairDTO(currency.getId(), currency.getSymbol());
    }

}
