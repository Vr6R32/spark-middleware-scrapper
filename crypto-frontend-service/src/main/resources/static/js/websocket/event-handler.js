

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
    currencySelectorHandle(payload);
}

function handleLast24hCurrencyPairRateHistory(payload) {
    initializeUpdateChart(payload, true);
}
