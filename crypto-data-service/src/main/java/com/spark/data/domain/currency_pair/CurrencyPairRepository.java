package com.spark.data.domain.currency_pair;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface CurrencyPairRepository extends JpaRepository<CurrencyPair,Long> {
    Optional<CurrencyPair> findBySymbol(String symbol);
}
