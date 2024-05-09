package com.spark.data.currency_pair;

import com.spark.data.currency_pair_rate_history.CurrencyPairRateHistoryFacade;
import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.model.ScrappedCurrency;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairRateHistoryResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
class CurrencyPairService {


    private final CurrencyPairRateHistoryFacade currencyPairRateHistoryFacade;
    private final CurrencyPairRepository currencyPairRepository;
    private final EntityManager entityManager;

    public AvailableCurrencyPairsResponse getAvailableCurrencies() {

        Set<CurrencyPairDTO> currencyPairDTOs = currencyPairRepository.findAll()
                .stream()
                .map(CurrencyPairMapper::mapCurrencyPairToDTO)
                .collect(Collectors.toSet());

        return new AvailableCurrencyPairsResponse(currencyPairDTOs);
    }

    public void updateCurrencyPairRateHistory(Set<ScrappedCurrency> scrappedCurrencySet) {

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO currency_pair_rate_history (symbol, value, timestamp) VALUES ");

        Iterator<ScrappedCurrency> iterator = scrappedCurrencySet.iterator();
        while (iterator.hasNext()) {

            ScrappedCurrency scrappedCurrency = iterator.next();

            queryBuilder.append("('")
                    .append(scrappedCurrency.symbol()).append("', ")
                    .append(scrappedCurrency.lastPrice()).append(", ")
                    .append("'").append(scrappedCurrency.timestamp()).append("')");
            if (iterator.hasNext()) {
                queryBuilder.append(", ");
            }
        }

        try {
            entityManager.createNativeQuery(queryBuilder.toString()).executeUpdate();
        } catch (Exception e) {
            log.warn("DB EXCEPTION OCCURRED WHILE UPDATING CURRENCY PAIR RATE HISTORY -> {}" , e.getMessage());
        }
    }

    public CurrencyPairRateHistoryResponse getLatestCurrencyPairRate(String symbol, String userZoneId) {
        CurrencyPair currencyPair = currencyPairRepository.findBySymbol(symbol).orElseThrow(CurrencyPairException::new);
        return currencyPairRateHistoryFacade.getLatestCurrencyPairRate(currencyPair.getSymbol(),userZoneId);
    }

    public List<CurrencyPairRateHistoryResponse> getCurrencyPairLast24hRateHistory(String symbol, String userZoneId) {
        CurrencyPair currencyPair = currencyPairRepository.findBySymbol(symbol).orElseThrow(CurrencyPairException::new);
        return currencyPairRateHistoryFacade.getCurrencyPairLast24hRateHistory(currencyPair.getSymbol(),userZoneId);
    }

    public List<CurrencyPairRateHistoryResponse> getAvailableCurrencyPairsLatestCurrencyRate(String userZoneId) {
        return currencyPairRateHistoryFacade.getAvailableCurrencyPairsLatestCurrencyRate(userZoneId);
    }
}
