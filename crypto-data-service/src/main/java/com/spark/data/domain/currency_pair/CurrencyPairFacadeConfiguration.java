package com.spark.data.domain.currency_pair;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class CurrencyPairFacadeConfiguration {

    private final CurrencyPairRepository currencyPairRepository;

    @Bean
    CurrencyPairFacade currencyPairFacade() {
        CurrencyPairService currencyPairService = new CurrencyPairService(currencyPairRepository);
        return new CurrencyPairFacade(currencyPairService);
    }
}
