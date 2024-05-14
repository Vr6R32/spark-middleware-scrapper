package com.spark.data.domain.currency_pair_rate_history;

import com.spark.data.domain.currency_pair.CurrencyPairException;
import com.spark.data.domain.currency_pair.CurrencyPairFacade;
import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.model.ScrappedCurrency;
import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairChartRateHistoryResponse;
import com.spark.models.response.CurrencyPairSingleRateHistoryResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static com.spark.data.domain.currency_pair_rate_history.TimeWindowConverter.ONE_DAY_IN_SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyPairRateHistoryServiceTest {

    public static final String USER_ZONE_ID = "Europe/Warsaw";
    public static final String BTCUSDT = "BTCUSDT";
    public static final String ETHUSDT = "ETHUSDT";
    public static final String LTCUSDT = "LTCUSDT";

    @Mock
    private CurrencyPairRateHistoryRepository currencyPairRateHistoryRepository;

    @Mock
    private CurrencyPairFacade currencyPairFacade;

    @Mock
    private EntityManager entityManager;

    private CurrencyPairRateHistoryService rateHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rateHistoryService = new CurrencyPairRateHistoryService(currencyPairRateHistoryRepository, currencyPairFacade, entityManager);
    }

    @Test
    void successfullyBuildDynamicSqlInsertQuery() {

        // Given
        ScrappedCurrency scrappedBtc = new ScrappedCurrency(BTCUSDT, BigDecimal.valueOf(50000), Instant.now().toEpochMilli());
        ScrappedCurrency scrappedEth = new ScrappedCurrency(ETHUSDT, BigDecimal.valueOf(3000), Instant.now().toEpochMilli());
        Set<ScrappedCurrency> scrappedCurrencies = Set.of(scrappedBtc, scrappedEth);

        Comparator<ScrappedCurrency> currencyComparator = Comparator.comparing(ScrappedCurrency::symbol);
        Set<ScrappedCurrency> sortedCurrencies = new TreeSet<>(currencyComparator);
        sortedCurrencies.addAll(scrappedCurrencies);

        ScrappedCurrencyUpdateRequest request = new ScrappedCurrencyUpdateRequest(sortedCurrencies, 2);

        // When
        rateHistoryService.updateCurrencyPairRateHistory(request);

        // Then

        verify(entityManager, times(1)).createNativeQuery(anyString());


        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createNativeQuery(queryCaptor.capture());
        String generatedSqlQuery = queryCaptor.getValue();

        String expectedSqlQuery = String.format(
                "INSERT INTO currency_pair_rate_history (symbol, value, timestamp) VALUES ('%s', %s, '%s'), ('%s', %s, '%s')",
                scrappedBtc.symbol(), scrappedBtc.lastPrice(), scrappedBtc.timestamp(),
                scrappedEth.symbol(), scrappedEth.lastPrice(), scrappedEth.timestamp()
        );

        assertEquals(generatedSqlQuery, expectedSqlQuery);
    }

    @Test
    void getLatestCurrencyPairRateHappyPath() {

        // GIVEN
        long timestamp = Instant.now().toEpochMilli();
        BigDecimal value = BigDecimal.valueOf(56231.1234);
        CurrencyPairRateHistory rateHistory = CurrencyPairRateHistory.builder()
                .symbol(BTCUSDT)
                .value(value)
                .timestamp(timestamp)
                .build();

        when(currencyPairRateHistoryRepository.findLatestCurrencyPairRateBySymbol(BTCUSDT)).thenReturn(Optional.of(rateHistory));

        // WHEN
        CurrencyPairSingleRateHistoryResponse response = rateHistoryService.getLatestCurrencyPairRate(BTCUSDT, USER_ZONE_ID);

        // THEN
        assertEquals(BTCUSDT, response.symbol());
        assertNotNull(response.value());
        assertEquals(value, response.value());
    }

    @Test
    void getLatestCurrencyPairRateFailsThrowsExceptionWhenNonExistingSymbol() {

        // GIVEN
        when(currencyPairRateHistoryRepository.findLatestCurrencyPairRateBySymbol(BTCUSDT)).thenReturn(Optional.empty());

        // WHEN & THEN
        CurrencyPairException exception =
                assertThrows(CurrencyPairException.class, () -> rateHistoryService.getLatestCurrencyPairRate(BTCUSDT, USER_ZONE_ID));

        assertEquals(CurrencyPairException.SYMBOL_IS_NOT_AVAILABLE, exception.getMessage());
        assertEquals(BTCUSDT, exception.getSymbol());
    }


    @Test
    void getCurrencyPairLast24hRateHistoryHappyPath() {

        // GIVEN
        long timestamp = Instant.now().minusSeconds(ONE_DAY_IN_SECONDS).plusSeconds(10).toEpochMilli();
        long timestamp2 = Instant.now().minusSeconds(ONE_DAY_IN_SECONDS).plusSeconds(20).toEpochMilli();

        BigDecimal value1 = BigDecimal.valueOf(56231.1234);
        BigDecimal value2 = BigDecimal.valueOf(56831.1234);

        CurrencyPairRateHistory rateHistory1 = CurrencyPairRateHistory.builder()
                .symbol(BTCUSDT)
                .value(value1)
                .timestamp(timestamp)
                .build();

        CurrencyPairRateHistory rateHistory2 = CurrencyPairRateHistory.builder()
                .symbol(BTCUSDT)
                .value(value2)
                .timestamp(timestamp2)
                .build();


        List<CurrencyPairRateHistory> historyList = List.of(rateHistory1, rateHistory2);
        when(currencyPairRateHistoryRepository.findBySymbolAndTimestampGreaterThanEqual(anyString(), anyLong())).thenReturn(historyList);

        // When
        CurrencyPairChartRateHistoryResponse response = rateHistoryService.getCurrencyPairLast24hRateHistory(BTCUSDT, USER_ZONE_ID);

        // Then
        assertEquals(BTCUSDT, response.symbol());
        assertEquals("24h", response.timeWindow());
        assertFalse(response.rateHistory().isEmpty());
        assertEquals(value1, response.rateHistory().get(0).value());
        assertEquals(value2, response.rateHistory().get(1).value());
    }

    @Test
    void getCurrencyPairLast24hRateHistoryThrowsExceptionWhenListEmpty() {

        // GIVEN
        // WHEN
        CurrencyPairException exception = assertThrows(CurrencyPairException.class,
                () -> rateHistoryService.getCurrencyPairLast24hRateHistory(BTCUSDT, USER_ZONE_ID));

        // THEN
        assertEquals(CurrencyPairException.SYMBOL_IS_NOT_AVAILABLE, exception.getMessage());
        assertEquals(BTCUSDT, exception.getSymbol());
    }


    @Test
    void getCurrencyPairLast24hRateHistoryThrowsExceptionWhenNonExistingSymbol() {

        // GIVEN
        when(currencyPairRateHistoryRepository.findBySymbolAndTimestampGreaterThanEqual(BTCUSDT, Instant.now().toEpochMilli())).thenReturn(Collections.emptyList());

        // WHEN
        // THEN
        CurrencyPairException exception = assertThrows(CurrencyPairException.class,
                () -> rateHistoryService.getCurrencyPairLast24hRateHistory(BTCUSDT, USER_ZONE_ID));
        assertEquals(CurrencyPairException.SYMBOL_IS_NOT_AVAILABLE, exception.getMessage());
        assertEquals(BTCUSDT, exception.getSymbol());
    }

    @Test
    void getAvailableCurrencyPairsLatestCurrencyRateHappyPath() {

        // GIVEN
        Set<CurrencyPairDTO> currencies = Set.of(
                new CurrencyPairDTO(1L, BTCUSDT),
                new CurrencyPairDTO(2L, ETHUSDT),
                new CurrencyPairDTO(3L, LTCUSDT)
        );

        BigDecimal btcValue = BigDecimal.valueOf(50000);
        BigDecimal ethValue = BigDecimal.valueOf(3000);
        BigDecimal ltcValue = BigDecimal.valueOf(150);
        List<CurrencyPairRateHistory> rateHistories = List.of(
                CurrencyPairRateHistory.builder()
                        .symbol(BTCUSDT)
                        .value(btcValue)
                        .timestamp(Instant.now().toEpochMilli())
                        .build(),
                CurrencyPairRateHistory.builder()
                        .symbol(ETHUSDT)
                        .value(ethValue)
                        .timestamp(Instant.now().toEpochMilli())
                        .build(),
                CurrencyPairRateHistory.builder()
                        .symbol(LTCUSDT)
                        .value(ltcValue)
                        .timestamp(Instant.now().toEpochMilli())
                        .build()
        );
        when(currencyPairFacade.getAvailableCurrencies()).thenReturn(new AvailableCurrencyPairsResponse(currencies));
        when(currencyPairRateHistoryRepository.findAllCurrenciesLatestValue(anyList())).thenReturn(rateHistories);

        // WHEN
        List<CurrencyPairSingleRateHistoryResponse> responses = rateHistoryService.getAvailableCurrencyPairsLatestCurrencyRate(USER_ZONE_ID);

        // THEN
        assertEquals(3, responses.size());

        assertEquals(BTCUSDT, responses.get(0).symbol());
        assertEquals(btcValue, responses.get(0).value());

        assertEquals(ETHUSDT, responses.get(1).symbol());
        assertEquals(ethValue, responses.get(1).value());

        assertEquals(LTCUSDT, responses.get(2).symbol());
        assertEquals(ltcValue, responses.get(2).value());
    }
}
