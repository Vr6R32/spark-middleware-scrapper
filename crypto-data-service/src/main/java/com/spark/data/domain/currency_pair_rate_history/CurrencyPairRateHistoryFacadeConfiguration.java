package com.spark.data.domain.currency_pair_rate_history;

import com.spark.data.domain.currency_pair.CurrencyPairFacade;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class CurrencyPairRateHistoryFacadeConfiguration {

    private final CurrencyPairRateHistoryRepository currencyPairRateHistoryRepository;
    private final CurrencyPairFacade currencyPairFacade;
    private final EntityManager entityManager;

    @Bean
    public CurrencyPairRateHistoryFacade currencyPairRateHistoryFacade() {
        CurrencyPairRateHistoryService currencyPairRateHistoryService = new CurrencyPairRateHistoryService(currencyPairRateHistoryRepository,currencyPairFacade,entityManager);
        return new CurrencyPairRateHistoryFacade(currencyPairRateHistoryService);
    }
}
