package com.spark.data.infrastructure.controller;

import com.spark.data.domain.currency_pair.CurrencyPairException;
import com.spark.models.request.*;
import com.spark.models.response.CurrencyPairResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/management/currencies")
public interface ManagementApi {

    @Operation(summary = "Update Currency Pair Rate History",
            description = "Updates the rate history for a currency pair using scrapped data.")
    @PostMapping("/scrapper/update")
    void updateCurrencyPairRateHistory(@RequestBody ScrappedCurrencyUpdateRequest request);

    @Operation(summary = "Register New Currency Pair",
            description = "Registers a new currency pair.")
    @ApiResponse(responseCode = "200", description = "Successfully registered new currency pair",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyPairResponse.class)))
    @ApiResponse(responseCode = "400", description = "Bad request - Symbol already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyPairException.class)))
    @PostMapping
    CurrencyPairResponse registerNewCurrencyPair(@RequestBody CurrencyPairCreateRequest request);

    @Operation(summary = "Change Currency Pair Data",
            description = "Changes the data of an existing currency pair.")
    @ApiResponse(responseCode = "200", description = "Successfully changed currency pair data",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyPairResponse.class)))
    @ApiResponse(responseCode = "400", description = "Bad request - Invalid symbol",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyPairException.class)))
    @PutMapping
    CurrencyPairResponse changeCurrencyPairData(@RequestBody CurrencyPairUpdateRequest request);

    @Operation(summary = "Delete Currency Pair",
            description = "Deletes an existing currency pair.")
    @ApiResponse(responseCode = "200", description = "Successfully deleted currency pair",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyPairResponse.class)))
    @ApiResponse(responseCode = "400", description = "Bad request - Invalid symbol",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyPairException.class)))
    @DeleteMapping
    CurrencyPairResponse deleteCurrencyPair(@RequestBody CurrencyPairDeleteRequest request);

}
