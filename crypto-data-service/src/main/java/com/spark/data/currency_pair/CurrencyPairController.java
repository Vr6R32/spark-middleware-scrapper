package com.spark.data.currency_pair;

import com.spark.models.model.ScrappedCurrency;
import com.spark.models.response.AvailableCurrencyPairsResponse;
import com.spark.models.response.CurrencyPairRateHistoryResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/currencies")
record CurrencyPairController(CurrencyPairFacade currencyPairFacade) {

    @GetMapping
    public AvailableCurrencyPairsResponse getAvailableCurrencies() {
        return currencyPairFacade.getAvailableCurrencies();
    }

    @PutMapping("/update")
    void updateCurrencyPairRateHistory(@RequestBody Set<ScrappedCurrency> scrappedCurrencySet) {
        currencyPairFacade.updateCurrencyPairRateHistory(scrappedCurrencySet);
    }

    @GetMapping("/last/{symbol}")
    public CurrencyPairRateHistoryResponse getLatestCurrencyPairRate(@PathVariable("symbol") String symbol,
                                                                     @RequestParam(defaultValue = "Europe/Warsaw", required = false, name = "userZoneId") String userZoneId){
        return currencyPairFacade.getLatestCurrencyPairRate(symbol, userZoneId);
    }

    @GetMapping("/lastDay/{symbol}")
    public List<CurrencyPairRateHistoryResponse> getCurrencyPairLast24hRateHistory(@PathVariable("symbol") String symbol,
                                                                                   @RequestParam(defaultValue = "Europe/Warsaw", required = false, name = "userZoneId") String userZoneId) {
        return currencyPairFacade.getCurrencyPairLast24hRateHistory(symbol,userZoneId);
    }

    @GetMapping("/lastAll")
    public List<CurrencyPairRateHistoryResponse> getAvailableCurrencyPairsLatestCurrencyRate(@RequestParam(defaultValue = "Europe/Warsaw", required = false, name = "userZoneId") String userZoneId) {
        return currencyPairFacade.getAvailableCurrencyPairsLatestCurrencyRate(userZoneId);
    }

}
