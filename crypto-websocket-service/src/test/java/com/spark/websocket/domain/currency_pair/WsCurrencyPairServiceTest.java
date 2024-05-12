package com.spark.websocket.domain.currency_pair;

import static com.spark.websocket.infrastructure.model.MessageEventType.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;

import static org.assertj.core.api.Assertions.assertThat;
import com.spark.feign.client.CryptoDataServiceClient;
import com.spark.models.model.ChartRateHistory;
import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairChartRateHistoryResponse;
import com.spark.models.response.CurrencyPairSingleRateHistoryResponse;
import com.spark.websocket.infrastructure.model.WebSocketEventMessageResponse;
import com.spark.websocket.infrastructure.session.WebSocketSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

class WsCurrencyPairServiceTest {

    public static final String SESSION_ID = "session1";
    public static final String WS_SESSION_URL_PATH = "/ws/session/";
    public static final String USER_TIME_ZONE = "Europe/Warsaw";
    public static final ZonedDateTime TIMESTAMP = ZonedDateTime.parse("2024-05-12T16:42:36.545+02:00");

    @Mock
    private CryptoDataServiceClient cryptoDataServiceClient;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private WebSocketSessionManager sessionManager;


    private WsCurrencyPairService wsCurrencyPairService;

    @Captor
    ArgumentCaptor<WebSocketEventMessageResponse> argumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        wsCurrencyPairService = new WsCurrencyPairService(cryptoDataServiceClient, messagingTemplate, sessionManager);
    }

    @Test
    void shouldFetchAndSendAvailableCurrenciesEventToSession() {

        // Given
        CurrencyPairDTO btcusdt = CurrencyPairDTO.builder().id(1L).symbol("BTCUSDT").build();
        CurrencyPairDTO ethusdt = CurrencyPairDTO.builder().id(2L).symbol("ETHUSDT").build();
        CurrencyPairDTO ltcusdt = CurrencyPairDTO.builder().id(3L).symbol("LTCUSDT").build();
        Set<CurrencyPairDTO> currencyPairDTOSet = Set.of(btcusdt, ethusdt, ltcusdt);

        AvailableCurrencyPairsResponse response = new AvailableCurrencyPairsResponse(currencyPairDTOSet);

        WebSocketEventMessageResponse expectedResponse = new WebSocketEventMessageResponse(AVAILABLE_CURRENCIES,response);

        when(cryptoDataServiceClient.getAvailableCurrencies()).thenReturn(response);

        // When
        wsCurrencyPairService.fetchAndSendAvailableCurrenciesToSession(SESSION_ID);

        // Then

        verify(cryptoDataServiceClient,times(1)).getAvailableCurrencies();
        verify(messagingTemplate).convertAndSend(eq(WS_SESSION_URL_PATH + SESSION_ID), argumentCaptor.capture());

        WebSocketEventMessageResponse capturedResponse = argumentCaptor.getValue();
//
        assertThat(capturedResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

    }

    @Test
    void shouldFetchAndSendLatestCurrencyPairRateEventToSession() {
        // Given
        String symbol = "BTCUSDT";
        BigDecimal btcValue = BigDecimal.valueOf(60314.241);

        CurrencyPairSingleRateHistoryResponse dataClientMockedResponse = new CurrencyPairSingleRateHistoryResponse(symbol,btcValue, TIMESTAMP);

        WebSocketEventMessageResponse expectedResponse = new WebSocketEventMessageResponse(LATEST_CURRENCY_PAIR_RATE,dataClientMockedResponse);

        when(cryptoDataServiceClient.getLatestCurrencyPairRate(symbol, USER_TIME_ZONE)).thenReturn(dataClientMockedResponse);

        // When
        wsCurrencyPairService.fetchAndSendLatestCurrencyPairRateToSession(symbol, SESSION_ID, USER_TIME_ZONE);

        // Then
        verify(cryptoDataServiceClient).getLatestCurrencyPairRate(symbol, USER_TIME_ZONE);
        verify(messagingTemplate).convertAndSend(eq(WS_SESSION_URL_PATH + SESSION_ID), argumentCaptor.capture());

        WebSocketEventMessageResponse capturedResponse = argumentCaptor.getValue();

        assertThat(capturedResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    void shouldFetchAndSendCurrencyPairLast24hRateHistoryEventToSession() {
        // Given
        String symbol = "ETHUSD";
        String windowTime = "24H";

        CurrencyPairChartRateHistoryResponse dataClientMockedResponse = new CurrencyPairChartRateHistoryResponse(symbol,windowTime,List.of(new ChartRateHistory(BigDecimal.valueOf(3021.2314),TIMESTAMP)));

        WebSocketEventMessageResponse expectedResponse = new WebSocketEventMessageResponse(LAST_24H_CURRENCY_PAIR_RATE_HISTORY,dataClientMockedResponse);

        when(cryptoDataServiceClient.getCurrencyPairLast24hRateHistory(symbol, USER_TIME_ZONE)).thenReturn(dataClientMockedResponse);

        // When
        wsCurrencyPairService.fetchAndSendCurrencyPairLast24hRateHistoryToSession(symbol, SESSION_ID, USER_TIME_ZONE);

        // Then
        verify(cryptoDataServiceClient).getCurrencyPairLast24hRateHistory(symbol, USER_TIME_ZONE);
        verify(sessionManager).setSessionAttribute(SESSION_ID, "selectedSymbol", symbol);
        verify(messagingTemplate).convertAndSend(eq(WS_SESSION_URL_PATH + SESSION_ID), argumentCaptor.capture());

        WebSocketEventMessageResponse capturedResponse = argumentCaptor.getValue();

        assertThat(capturedResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

    }

    @Test
    void shouldFetchAndSendAvailableCurrencyPairsLatestCurrencyRateEventToSession() {
        // Given
        List<CurrencyPairSingleRateHistoryResponse> dataClientMockedResponse = List.of(new CurrencyPairSingleRateHistoryResponse("BTCUSDT", BigDecimal.valueOf(60231.2352), TIMESTAMP));
        when(cryptoDataServiceClient.getAvailableCurrencyPairsLatestCurrencyRate(USER_TIME_ZONE)).thenReturn(dataClientMockedResponse);

        WebSocketEventMessageResponse expectedResponse = new WebSocketEventMessageResponse(AVAILABLE_CURRENCIES_LATEST_RATE_HISTORY,dataClientMockedResponse);

        // WHen
        wsCurrencyPairService.fetchAndSendAvailableCurrencyPairsLatestCurrencyRateToSession(SESSION_ID, USER_TIME_ZONE);

        // Then
        verify(cryptoDataServiceClient).getAvailableCurrencyPairsLatestCurrencyRate(USER_TIME_ZONE);
        verify(messagingTemplate).convertAndSend(eq(WS_SESSION_URL_PATH + SESSION_ID), argumentCaptor.capture());

        WebSocketEventMessageResponse capturedResponse = argumentCaptor.getValue();

        assertThat(capturedResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }
}