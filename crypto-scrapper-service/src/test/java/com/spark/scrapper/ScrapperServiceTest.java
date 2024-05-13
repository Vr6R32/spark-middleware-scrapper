package com.spark.scrapper;

import com.spark.feign.client.CryptoDataServiceClient;
import com.spark.feign.client.WebSocketServiceClient;
import com.spark.models.model.BinanceCurrencyResponse;
import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.model.ScrappedCurrency;
import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import feign.Request;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
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
        ReflectionTestUtils.setField(scrapperService, "retryInterval", 300);
        ReflectionTestUtils.setField(scrapperService, "retryAttempts", 3);
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

        scrapperService.asynchronouslyScrapeBinanceApiMarketDataAndPushDataUpdateRequest();

        // Then

        verify(webSocketServiceClient, times(1)).pushScrappedDataForUpdateToWebSocketSessions(captor.capture());
        verify(cryptoDataServiceClient, times(1)).pushScrappedCurrencySetForDataServiceUpdate(captor.capture());
        verify(cryptoDataServiceClient, times(1)).getAvailableCurrencies();
        verify(restTemplate, times(currenciesToScrape.size())).getForObject(anyString(), eq(BinanceCurrencyResponse.class));

        ScrappedCurrencyUpdateRequest capturedRequest = captor.getValue();
        Set<ScrappedCurrency> capturedSet = capturedRequest.scrappedCurrencySet();
        Set<ScrappedCurrency> expectedSet = Set.of(new ScrappedCurrency(BTCUSDT, btcLastPrice, Instant.now().toEpochMilli()), (new ScrappedCurrency(LTCUSDT, ltcLastPrice, Instant.now().toEpochMilli())));

        assertThat(capturedSet)
                .usingRecursiveComparison()
                .ignoringFields("timestamp")
                .ignoringCollectionOrder()
                .isEqualTo(expectedSet);
    }

    @Test
    void shouldRetryOnFailureAndEventuallySucceedWhenPushUpdateToWebSocket() {
        // Given
        CurrencyPairDTO currencyPair = new CurrencyPairDTO(1L, BTCUSDT);
        Set<CurrencyPairDTO> currenciesToScrape = Set.of(currencyPair);
        AvailableCurrencyPairsResponse availableCurrencyPairsResponse = new AvailableCurrencyPairsResponse(currenciesToScrape);

        BigDecimal btcLastPrice = BigDecimal.valueOf(62300, 421);
        BinanceCurrencyResponse binanceResponseBTC = BinanceCurrencyResponse.builder()
                .symbol(BTCUSDT)
                .lastPrice(btcLastPrice)
                .build();

        when(cryptoDataServiceClient.getAvailableCurrencies()).thenReturn(availableCurrencyPairsResponse);
        when(restTemplate.getForObject(anyString(), eq(BinanceCurrencyResponse.class)))
                .thenThrow(new RestClientException("Binance Api Exception"))
                .thenReturn(binanceResponseBTC);

        // When
        scrapperService.asynchronouslyScrapeBinanceApiMarketDataAndPushDataUpdateRequest();

        // Then
        verify(restTemplate, times(2)).getForObject(anyString(), eq(BinanceCurrencyResponse.class));

        ScrappedCurrencyUpdateRequest expectedScrappedCurrencyRequest = new ScrappedCurrencyUpdateRequest(Set.of(new ScrappedCurrency(BTCUSDT, btcLastPrice, 1231231231231L)), availableCurrencyPairsResponse.currencies().size());

        verify(webSocketServiceClient, times(1)).pushScrappedDataForUpdateToWebSocketSessions(
                argThat(request ->
                        request.availableCurrencyPairs() == expectedScrappedCurrencyRequest.availableCurrencyPairs() &&
                                request.scrappedCurrencySet().size() == expectedScrappedCurrencyRequest.scrappedCurrencySet().size() &&
                                request.scrappedCurrencySet().stream().allMatch(sc ->
                                        sc.symbol().equals(BTCUSDT) && sc.lastPrice().compareTo(btcLastPrice) == 0)
                )
        );

//        verify(webSocketServiceClient, times(1)).pushScrappedDataForUpdateToWebSocketSessions(captor.capture());
//
//        ScrappedCurrencyUpdateRequest request = captor.getValue();
//
//        assertThat(expectedScrappedCurrencyRequest)
//                .usingRecursiveComparison()
//                .ignoringFields("timeStamp")
//                .ignoringCollectionOrder()
//                .isEqualTo(request);

    }


    @Test
    void shouldFailAfterMaxRetriesWhenFetchAvailableCurrencies() {
        // Given
        when(cryptoDataServiceClient.getAvailableCurrencies())
                .thenThrow(new RetryableException(503, "Service Unavailable", Request.HttpMethod.GET, 0L, Request.create(Request.HttpMethod.GET, "http://example.com", Collections.emptyMap(), null, null, null)));

        // When
        scrapperService.asynchronouslyScrapeBinanceApiMarketDataAndPushDataUpdateRequest();

        // Then
        Object retryAttemptsObject = ReflectionTestUtils.getField(scrapperService, "retryAttempts");
        assert retryAttemptsObject != null;
        int retryAttempts = (int) retryAttemptsObject;
        verify(cryptoDataServiceClient, times(retryAttempts)).getAvailableCurrencies();
        verify(restTemplate, never()).getForObject(anyString(), eq(BinanceCurrencyResponse.class));
        verify(webSocketServiceClient, never()).pushScrappedDataForUpdateToWebSocketSessions(any());
    }

    @Test
    void shouldRetryWebSocketPushMethodAndHandleFailure() throws Exception {
        // Given
        ArgumentCaptor<ScrappedCurrencyUpdateRequest> requestCaptor = ArgumentCaptor.forClass(ScrappedCurrencyUpdateRequest.class);

        BigDecimal btcLastPrice = BigDecimal.valueOf(62300, 421);

        doThrow(new RuntimeException("Temporary Error"))
                .doNothing()
                .when(webSocketServiceClient).pushScrappedDataForUpdateToWebSocketSessions(any(ScrappedCurrencyUpdateRequest.class));

        ScrappedCurrencyUpdateRequest dummyRequest = new ScrappedCurrencyUpdateRequest(Set.of(new ScrappedCurrency("BTCUSDT", btcLastPrice, 234523452323L)), 1);

        // When
        CompletableFuture<Void> testFuture = scrapperService.executeWithRetryVoidAsynchronous(consumer -> {
            webSocketServiceClient.pushScrappedDataForUpdateToWebSocketSessions(dummyRequest);
        }, "WebSocket Push");
        testFuture.join();

        // Then
        verify(webSocketServiceClient, times(2)).pushScrappedDataForUpdateToWebSocketSessions(requestCaptor.capture());
        ScrappedCurrencyUpdateRequest capturedRequest = requestCaptor.getValue();
        Set<ScrappedCurrency> capturedSet = capturedRequest.scrappedCurrencySet();

        assertThat(capturedSet).hasSize(1);
        capturedSet.forEach(scrappedCurrency -> {
            assertThat(scrappedCurrency.symbol()).isEqualTo("BTCUSDT");
            assertThat(scrappedCurrency.lastPrice()).isEqualByComparingTo(btcLastPrice);
        });
    }
}
