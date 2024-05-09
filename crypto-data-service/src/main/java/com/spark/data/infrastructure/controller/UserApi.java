package com.spark.data.infrastructure.controller;

import com.spark.data.domain.currency_pair.CurrencyPairException;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairRateHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserApi {

    @Operation(summary = "Get Available Currencies", description = "Retrieves all available currency pairs.")
    @GetMapping("/api/v1/currencies")
    AvailableCurrencyPairsResponse getAvailableCurrencies();


    @Operation(summary = "Get Latest Currency Pair Rate",
            description = "Retrieves the latest rate history for a specific currency pair.")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of latest rate history",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyPairRateHistoryResponse.class)))
    @ApiResponse(responseCode = "400", description = "Bad request - Invalid symbol",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CurrencyPairException.class)))
    @GetMapping("/api/v1/currencies/last/{symbol}")
    CurrencyPairRateHistoryResponse getLatestCurrencyPairRate(
            @Parameter(description = "Symbol of the currency pair", required = true, example = "BTCUSDT")
            @PathVariable("symbol") String symbol,
            @Parameter(description = "User's timezone identifier", required = false, example = "Europe/Warsaw")
            @RequestParam(defaultValue = "Europe/Warsaw", required = false, name = "userZoneId") String userZoneId);

    @Operation(summary = "Get Currency Pair Rate History (Last 24 Hours)",
            description = "Retrieves the rate history for a specific currency pair for the last 24 hours.")
    @ApiResponse(
            responseCode = "200",
            description = "Successful retrieval of rate history",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CurrencyPairRateHistoryResponse.class))))
    @GetMapping("/api/v1/currencies/lastDay/{symbol}")
    List<CurrencyPairRateHistoryResponse> getCurrencyPairLast24hRateHistory(
            @Parameter(description = "Symbol of the currency pair", required = true, example = "BTCUSDT")
            @PathVariable("symbol") String symbol,
            @Parameter(description = "User's timezone identifier", required = false, example = "Europe/Warsaw")
            @RequestParam(defaultValue = "Europe/Warsaw", required = false, name = "userZoneId") String userZoneId);

    @Operation(summary = "Get Latest Currency Rate for All Pairs",
            description = "Retrieves the latest rate history for all available currency pairs.")
    @ApiResponse(
            responseCode = "200",
            description = "Successful retrieval of latest rate histories",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CurrencyPairRateHistoryResponse.class))))
    @GetMapping("/api/v1/currencies/lastAll")
    List<CurrencyPairRateHistoryResponse> getAvailableCurrencyPairsLatestCurrencyRate(
            @Parameter(description = "User's timezone identifier", required = false, example = "Europe/Warsaw")
            @RequestParam(defaultValue = "Europe/Warsaw", required = false, name = "userZoneId") String userZoneId);

}