package com.spark.scrapper;

import com.spark.feign_client.CryptoDataServiceClient;
import com.spark.models.model.BinanceCurrencyResponse;
import com.spark.models.model.CurrencyPairDTO;
import com.spark.models.model.ScrappedCurrency;
import com.spark.models.request.ScrappedCurrencyUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
class ScrapperService {

    @Value("${binance.apiUrl}")
    private String binanceApiUrl;

    private final RestTemplate restTemplate;
    private final CryptoDataServiceClient cryptoDataServiceClient;

    @Scheduled(fixedRateString = "${scheduler.intervalMilliseconds}")
    public void scrapeBinanceApiMarketData() {
        Set<CurrencyPairDTO> currenciesToScrape = cryptoDataServiceClient.getAvailableCurrencies().currencies();

        Set<ScrappedCurrency> scrappedCurrencySet = new HashSet<>();

        for (CurrencyPairDTO currencyPairDTO : currenciesToScrape) {
            String urlCompletedBySymbol = binanceApiUrl + currencyPairDTO.symbol();
            try {
                BinanceCurrencyResponse response = restTemplate.getForObject(urlCompletedBySymbol, BinanceCurrencyResponse.class);
                assert response != null;
                ScrappedCurrency scrappedCurrency = new ScrappedCurrency(response.symbol(), response.lastPrice(), Instant.now().toEpochMilli());
                scrappedCurrencySet.add(scrappedCurrency);
            } catch (RestClientException exception) {
                log.warn("EXCEPTION OCCURRED WHILE SCRAPPING BINANCE SERVICE -> {} WITH PAYLOAD -> {} ", exception.getMessage() , currencyPairDTO.symbol());
            }
        }
        cryptoDataServiceClient.pushScrappedCurrencySetForUpdate(new ScrappedCurrencyUpdateRequest(scrappedCurrencySet, currenciesToScrape.size()));
        log.info("SUCCESSFULLY SCRAPPED [{}] OF [{}] AVAILABLE CURRENCY PAIRS", scrappedCurrencySet.size(), currenciesToScrape.size());
    }

}
