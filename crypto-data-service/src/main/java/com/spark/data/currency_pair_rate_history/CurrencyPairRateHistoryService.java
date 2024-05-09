package com.spark.data.currency_pair_rate_history;

import com.spark.data.currency_pair.CurrencyPairException;
import com.spark.data.currency_pair.CurrencyPairFacade;
import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.response.CurrencyPairRateHistoryResponse;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.spark.data.currency_pair_rate_history.CurrencyPairRateHistoryMapper.mapCurrencyPairRateHistoryToResponseDTO;

@RequiredArgsConstructor
class CurrencyPairRateHistoryService {

    public static final int ONE_DAY_IN_MILLIS = 86400;
    private final CurrencyPairRateHistoryRepository currencyPairRateHistoryRepository;
    private final CurrencyPairFacade currencyPairFacade;

    public CurrencyPairRateHistoryResponse getLatestCurrencyPairRate(String symbol, String userZoneId) {
        CurrencyPairRateHistory latestRateHistory = currencyPairRateHistoryRepository.findBySymbol(symbol).orElseThrow(CurrencyPairException::new);
        return mapCurrencyPairRateHistoryToResponseDTO(latestRateHistory,userZoneId);
    }

    public List<CurrencyPairRateHistoryResponse> getCurrencyPairLast24hRateHistory(String symbol, String userZoneId) {
        long twentyFourHoursWindowTimeMillis = Instant.now().minusSeconds(ONE_DAY_IN_MILLIS).toEpochMilli();
        List<CurrencyPairRateHistory> twentyFourHourHistoryList = currencyPairRateHistoryRepository.findBySymbolAndTimestampGreaterThanEqual(symbol, twentyFourHoursWindowTimeMillis);
        return twentyFourHourHistoryList.stream().map(e -> mapCurrencyPairRateHistoryToResponseDTO(e, userZoneId)).toList();
    }

    public List<CurrencyPairRateHistoryResponse> getAvailableCurrencyPairsLatestCurrencyRate(String userZoneId) {
        Set<CurrencyPairDTO> currencies = currencyPairFacade.getAvailableCurrencies().currencies();
        List<String> symbols = currencies.stream().map(CurrencyPairDTO::symbol).toList();
        List<CurrencyPairRateHistory> allCurrenciesLatestValue = currencyPairRateHistoryRepository.findAllCurrenciesLatestValue(symbols);
        return allCurrenciesLatestValue.stream().map(e -> mapCurrencyPairRateHistoryToResponseDTO(e, userZoneId)).toList();
    }
}
