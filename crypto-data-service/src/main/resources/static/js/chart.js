let coinChart;

document.addEventListener('DOMContentLoaded', function() {
    const currencySelector = document.getElementById('currency-selector');
    fetchAvailableCurrencies();
    currencySelector.addEventListener('change', function() {
        fetchCoinRateHistory(this.value);
    });
});

window.addEventListener('resize', function() {
    coinChart.resize();
});

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


function fetchCoinRateHistory(selectedSymbol) {
    fetch('/api/v1/currencies/lastDay/' + selectedSymbol)
        .then(response => {
            if (!response.ok) {
                throw new Error('ERROR OCCURRED WHILE FETCHING CURRENCY RATE HISTORY DATA');
            }
            return response.json();
        })
        .then(data => {
            createOrUpdateChart(data);
        })
        .catch(error => {
            console.error('ERROR OCCURRED', error);
        });
}

function addData(label, newData) {
    coinChart.data.labels.push(label);
    coinChart.data.datasets.forEach((dataset) => {
        dataset.data.push(newData);
    });
    coinChart.update();

}

function createOrUpdateChart(data) {

    const labels = data.rateHistory.map(entry => new Date(entry.timestamp));
    const values = data.rateHistory.map(entry => entry.value);

    const canvasObject = document.getElementById('chart').getContext('2d');

    if (coinChart) {
        coinChart.resetZoom();
        coinChart.data.labels = labels;
        coinChart.data.datasets[0].data = values;
        coinChart.data.datasets[0].label = '(' + data.symbol + ') Price';
        coinChart.update();
    } else {
        coinChart = new Chart(canvasObject, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: '(' + data.symbol + ') Price',
                    data: values,
                    borderColor: 'rgb(105, 232, 192)',
                    // borderColor: 'rgb(148, 0, 211)',
                    // borderColor: 'rgb(75, 192, 192)',
                    tension: 0.2,
                    fill: true
                }]
            },
            options: {
                animation: true,
                responsive: true,
                plugins: {
                    legend: {
                        labels: {
                            ticks: {
                                color: 'white',
                                font: {
                                    size: 16
                                }
                            }
                        }
                    },
                    title: {
                        display: true,
                        text: 'Price Chart'
                    },
                    zoom: {
                        zoom: {
                            wheel: {
                                enabled: true,
                            },
                            drag: {
                                enabled: true
                            }
                        },

                    }
                },
                scales: {
                    x: {
                        type: 'time',

                        time: {
                            unit: 'minute'
                        },
                        ticks: {
                            color: 'white',
                            font: {
                                size: 16
                            },
                            // autoSkip: true,
                            // maxRotation: 0,
                            // minRotation: 0
                        },
                        grid: {
                            color: 'rgba(90,90,90,0.5)'
                        }
                    },
                    y: {
                        beginAtZero: false,
                        ticks: {
                            color: 'white',
                            font: {
                                size: 16
                            }
                        },
                        grid: {
                            color: 'rgba(90,90,90,0.5)'
                        }
                    }
                },
                interaction: {
                    mode: 'index',
                    intersect: false,
                },
            }
        });
    }
}

