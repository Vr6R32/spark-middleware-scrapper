function fetchCoinRateHistory(selectedSymbol) {
    let url = `/api/v1/currencies/lastDay/${selectedSymbol}?timezone=${getUserTimeZone()}`;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('ERROR OCCURRED WHILE FETCHING CURRENCY RATE HISTORY DATA');
            }
            return response.json();
        })
        .then(data => {
            initializeUpdateChart(data,true);
        })
        .catch(error => {
            console.error('ERROR OCCURRED', error);
        });
}

function fetchAvailableCurrencies() {
    fetch('/api/v1/currencies')
        .then(response => {
            if (!response.ok) {
                throw new Error('ERROR OCCURRED WHILE FETCHING AVAILABLE CURRENCY DATA');
            }
            return response.json();
        })
        .then(data => {
            const currencySelector = document.getElementById('currency-selector');
            data.currencies.forEach(currency => {
                const option = document.createElement('option');
                option.value = currency.symbol;
                option.text = currency.symbol;
                currencySelector.appendChild(option);
            });
            selectedSymbol = currencySelector.value;
            fetchCoinRateHistory(selectedSymbol);
        })
        .catch(error => {
            console.error('ERROR OCCURRED', error.message);
        });
}