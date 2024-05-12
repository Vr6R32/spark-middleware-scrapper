

function handleIncomingEvent(event) {
    if (event.type === 'AVAILABLE_CURRENCIES') {
        handleAvailableCurrenciesData(event.payload);
    }
    if (event.type === 'LATEST_CURRENCY_PAIR_RATE') {
        // TODO WS BACKEND HANDLES IT , JUST NEED TO IMPLEMENT FRONTEND BEHAVIOUR ON OUR NEEDS
        console.log(event.payload);
    }
    if (event.type === 'LAST_24H_CURRENCY_PAIR_RATE_HISTORY') {
        handleLast24hCurrencyPairRateHistory(event.payload);
    }
    if (event.type === 'AVAILABLE_CURRENCIES_LATEST_RATE_HISTORY') {
        // TODO WS BACKEND HANDLES IT , JUST NEED TO IMPLEMENT FRONTEND BEHAVIOUR ON OUR NEEDS
        console.log(event.payload);
    }
    if (event.type === 'CURRENCY_PAIR_RATE_HISTORY_UPDATE') {
        updateChartData(event.payload);
    }
}

function handleAvailableCurrenciesData(payload) {
    const currencySelector = document.getElementById('currency-selector');
    payload.currencies.forEach(currency => {
        const option = document.createElement('option');
        option.value = currency.symbol;
        option.text = currency.symbol;
        currencySelector.appendChild(option);
    });
    selectedSymbol = currencySelector.value;
    fetchCoinRateHistory(selectedSymbol);
}

function handleLast24hCurrencyPairRateHistory(payload) {
    initializeUpdateChart(payload, true);
}
