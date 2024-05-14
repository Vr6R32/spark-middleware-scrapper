function fetchCoinRateHistory(selectedSymbol) {

    //TODO make urls loaded from config properties

    let url = `http://localhost:9999/api/v1/currencies/lastDay/${selectedSymbol}?timezone=${getUserTimeZone()}`;

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

    let url = `http://localhost:9999/api/v1/currencies`;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('ERROR OCCURRED WHILE FETCHING AVAILABLE CURRENCY DATA');
            }
            return response.json();
        })
        .then(payload => {
            currencySelectorHandle(payload);
        })
        .catch(error => {
            console.error('ERROR OCCURRED', error.message);
        });
}