function fetchCoinRateHistory(selectedSymbol) {
    fetch('/api/v1/currencies/lastDay/' + selectedSymbol)
        .then(response => {
            if (!response.ok) {
                throw new Error('ERROR OCCURRED WHILE FETCHING CURRENCY RATE HISTORY DATA');
            }
            return response.json();
        })
        .then(data => {
            currentData = data;
            createOrUpdateChart(data,true);
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
            fetchCoinRateHistory(currencySelector.value);
        })
        .catch(error => {
            console.error('ERROR OCCURRED', error.message);
        });
}