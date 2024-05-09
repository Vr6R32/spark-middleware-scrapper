package com.spark.data.domain.currency_pair_rate_history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface CurrencyPairRateHistoryRepository extends JpaRepository<CurrencyPairRateHistory,Long> {


    @Query("SELECT c FROM CurrencyPairRateHistory c WHERE c.symbol = :symbol AND c.timestamp = (SELECT MAX(cr.timestamp) FROM CurrencyPairRateHistory cr WHERE cr.symbol = :symbol)")
    Optional<CurrencyPairRateHistory> findLatestCurrencyPairRateBySymbol(@Param("symbol") String symbol);

    List<CurrencyPairRateHistory> findBySymbolAndTimestampGreaterThanEqual(String symbol, long twentyFourHoursWindowTimeMillis);

    @Query("SELECT c FROM CurrencyPairRateHistory c " +
            "WHERE c.symbol IN :symbols " +
            "AND c.timestamp = (" +
            "SELECT MAX(c2.timestamp) FROM CurrencyPairRateHistory c2 " +
            "WHERE c2.symbol = c.symbol" +
            ")")
    List<CurrencyPairRateHistory> findAllCurrenciesLatestValue(@Param("symbols") List<String> symbols);

}
