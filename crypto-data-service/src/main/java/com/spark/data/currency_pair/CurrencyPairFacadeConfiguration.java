package com.spark.data.currency_pair;

import com.spark.data.currency_pair_rate_history.CurrencyPairRateHistoryFacade;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class CurrencyPairFacadeConfiguration {

    private final CurrencyPairRateHistoryFacade currencyPairRateHistoryFacade;
    private final CurrencyPairRepository currencyPairRepository;
    private final EntityManager entityManager;


    @Bean
    CurrencyPairFacade currencyPairFacade() {
        CurrencyPairService currencyPairService = new CurrencyPairService(currencyPairRateHistoryFacade,currencyPairRepository, entityManager);
        return new CurrencyPairFacade(currencyPairService);
    }
}
