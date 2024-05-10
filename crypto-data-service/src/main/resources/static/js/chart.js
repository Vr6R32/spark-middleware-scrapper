let coinChart;
let currentData;
let useSingleColor = true;
let dragZoomDisabled = true;
let black_color = 'rgb(0, 0, 0, 0.4)';
let teal_color = 'rgb(105, 232, 192)';
let red_color = 'rgb(255, 99, 132)';
let gray_color = 'rgba(90,90,90,0.5)';



document.addEventListener('DOMContentLoaded', function() {
    fetchAvailableCurrencies();
    currencySelectorListener();
    colorButtonListener();
    windowResizeListener();
    dragZoomListener();
});


function addData(label, newData) {
    coinChart.data.labels.push(label);
    coinChart.data.datasets.forEach((dataset) => {
        dataset.data.push(newData);
    });
    coinChart.update();

}

function toggleDragZoom() {
    if (coinChart) {
        dragZoomDisabled = !dragZoomDisabled;
        this.classList.toggle('active');
        coinChart.options.plugins.zoom.pan.enabled = dragZoomDisabled;
        coinChart.options.plugins.zoom.zoom.drag.enabled = !dragZoomDisabled;
        coinChart.update();
    }
}


function createOrUpdateChart(data, resetzoom) {
    const labels = data.rateHistory.map(entry => new Date(entry.timestamp));
    const values = data.rateHistory.map(entry => entry.value);

    const canvasObject = document.getElementById('chart').getContext('2d');

    const getBorderColor = (ctx) => {
        if (useSingleColor) {
            return teal_color;
        } else {
            const p0 = ctx.p0DataIndex;
            const p1 = ctx.p1DataIndex;
            if (p1 === undefined || p0 === undefined) return teal_color;
            return ctx.chart.data.datasets[0].data[p1] > ctx.chart.data.datasets[0].data[p0] ? red_color : teal_color;
        }
    };


    if (coinChart) {
        if(resetzoom) {
            coinChart.resetZoom();
        }
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
                    borderColor: getBorderColor,
                    tension: 0.1,
                    pointRadius: 2,
                    pointBorderColor: ctx => {
                        if (useSingleColor) {
                            return teal_color;
                        }
                        const index = ctx.dataIndex;
                        const data = ctx.dataset.data;
                        if (index === 0) return teal_color;
                        return data[index] > data[index - 1] ? red_color : teal_color;
                    },
                    pointBackgroundColor: black_color,
                    fill: false,
                    segment: {
                        borderColor: getBorderColor
                    }
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        labels: {
                            color: 'white',
                            font: {
                                size: 16
                            }
                        }
                    },
                    title: {
                        display: true,
                        // text: 'Price Chart'
                        text: data.timeWindow + ' Price Chart'
                    },
                    zoom: {
                        pan: {
                            enabled: true,
                            mode: 'xy'
                        },
                        zoom: {
                            wheel: {
                                enabled: true
                            },
                            pinch: {
                                enabled: true
                            },
                            mode: 'xy',
                            drag: {
                                enabled: false,
                                mode: 'xy',
                                threshold: 5,
                                onDragComplete: function ({chart}) {
                                    chart.update();
                                }
                            }
                        }
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
                            }
                        },
                        grid: {
                            color: gray_color
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
                            color: gray_color
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