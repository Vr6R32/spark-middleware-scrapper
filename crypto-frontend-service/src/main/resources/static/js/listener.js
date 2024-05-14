
function activateListeners() {
    currencySelectorListener();
    chartTimeScaleSelectorListener();
    currencyTimeWindowSelectorListener();
    resetZoomListener();
    windowResizeListener();
    colorButtonListener();
    dragZoomListener();
}


function colorButtonListener() {
    let colorModeButton = document.getElementById('color-mode');
    colorModeButton.addEventListener('click', function () {
        useSingleColor = !useSingleColor;
        this.classList.toggle('active');
        saveUserSettingToLocalStorage('use-single-color', useSingleColor);
        initializeUpdateChart(currentData, false);
    });
}

function chartTimeScaleSelectorListener() {

    const timeScaleUnits = [
        'auto',
        'minute',
        'hour',
        'day',
        'week',
        'month',
        'quarter',
        'year'
    ];

    const chartTimeScaleSelectorListener = document.getElementById('chart-time-scale-selector');

    timeScaleUnits.forEach(option => {
        const opt = document.createElement('option');
        opt.value = option;
        opt.textContent = option.charAt(0).toUpperCase() + option.slice(1);
        chartTimeScaleSelectorListener.appendChild(opt);
        if (option === 'auto') {
            opt.selected = true;
        }
    });

    chartTimeScaleSelectorListener.addEventListener('change', function() {
        saveUserSettingToLocalStorage('chart-time-scale', this.value);
        updateChartTimeScaleUnit(this.value);
    });
}

function currencyTimeWindowSelectorListener() {

    //TODO HANDLE DIFFERENT TIME WINDOW CASES

    const timeWindowUnits = [
        // '1hour',
        '24h'
        // 'week',
        // 'month',
        // 'quarter',
        // 'year'
    ];

    const currencyTimeWindowSelectorListener = document.getElementById('currency-time-window-selector');

    timeWindowUnits.forEach(option => {
        const opt = document.createElement('option');
        opt.value = option;
        opt.textContent = option.charAt(0).toUpperCase() + option.slice(1);
        currencyTimeWindowSelectorListener.appendChild(opt);
        if (option === '24h') {
            opt.selected = true;
        }
    });

    currencyTimeWindowSelectorListener.addEventListener('change', function() {
        // fetchCoinRateHistory(selectedSymbol);
    });

}


function resetZoomListener() {
    let resetZoom = document.getElementById('reset-zoom');
    resetZoom.addEventListener('click', function () {
        resetChartZoom();
    });
}


function currencySelectorListener() {
    let currencySelector = document.getElementById('currency-selector');
    currencySelector.addEventListener('change', function () {
        selectedSymbol = this.value;
        saveUserSettingToLocalStorage('selected-symbol', selectedSymbol);
        fetchCoinRateHistory(selectedSymbol);
    });
}

function windowResizeListener() {
    window.onresize = function (event) {
        coinChart.resize();
    };
    // window.addEventListener('resize', function() {
    //     coinChart.resize();
    // });
}

function dragZoomListener() {
    let dragZoom = document.getElementById('drag-zoom');
    dragZoom.addEventListener('click', toggleDragZoom);
}

function currencySelectorHandle(payload) {
    const currencySelector = document.getElementById('currency-selector');
    payload.currencies.forEach(currency => {
        const option = document.createElement('option');
        option.value = currency.symbol;
        option.text = currency.symbol;
        currencySelector.appendChild(option);
        if (selectedSymbol !== null && option.value === selectedSymbol) option.selected = true;
    });
    if (selectedSymbol === null) selectedSymbol = currencySelector.value;
    fetchCoinRateHistory(selectedSymbol);
}