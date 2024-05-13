function getUserTimeZone() {
    return encodeURIComponent(Intl.DateTimeFormat().resolvedOptions().timeZone);
}

function extractSessionId(socketUrl) {
    const parts = socketUrl.split('/');
    return parts[5];
}

function calculateColorPrediction() {
    let latestHistory = currentData.rateHistory[0].value;
    let latestHistoryMinusOne = currentData.rateHistory[1].value;
    if (latestHistory < latestHistoryMinusOne) {
        currentChartColor = red_color;
    } else {
        currentChartColor = teal_color;
    }

}
