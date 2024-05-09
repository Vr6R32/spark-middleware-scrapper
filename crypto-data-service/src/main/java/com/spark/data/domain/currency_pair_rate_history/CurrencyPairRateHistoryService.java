package com.spark.data.domain.currency_pair_rate_history;

import com.spark.data.domain.currency_pair.CurrencyPairException;
import com.spark.data.domain.currency_pair.CurrencyPairFacade;
import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import com.spark.models.response.CurrencyPairRateHistoryResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.spark.data.domain.currency_pair.CurrencyPairException.SYMBOL_IS_NOT_AVAILABLE;

@Slf4j
@RequiredArgsConstructor
class CurrencyPairRateHistoryService {

    public static final int ONE_DAY_IN_MILLIS = 86400;
    private final CurrencyPairRateHistoryRepository currencyPairRateHistoryRepository;
    private final CurrencyPairFacade currencyPairFacade;
    private final EntityManager entityManager;

    /**
     * Updates the currency pair rate history based on scrapped currency data.
     *
     * @param request The request containing scrapped currency data and count of available currency pairs.
     */
    public void updateCurrencyPairRateHistory(ScrappedCurrencyUpdateRequest request) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO currency_pair_rate_history (symbol, value, timestamp) VALUES ");

        request.scrappedCurrencySet().forEach(scrappedCurrency ->
                queryBuilder.append("('")
                .append(scrappedCurrency.symbol()).append("', ")
                .append(scrappedCurrency.lastPrice()).append(", ")
                .append("'").append(scrappedCurrency.timestamp()).append("'), "));

        String sqlQuery = StringUtils.removeEnd(queryBuilder.toString(), ", ");

        try {
            entityManager.createNativeQuery(sqlQuery).executeUpdate();
            log.info("SUCCESSFULLY UPDATED [{}] OF [{}] AVAILABLE CURRENCY PAIRS RATE HISTORY",
                    request.scrappedCurrencySet().size(), request.availableCurrencyPairs());
        } catch (Exception e) {
            log.warn("DB EXCEPTION OCCURRED WHILE UPDATING CURRENCY PAIR RATE HISTORY -> {}", e.getMessage());
        }
    }

    /**
     * Retrieves the latest rate history for a specific currency pair symbol.
     *
     * @param symbol    The symbol of the currency pair.
     * @param userZoneId The user's time zone identifier.
     * @return The response containing the latest currency pair rate history.
     * @throws CurrencyPairException if the specified currency pair symbol is not available.
     */
    public CurrencyPairRateHistoryResponse getLatestCurrencyPairRate(String symbol, String userZoneId) {
        CurrencyPairRateHistory latestRateHistory = currencyPairRateHistoryRepository.findBySymbol(symbol)
                .orElseThrow(() -> new CurrencyPairException(SYMBOL_IS_NOT_AVAILABLE));
        return CurrencyPairRateHistoryMapper.mapCurrencyPairRateHistoryToResponseDTO(latestRateHistory, userZoneId);
    }

    /**
     * Retrieves the rate history for a specific currency pair symbol within the last 24 hours.
     *
     * @param symbol    The symbol of the currency pair.
     * @param userZoneId The user's time zone identifier.
     * @return The list of currency pair rate history within the last 24 hours.
     * @throws CurrencyPairException if the specified currency pair symbol is not available.
     */
    public List<CurrencyPairRateHistoryResponse> getCurrencyPairLast24hRateHistory(String symbol, String userZoneId) {
        long twentyFourHoursWindowTimeMillis = Instant.now().minusSeconds(ONE_DAY_IN_MILLIS).toEpochMilli();
        List<CurrencyPairRateHistory> twentyFourHourHistoryList = currencyPairRateHistoryRepository.findBySymbolAndTimestampGreaterThanEqual(symbol, twentyFourHoursWindowTimeMillis);
        return twentyFourHourHistoryList.stream()
                .map(e -> CurrencyPairRateHistoryMapper.mapCurrencyPairRateHistoryToResponseDTO(e, userZoneId))
                .toList();
    }

    /**
     * Retrieves the latest currency rate history for all available currency pairs.
     *
     * @param userZoneId The user's time zone identifier.
     * @return The list of latest currency pair rate history for all available currency pairs.
     */
    public List<CurrencyPairRateHistoryResponse> getAvailableCurrencyPairsLatestCurrencyRate(String userZoneId) {
        Set<CurrencyPairDTO> currencies = currencyPairFacade.getAvailableCurrencies().currencies();
        List<String> symbols = currencies.stream().map(CurrencyPairDTO::symbol).toList();
        List<CurrencyPairRateHistory> allCurrenciesLatestValue = currencyPairRateHistoryRepository.findAllCurrenciesLatestValue(symbols);
        return allCurrenciesLatestValue.stream()
                .map(e -> CurrencyPairRateHistoryMapper.mapCurrencyPairRateHistoryToResponseDTO(e, userZoneId))
                .toList();
    }
}
