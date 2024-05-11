function colorButtonListener() {
    let colorModeButton = document.getElementById('color-mode');
    colorModeButton.addEventListener('click', function () {
        useSingleColor = !useSingleColor;
        this.classList.toggle('active');
        initializeUpdateChart(currentData,false);
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