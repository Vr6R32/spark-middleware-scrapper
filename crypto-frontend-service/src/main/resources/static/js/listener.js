function colorButtonListener() {
    let colorModeButton = document.getElementById('color-mode');
    colorModeButton.addEventListener('click', function () {
        useSingleColor = !useSingleColor;
        this.classList.toggle('active');
        initializeUpdateChart(currentData,false);
    });
}

function tickRateSelectorListener() {

    const timeUnits = [
        'auto',
        'minute',
        'hour',
        'day',
        'week',
        'month',
        'quarter',
        'year'
    ];

    const currencyTickRateSelector = document.getElementById('currency-tick-rate-selector');

    timeUnits.forEach(option => {
        const opt = document.createElement('option');
        opt.value = option;
        opt.textContent = option.charAt(0).toUpperCase() + option.slice(1);
        currencyTickRateSelector.appendChild(opt);
        if (option === 'auto') {
            opt.selected = true;
        }
    });

    currencyTickRateSelector.addEventListener('change', function() {
        updateChartTimeUnit(this.value);
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