package com.spark.data.currency_pair_rate_history;

import com.spark.data.currency_pair.CurrencyPairFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
class CurrencyPairRateHistoryFacadeConfiguration {

    private final CurrencyPairRateHistoryRepository currencyPairRateHistoryRepository;
    private final CurrencyPairFacade currencyPairFacade;

    public CurrencyPairRateHistoryFacadeConfiguration(CurrencyPairRateHistoryRepository currencyPairRateHistoryRepository, @Lazy CurrencyPairFacade currencyPairFacade) {
        this.currencyPairRateHistoryRepository = currencyPairRateHistoryRepository;
        this.currencyPairFacade = currencyPairFacade;
    }

    @Bean
    public CurrencyPairRateHistoryFacade currencyPairRateHistoryFacade() {
        CurrencyPairRateHistoryService currencyPairRateHistoryService = new CurrencyPairRateHistoryService(currencyPairRateHistoryRepository,currencyPairFacade);
        return new CurrencyPairRateHistoryFacade(currencyPairRateHistoryService);
    }
}
