function getUserTimeZone() {
    return encodeURIComponent(Intl.DateTimeFormat().resolvedOptions().timeZone);
}

function extractSessionId(socketUrl) {
    const parts = socketUrl.split('/');
    return parts[5];
}

function calculateColorPrediction() {
    if(currentData!==null) {
        let latestHistory = currentData.rateHistory[0].value;
        let latestHistoryMinusOne = currentData.rateHistory[1].value;
        if (latestHistory < latestHistoryMinusOne) {
            currentChartColor = red_color;
        } else {
            currentChartColor = teal_color;
        }
    }
}


function saveUserSettingToLocalStorage(name,value) {
    localStorage.setItem(name, value);
}

function retrieveUserSavedSettings(name,value) {
    value = localStorage.getItem(name);
    if(value === 'true') {
        return true;
    }
    if(value === 'false'){
        return false;
    }
    return value;
}

function initializeUserSettings() {

    let colorSettingsFlag = retrieveUserSavedSettings('use-single-color');
    useSingleColor = colorSettingsFlag !== null ? colorSettingsFlag : true;
    if(!useSingleColor) {
        let colorModeButton = document.getElementById('color-mode');
        colorModeButton.classList.toggle('active');
    }

    selectedSymbol = retrieveUserSavedSettings('selected-symbol');
    fetchAvailableCurrencies();
}