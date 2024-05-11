package com.spark.scrapper;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.spark.feign_client.CryptoDataServiceClient;
import com.spark.feign_client.WebSocketServiceClient;
import com.spark.models.model.BinanceCurrencyResponse;
import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.model.ScrappedCurrency;
import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ScrapperServiceTest {

    public static final String BTCUSDT = "BTCUSDT";
    public static final String LTCUSDT = "LTCUSDT";
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CryptoDataServiceClient cryptoDataServiceClient;

    @Mock
    private WebSocketServiceClient webSocketServiceClient;

    @Captor
    private ArgumentCaptor<ScrappedCurrencyUpdateRequest> captor;

    private ScrapperService scrapperService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scrapperService = new ScrapperService(restTemplate, webSocketServiceClient, cryptoDataServiceClient);
    }

    @Test
    void shouldFetchDataFromBinanceAndPushToUpdateHappyPath() {

        // Given

        CurrencyPairDTO currencyPairBTC = new CurrencyPairDTO(1L, BTCUSDT);
        CurrencyPairDTO currencyPairLTC = new CurrencyPairDTO(2L, LTCUSDT);
        Set<CurrencyPairDTO> currenciesToScrape = Set.of(currencyPairLTC, currencyPairBTC);

        BigDecimal btcLastPrice = BigDecimal.valueOf(62300, 421);
        BinanceCurrencyResponse binanceResponseBTC = BinanceCurrencyResponse.builder()
                .symbol(BTCUSDT)
                .lastPrice(btcLastPrice)
                .build();

        BigDecimal ltcLastPrice = BigDecimal.valueOf(250.75);
        BinanceCurrencyResponse binanceResponseLTC = BinanceCurrencyResponse.builder()
                .symbol(LTCUSDT)
                .lastPrice(ltcLastPrice)
                .build();

        AvailableCurrencyPairsResponse availableCurrencyPairsResponse = new AvailableCurrencyPairsResponse(currenciesToScrape);

        when(cryptoDataServiceClient.getAvailableCurrencies()).thenReturn(availableCurrencyPairsResponse);
        when(restTemplate.getForObject(anyString(), eq(BinanceCurrencyResponse.class)))
                .thenReturn(binanceResponseBTC)
                .thenReturn(binanceResponseLTC);

        // When

        scrapperService.scrapeBinanceApiMarketData();

        // Then

        verify(webSocketServiceClient, times(1)).pushScrappedDataForUpdateToWebSocketSessions(captor.capture());
        verify(cryptoDataServiceClient, times(1)).pushScrappedCurrencySetForUpdate(captor.capture());
        verify(cryptoDataServiceClient, times(1)).getAvailableCurrencies();
        verify(restTemplate, times(currenciesToScrape.size())).getForObject(anyString(), eq(BinanceCurrencyResponse.class));

        ScrappedCurrencyUpdateRequest request = captor.getValue();
        Set<ScrappedCurrency> capturedSet = request.scrappedCurrencySet();
        Set<ScrappedCurrency> expectedSet = new HashSet<>();

        expectedSet.add(new ScrappedCurrency(BTCUSDT, btcLastPrice, Instant.now().toEpochMilli()));
        expectedSet.add(new ScrappedCurrency(LTCUSDT, ltcLastPrice, Instant.now().toEpochMilli()));

        assertThat(capturedSet).usingElementComparatorIgnoringFields("timestamp").containsExactlyInAnyOrderElementsOf(expectedSet);
    }

    @Test
    void shouldLogWarnWhileUpdatingDataFromBinanceWhenThrowRestClientException() {
        // Given
        CurrencyPairDTO currencyPair = new CurrencyPairDTO(1L, BTCUSDT);
        Set<CurrencyPairDTO> currenciesToScrape = Collections.singleton(currencyPair);

        AvailableCurrencyPairsResponse availableCurrencyPairsResponse = new AvailableCurrencyPairsResponse(currenciesToScrape);

        when(cryptoDataServiceClient.getAvailableCurrencies()).thenReturn(availableCurrencyPairsResponse);
        when(restTemplate.getForObject(anyString(), eq(BinanceCurrencyResponse.class)))
                .thenThrow(new RestClientException("Mocked exception"));

        // When
        scrapperService.scrapeBinanceApiMarketData();

        // Then
        TestLogger testLogger = TestLoggerFactory.getTestLogger(ScrapperService.class);

        assertEquals(1, testLogger.getLoggingEvents().size());
        assertEquals("EXCEPTION OCCURRED WHILE SCRAPPING BINANCE SERVICE -> Mocked exception WITH PAYLOAD -> BTCUSDT ", testLogger.getLoggingEvents().get(0).getFormattedMessage());
    }

}
