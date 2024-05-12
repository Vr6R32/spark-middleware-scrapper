
function fetchSelectedCoin24hRateHistoryWebSocketEvent(symbol) {
    if (stompClient && stompClient.connected) {
        stompClient.send('/ws/api/v1/lastDay/' + symbol, {}, null);
    } else {
        setTimeout(() => fetchSelectedCoin24hRateHistoryWebSocketEvent(symbol), 50);
    }
}

function fetchAvailableCurrenciesWebSocketEvent() {
    if (stompClient && stompClient.connected) {
        stompClient.send('/ws/api/v1/currencies', {}, null);
    } else {
        setTimeout(() => fetchAvailableCurrenciesWebSocketEvent(), 50);
    }
}


function fetchAvailableCurrencies() {
    fetchAvailableCurrenciesWebSocketEvent();
}

function fetchCoinRateHistory(selectedSymbol) {
    fetchSelectedCoin24hRateHistoryWebSocketEvent(selectedSymbol);
}
