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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
@Service
@RequiredArgsConstructor
class ScrapperService {

    @Value("${binance.apiUrl}")
    private String binanceApiUrl;

    @Value("${scheduler.retryFetchIntervalMilliseconds}")
    private int retryInterval;

    @Value("${scheduler.retryFetchAttempts}")
    private int retryAttempts;

    private final RestTemplate restTemplate;
    private final WebSocketServiceClient webSocketServiceClient;
    private final CryptoDataServiceClient cryptoDataServiceClient;

    @Scheduled(fixedRateString = "${scheduler.intervalMilliseconds}")
    public void scrapeBinanceApiMarketData() {

        Optional<Set<CurrencyPairDTO>> optionalCurrenciesToScrape = executeWithRetry(
                () -> cryptoDataServiceClient.getAvailableCurrencies().currencies(), false, "Fetch Available Currencies");


        if (optionalCurrenciesToScrape.isEmpty()) {
            log.error("Failed to fetch currencies. Exiting method.");
            return;
        }

        Set<CurrencyPairDTO> currenciesToScrape = optionalCurrenciesToScrape.get();

        Set<ScrappedCurrency> scrappedCurrencySet = new HashSet<>();
        for (CurrencyPairDTO currencyPairDTO : currenciesToScrape) {
            executeWithRetry(() -> {
                BinanceCurrencyResponse response = restTemplate.getForObject(binanceApiUrl + currencyPairDTO.symbol(), BinanceCurrencyResponse.class);
                assert response != null;
                scrappedCurrencySet.add(new ScrappedCurrency(response.symbol(), response.lastPrice(), Instant.now().toEpochMilli()));
                return null;
            }, true, "Fetch Data for " + currencyPairDTO.symbol());
        }

        log.info("SUCCESSFULLY SCRAPPED [{}] OF [{}] AVAILABLE CURRENCY PAIRS", scrappedCurrencySet.size(), currenciesToScrape.size());

        ScrappedCurrencyUpdateRequest request = new ScrappedCurrencyUpdateRequest(scrappedCurrencySet, currenciesToScrape.size());
        executeWithRetry(() -> { webSocketServiceClient.pushScrappedDataForUpdateToWebSocketSessions(request); return null; }, true, "WebSocket Push");
        executeWithRetry(() -> { cryptoDataServiceClient.pushScrappedCurrencySetForDataServiceUpdate(request); return null; }, true, "Update Currency Set");
    }

    <T> Optional<T> executeWithRetry(Supplier<T> operation, boolean isVoid, String logContext) {
        int attempts = 0;
        while (attempts < retryAttempts) {
            try {
                T result = operation.get();
                if (!isVoid) return Optional.ofNullable(result);

                return Optional.empty();
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
                    throw new RuntimeException(ie.getMessage());
                }
            }
        }
        return Optional.empty();
    }
}