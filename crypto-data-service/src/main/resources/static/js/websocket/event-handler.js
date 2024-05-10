

function handleIncomingEvent(notificationMessage) {
    if (notificationMessage.type === 'AVAILABLE_CURRENCIES') {
        handleAvailableCurrenciesData(notificationMessage.payload);
    }
    if (notificationMessage.type === 'LATEST_CURRENCY_PAIR_RATE') {
        // TODO
    }
    if (notificationMessage.type === 'LAST_24H_CURRENCY_PAIR_RATE_HISTORY') {
        handleLast24hCurrencyPairRateHistory(notificationMessage);
    }
    if (notificationMessage.type === 'CURRENCY_PAIR_RATE_HISTORY_UPDATE') {
        // TODO
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

function handleLast24hCurrencyPairRateHistory(notificationMessage) {
    currentData = notificationMessage.payload;
    createOrUpdateChart(notificationMessage.payload, true);
}
