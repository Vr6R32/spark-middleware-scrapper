package com.spark.data.domain.currency_pair_rate_history;

import com.spark.data.domain.currency_pair.CurrencyPairException;
import com.spark.data.domain.currency_pair.CurrencyPairFacade;
import com.spark.models.model.ChartRateHistory;
import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import com.spark.models.response.CurrencyPairChartRateHistoryResponse;
import com.spark.models.response.CurrencyPairSingleRateHistoryResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.spark.data.domain.currency_pair.CurrencyPairException.SYMBOL_IS_NOT_AVAILABLE;
import static com.spark.data.domain.currency_pair_rate_history.CurrencyPairRateHistoryMapper.mapCurrencyPairRateHistoryToChartResponse;
import static com.spark.data.domain.currency_pair_rate_history.CurrencyPairRateHistoryMapper.mapCurrencyPairRateHistoryToResponseDTO;
import static com.spark.data.domain.currency_pair_rate_history.TimeWindowConverter.*;

@Slf4j
@RequiredArgsConstructor
class CurrencyPairRateHistoryService {

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
    public CurrencyPairSingleRateHistoryResponse getLatestCurrencyPairRate(String symbol, String userZoneId) {
        CurrencyPairRateHistory latestRateHistory = currencyPairRateHistoryRepository.findLatestCurrencyPairRateBySymbol(symbol)
                .orElseThrow(() -> new CurrencyPairException(SYMBOL_IS_NOT_AVAILABLE, symbol));
        return mapCurrencyPairRateHistoryToResponseDTO(latestRateHistory, userZoneId);
    }

    /**
     * Retrieves the rate history for a specific currency pair symbol within the last 24 hours.
     *
     * @param symbol    The symbol of the currency pair.
     * @param userZoneId The user's time zone identifier.
     * @return The specified response object containing list of currency pair rate history within the last 24 hours.
     * @throws CurrencyPairException if the specified currency pair symbol is not available.
     */
    public CurrencyPairChartRateHistoryResponse getCurrencyPairLast24hRateHistory(String symbol, String userZoneId) {

        long twentyFourHoursWindowTimeMillis = Instant.now().minusSeconds(ONE_DAY_IN_SECONDS).toEpochMilli();
        List<CurrencyPairRateHistory> twentyFourHourHistoryList = currencyPairRateHistoryRepository.findBySymbolAndTimestampGreaterThanEqual(symbol, twentyFourHoursWindowTimeMillis);
        List<ChartRateHistory> chartRateHistoryList = twentyFourHourHistoryList.stream().map(e -> mapCurrencyPairRateHistoryToChartResponse(e, userZoneId)).toList();
        if(chartRateHistoryList.isEmpty()) throw new CurrencyPairException(SYMBOL_IS_NOT_AVAILABLE, symbol);
        return new CurrencyPairChartRateHistoryResponse(symbol, convertSecondsToTimeWindowFormat(ONE_DAY_IN_SECONDS), chartRateHistoryList);

    }

    /**
     * Retrieves the latest currency rate history for all available currency pairs.
     *
     * @param userZoneId The user's time zone identifier.
     * @return The list of latest currency pair rate history for all available currency pairs.
     */
    public List<CurrencyPairSingleRateHistoryResponse> getAvailableCurrencyPairsLatestCurrencyRate(String userZoneId) {

        Set<CurrencyPairDTO> currencies = currencyPairFacade.getAvailableCurrencies().currencies();
        List<String> symbols = currencies.stream().map(CurrencyPairDTO::symbol).toList();
        List<CurrencyPairRateHistory> allCurrenciesLatestValue = currencyPairRateHistoryRepository.findAllCurrenciesLatestValue(symbols);

        return allCurrenciesLatestValue.stream()
                .map(e -> mapCurrencyPairRateHistoryToResponseDTO(e, userZoneId))
                .toList();
    }
}
