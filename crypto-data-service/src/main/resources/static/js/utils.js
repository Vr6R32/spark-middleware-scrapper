function getUserTimeZone() {
    return encodeURIComponent(Intl.DateTimeFormat().resolvedOptions().timeZone);
}

function extractSessionId(socketUrl) {
    const parts = socketUrl.split('/');
    return parts[5];
}

function normalizeDate(timestamp) {
    // TODO THINK WHY WEBSOCKET SERIALIZED RESPONSE IS LITTLE DIFFERENT FROM REST ( SAME TIME CONVERTER )
    // {"value":60864.51000000,"timestamp":"2024-05-11T10:00:50.941Z"},
    // {"value":60864.51000000,"timestamp":"2024-05-11T10:00:50.941+02:00"}}
    return new Date(timestamp).toISOString();
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
