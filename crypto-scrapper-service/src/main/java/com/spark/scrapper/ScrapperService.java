package com.spark.scrapper;

import com.spark.feign.client.CryptoDataServiceClient;
import com.spark.feign.client.WebSocketServiceClient;
import com.spark.models.model.BinanceCurrencyResponse;
import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.model.ScrappedCurrency;
import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
@Service
@RequiredArgsConstructor
class ScrapperService {

    @Value("${binance.apiUrl}")
    private String binanceApiUrl;

    @Value("${scrapper.retryFetchIntervalMilliseconds}")
    private int retryInterval;

    @Value("${scrapper.retryFetchAttempts}")
    private int retryAttempts;

    private final RestTemplate restTemplate;
    private final WebSocketServiceClient webSocketServiceClient;
    private final CryptoDataServiceClient cryptoDataServiceClient;

    @Scheduled(fixedRateString = "${scheduler.intervalMilliseconds}")
    public void asynchronouslyScrapeBinanceApiMarketDataAndPushDataUpdateRequest() {
        Optional<Set<CurrencyPairDTO>> optionalCurrenciesToScrape = executeWithRetryOptional(
                () -> cryptoDataServiceClient.getAvailableCurrencies().currencies(), "Fetch Available Currencies");

        if (optionalCurrenciesToScrape.isEmpty()) {
            log.error("Failed to fetch currencies. Exiting method.");
            return;
        }

        Set<CurrencyPairDTO> currenciesToScrape = optionalCurrenciesToScrape.get();
        Set<ScrappedCurrency> scrappedCurrencySet = ConcurrentHashMap.newKeySet();

        List<CompletableFuture<Void>> scrappedCompletableFutureCurrencies = currenciesToScrape.stream()
                .map(currencyPairDTO -> CompletableFuture.runAsync(() -> executeWithRetryOptional(() -> {
                    BinanceCurrencyResponse response = restTemplate.getForObject(binanceApiUrl + currencyPairDTO.symbol(), BinanceCurrencyResponse.class);
                    assert response != null;
                    return scrappedCurrencySet.add(new ScrappedCurrency(response.symbol(), response.lastPrice(), Instant.now().toEpochMilli()));
                }, "Fetch Data for " + currencyPairDTO.symbol())))
                .toList();

        CompletableFuture.allOf(scrappedCompletableFutureCurrencies.toArray(new CompletableFuture[0])).join();

        log.info("SUCCESSFULLY SCRAPPED [{}] OF [{}] AVAILABLE CURRENCY PAIRS", scrappedCurrencySet.size(), currenciesToScrape.size());

        ScrappedCurrencyUpdateRequest request = new ScrappedCurrencyUpdateRequest(scrappedCurrencySet, currenciesToScrape.size());

        CompletableFuture<Void> websocketScrappedUpdate = executeWithRetryVoidAsynchronous(
                consumer -> webSocketServiceClient.pushScrappedDataForUpdateToWebSocketSessions(request), "WebSocket Push");

        CompletableFuture<Void> dataServiceScrappedUpdate = executeWithRetryVoidAsynchronous(
                consumer -> cryptoDataServiceClient.pushScrappedCurrencySetForDataServiceUpdate(request), "Update Currency Set");

        CompletableFuture.allOf(websocketScrappedUpdate, dataServiceScrappedUpdate).join();
    }

    <T> Optional<T> executeWithRetryOptional(Supplier<T> operation, String logContext) {
        int attempts = 0;
        while (attempts < retryAttempts) {
            try {
                T result = operation.get();
                return Optional.ofNullable(result);
            } catch (Exception e) {
                attempts++;
                log.warn("{} - Retry {} of {}: {}", logContext, attempts, retryAttempts, e.getMessage());
                if (attempts >= retryAttempts) {
                    log.error("Failed to complete {} after {} attempts", logContext, retryAttempts);
                    break;
                }
                try {
                    MILLISECONDS.sleep(retryInterval);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Thread interrupted during retry of {}", logContext, ie);
                    throw new ScrapperThreadInterruptException(ie.getMessage());
                }
            }
        }
        return Optional.empty();
    }

    public CompletableFuture<Void> executeWithRetryVoidAsynchronous(Consumer<Void> operation, String logContext) {
        return CompletableFuture.runAsync(() -> {
            int attempts = 0;
            while (attempts < retryAttempts) {
                try {
                    operation.accept(null);
                    break;
                } catch (Exception e) {
                    attempts++;
                    log.warn("{} - Retry {} of {}: {}", logContext, attempts, retryAttempts, e.getMessage());
                    if (attempts >= retryAttempts) {
                        log.error("Failed after {} attempts due to {}", attempts, e.toString());
                        break;
                    }
                    try {
                        MILLISECONDS.sleep(retryInterval);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new ScrapperThreadInterruptException(ie.getMessage());
                    }
                }
            }
        });
    }
}