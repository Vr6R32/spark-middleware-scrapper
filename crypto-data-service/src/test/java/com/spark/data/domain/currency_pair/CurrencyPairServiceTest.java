package com.spark.data.domain.currency_pair;

import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.request.CurrencyPairCreateRequest;
import com.spark.models.request.CurrencyPairDeleteRequest;
import com.spark.models.request.CurrencyPairUpdateRequest;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.spark.data.domain.currency_pair.CurrencyPairException.SYMBOL_ALREADY_EXISTS;
import static com.spark.data.domain.currency_pair.CurrencyPairException.SYMBOL_IS_NOT_AVAILABLE;
import static com.spark.models.response.CurrencyPairResponse.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CurrencyPairServiceTest {

    @Mock
    private CurrencyPairRepository currencyPairRepository;

    private CurrencyPairService currencyPairService;

    public static final String BTCUSDT = "BTCUSDT";
    public static final String ETHUSDT = "ETHUSDT";
    public static final String LTCUSDT = "LTCUSDT";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyPairService = new CurrencyPairService(currencyPairRepository);
    }

    @Test
    void getAvailableCurrenciesReturnsAvailableCurrencyPairsHappyPath() {

        // Given
        List<CurrencyPair> currencyPairs = new ArrayList<>();
        currencyPairs.add(new CurrencyPair(1L, BTCUSDT));
        currencyPairs.add(new CurrencyPair(2L, ETHUSDT));
        when(currencyPairRepository.findAll()).thenReturn(currencyPairs);

        // When
        AvailableCurrencyPairsResponse response = currencyPairService.getAvailableCurrencies();

        // Then
        assertNotNull(response);
        assertEquals(2, response.currencies().size());
    }

    @Test
    void registerNewCurrencyPairSuccessfullyRegistersNewPairHappyPath() {

        // Given
        CurrencyPairCreateRequest request = new CurrencyPairCreateRequest(LTCUSDT);

        when(currencyPairRepository.findBySymbol(LTCUSDT)).thenReturn(Optional.empty());

        // When
        CurrencyPairResponse response = currencyPairService.registerNewCurrencyPair(request);

        // Then
        assertNotNull(response);
        assertEquals(CREATED_RESPONSE, response.message());
        assertEquals(LTCUSDT, response.currencyPair().symbol());
        verify(currencyPairRepository, times(1)).save(any());
    }

    @Test
    void registerNewCurrencyPairFailsThrowsExceptionIfSymbolAlreadyExists() {

        // Given
        CurrencyPairCreateRequest request = new CurrencyPairCreateRequest(BTCUSDT);

        // WHEN
        // THEN
        when(currencyPairRepository.findBySymbol(BTCUSDT)).thenReturn(Optional.of(new CurrencyPair(1L, BTCUSDT)));
        CurrencyPairException exception = assertThrows(CurrencyPairException.class, () -> {
            currencyPairService.registerNewCurrencyPair(request);
        });

        assertEquals(SYMBOL_ALREADY_EXISTS, exception.getMessage());
    }

    @Test
    void deleteCurrencyPairSuccessfullyDeletesPairHappyPath() {

        // Given
        CurrencyPairDeleteRequest request = new CurrencyPairDeleteRequest(ETHUSDT);
        CurrencyPair pairToDelete = new CurrencyPair(2L, ETHUSDT);
        when(currencyPairRepository.findBySymbol(ETHUSDT)).thenReturn(Optional.of(pairToDelete));

        // When
        CurrencyPairResponse response = currencyPairService.deleteCurrencyPair(request);

        // Then
        assertNotNull(response);
        assertEquals(DELETED_RESPONSE, response.message());
        assertEquals(ETHUSDT, response.currencyPair().symbol());
        verify(currencyPairRepository, times(1)).deleteById(pairToDelete.getId());
    }

    @Test
    void deleteCurrencyPairFailsThrowsExceptionIfPairNotAvailable() {

        // Given
        CurrencyPairDeleteRequest request = new CurrencyPairDeleteRequest(LTCUSDT);
        when(currencyPairRepository.findBySymbol(LTCUSDT)).thenReturn(Optional.empty());

        // When
        // Then
        CurrencyPairException exception = assertThrows(CurrencyPairException.class, () -> {
            currencyPairService.deleteCurrencyPair(request);
        });
        assertEquals(SYMBOL_IS_NOT_AVAILABLE, exception.getMessage());
    }

    @Test
    void changeCurrencyPairDataSuccessfullyChangesPairDataHappyPath() {

        // Given
        CurrencyPairUpdateRequest request = new CurrencyPairUpdateRequest(BTCUSDT,new CurrencyPairDTO(1L, BTCUSDT));

        CurrencyPair existingPair = new CurrencyPair(1L, BTCUSDT);
        when(currencyPairRepository.findBySymbol(BTCUSDT)).thenReturn(Optional.of(existingPair));
        when(currencyPairRepository.save(existingPair)).thenReturn(existingPair);
//
        // When
        CurrencyPairResponse response = currencyPairService.changeCurrencyPairData(request);

        // Then
        assertNotNull(response);
        assertEquals(EDITED_RESPONSE, response.message());
        assertEquals(BTCUSDT, response.currencyPair().symbol());
        verify(currencyPairRepository, times(1)).save(existingPair);
    }


    @Test
    void changeCurrencyPairDataFailsThrowsExceptionIfPairNotAvailable() {

        // Given
        CurrencyPairUpdateRequest request = new CurrencyPairUpdateRequest(BTCUSDT,new CurrencyPairDTO(1L, LTCUSDT));
        when(currencyPairRepository.findBySymbol(LTCUSDT)).thenReturn(Optional.empty());

        // When / Then
        CurrencyPairException exception = assertThrows(CurrencyPairException.class, () -> {
            currencyPairService.changeCurrencyPairData(request);
        });
        assertEquals(SYMBOL_IS_NOT_AVAILABLE, exception.getMessage());
    }
}
