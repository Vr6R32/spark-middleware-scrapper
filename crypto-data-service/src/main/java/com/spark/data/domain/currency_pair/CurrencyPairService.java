package com.spark.data.domain.currency_pair;

import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.request.CurrencyPairRequest;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

import static com.spark.data.domain.currency_pair.CurrencyPairException.SYMBOL_ALREADY_EXISTS;
import static com.spark.data.domain.currency_pair.CurrencyPairException.SYMBOL_IS_NOT_AVAILABLE;
import static com.spark.data.domain.currency_pair.CurrencyPairMapper.mapCurrencyPairToDTO;
import static com.spark.models.response.CurrencyPairResponse.*;

@Slf4j
@RequiredArgsConstructor
class CurrencyPairService {

    private final CurrencyPairRepository currencyPairRepository;

    /**
     * Retrieves all available currency pairs.
     *
     * @return Response containing available currency pairs.
     */
    public AvailableCurrencyPairsResponse getAvailableCurrencies() {
        Set<CurrencyPairDTO> currencyPairDTOs = currencyPairRepository.findAll()
                .stream()
                .map(CurrencyPairMapper::mapCurrencyPairToDTO)
                .collect(Collectors.toSet());
        return new AvailableCurrencyPairsResponse(currencyPairDTOs);
    }

    /**
     * Registers a new currency pair based on the provided request.
     *
     * @param request Request containing the symbol of the new currency pair.
     * @return Response indicating the success of the operation and the newly registered currency pair.
     * @throws CurrencyPairException if the specified currency pair already exists.
     */
    public CurrencyPairResponse registerNewCurrencyPair(CurrencyPairRequest request) {
        if (currencyPairRepository.findBySymbol(request.symbol()).isPresent()) {
            throw new CurrencyPairException(SYMBOL_ALREADY_EXISTS);
        }
        CurrencyPair newCurrencyPair = CurrencyPair.builder().symbol(request.symbol()).build();
        currencyPairRepository.save(newCurrencyPair);
        return new CurrencyPairResponse(CREATED_RESPONSE, mapCurrencyPairToDTO(newCurrencyPair));
    }

    /**
     * Deletes a currency pair based on the provided request.
     *
     * @param request Request containing the symbol of the currency pair to delete.
     * @return Response indicating the success of the operation and the deleted currency pair.
     * @throws CurrencyPairException if the specified currency pair is not available.
     */
    public CurrencyPairResponse deleteCurrencyPair(CurrencyPairRequest request) {
        CurrencyPair deletedCurrencyPair = currencyPairRepository.findBySymbol(request.symbol())
                .orElseThrow(() -> new CurrencyPairException(SYMBOL_IS_NOT_AVAILABLE));
        currencyPairRepository.deleteById(deletedCurrencyPair.getId());
        return new CurrencyPairResponse(DELETED_RESPONSE, mapCurrencyPairToDTO(deletedCurrencyPair));
    }

    /**
     * Updates the data of a currency pair based on the provided request.
     * This method may require further enhancements in the future to accommodate additional fields
     * that could be added to the CurrencyPair entity.
     *
     * @param request The request containing updated data for the currency pair.
     * @return A response indicating the success of the operation and the updated currency pair data.
     * @throws CurrencyPairException if the specified currency pair is not available.
     */
    public CurrencyPairResponse changeCurrencyPairData(CurrencyPairRequest request) {
        CurrencyPair currencyPairToChange = currencyPairRepository.findBySymbol(request.symbol())
                .orElseThrow(() -> new CurrencyPairException(SYMBOL_IS_NOT_AVAILABLE));
        currencyPairToChange.setSymbol(request.symbol());
        CurrencyPair changedCurrencyPair = currencyPairRepository.save(currencyPairToChange);
        return new CurrencyPairResponse(EDITED_RESPONSE, mapCurrencyPairToDTO(changedCurrencyPair));
    }
}
