let coinChart;
let currentData;
let selectedSymbol;
let currentChartColor;
let useSingleColor = true;
let dragZoomDisabled = true;
let black_color_transparent = 'rgb(0, 0, 0, 0.4)';
let black_color_solid = 'rgb(0, 0, 0)';
let teal_color = 'rgb(105, 232, 192)';
let red_color = 'rgb(255, 99, 132)';
let gray_color = 'rgba(90,90,90,0.5)';
let white_color = 'rgb(255, 255, 255)';



document.addEventListener('DOMContentLoaded', function() {
    fetchAvailableCurrencies();
    currencySelectorListener();
    windowResizeListener();
    colorButtonListener();
    dragZoomListener();
});



function updateChartData(newData) {
    const newRateHistoryEntry = {
        value: newData.value,
        timestamp: normalizeDate(newData.timestamp)
    };

    //TODO THINK ABOUT THIS TIME COMPLEXITY , IS IT OKAY ?
    // INDEX [0] IS LATEST CURRENCY PAIR TIMESTAMP & VALUE
    // IS IT BETTER TO SORT DATA IN ASC ON BACKEND OR LET SORTING IT FOR CLIENT SIDE?
    // OR MAYBE LEAVE IT LIKE THIS THAT WE CAN HAVE LATEST VALUE AT FIRST INDEX

    currentData.rateHistory.unshift(newRateHistoryEntry);
    // currentData.rateHistory.push(newRateHistoryEntry);

    initializeUpdateChart(currentData, false,true);
}

function toggleDragZoom() {
    if (coinChart) {
        dragZoomDisabled = !dragZoomDisabled;
        this.classList.toggle('active');
        coinChart.options.plugins.zoom.pan.enabled = dragZoomDisabled;
        coinChart.options.plugins.zoom.zoom.drag.enabled = !dragZoomDisabled;
        coinChart.update();
        // console.log(coinChart.getZoomLevel());
        // console.log(coinChart.isZoomedOrPanned());
    }
}



function initializeUpdateChart(data, resetzoom, isUpdate = false) {
    currentData = data;

    if (!useSingleColor) {
        calculateColorPrediction();
    }

    const labels = data.rateHistory.map(entry => new Date(entry.timestamp));
    const values = data.rateHistory.map(entry => entry.value);


    const getBorderColor = (ctx) => {
        if (useSingleColor) {
            return teal_color;
        } else {
            const p0 = ctx.p0DataIndex;
            const p1 = ctx.p1DataIndex;
            if (p1 === undefined || p0 === undefined) {
                return currentChartColor;
            }
            return ctx.chart.data.datasets[0].data[p1] > ctx.chart.data.datasets[0].data[p0] ? red_color : teal_color;
        }
    };

    const getPointBorderColor = (ctx) => {
        if (useSingleColor) return teal_color;
        const index = ctx.dataIndex;
        const data = ctx.dataset.data;
        if (index === 0) return currentChartColor;
        return data[index] > data[index - 1] ? red_color : teal_color;
    };

    if (coinChart) {
        if(resetzoom) {
            coinChart.resetZoom();
            coinChart.update();
        }
        coinChart.data.labels = labels;
        coinChart.data.datasets[0].data = values;
        coinChart.data.datasets[0].label = '(' + data.symbol + ') Price';
        if(isUpdate) coinChart.update('none');

        coinChart.update();

    } else {
        const canvasObject = document.getElementById('chart').getContext('2d');
        coinChart = new Chart(canvasObject, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: '(' + data.symbol + ') Price',
                    data: values,
                    borderColor: getBorderColor,
                    tension: 0.1,
                    pointRadius: 0,
                    pointBorderColor: getPointBorderColor,
                    pointBackgroundColor: black_color_transparent,
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
                    tooltip: {
                        enabled: true,
                        intersect: false,
                        yAlign: 'top',
                        callbacks: {
                            // label: (ttItem) => (`${ttItem.formattedValue}` + '$'),
                            label: (ctx) => (`${ctx.dataset.label}: ${ctx.raw}` + '$'),
                            title: (title) => (`Date: ${title[0].label}`),
                            //     labelColor: function() {
                            //         return {
                            //             backgroundColor: black_color_solid,
                            //             borderWidth: 2,
                            //             borderRadius: 2,
                            //         };
                            //     },
                            //     labelTextColor: function() {
                            //         return white_color;
                            //     },
                        }
                    },
                    title: {
                        display: true,
                        text: data.timeWindow + ' Price Chart'
                    },
                    zoom: {
                        limits: {
                            // TODO MAKE THIS GENERIC FROM CURRENCY HIGH AND LOW TIMEWINDOW RESULT VALUE
                            // y: {min: 55000, max: 65000},
                        },
                        pan: {
                            enabled: true,
                            mode: 'xy'
                        },
                        zoom: {
                            onZoom: function ({ chart }) {
                                // coinChart.options.plugins.zoom.pan.enabled = false;
                                // coinChart.options.plugins.zoom.zoom.drag.enabled = false;
                                // coinChart.options.plugins.zoom.zoom.wheel.enabled = false;
                            },
                            onZoomComplete: function ({ chart }) {
                                //
                            },
                            wheel: {
                                mode: 'xy',
                                enabled: true,
                            },
                            pinch: {
                                mode: 'xy',
                                enabled: true
                            },
                            drag: {
                                enabled: false,
                                mode: 'xy',
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        type: 'time',
                        time: {
                            // displayFormats: {
                            //     day: "d-MMM HH:mm",
                            //     millisecond: "HH:mm:ss.SSS",
                            //     second: "HH:mm:ss",
                            //     minute: "HH:mm",
                            //     hour: "d-MMM HH:mm",
                            // },
                            // TODO IN FUTURE MAKE THIS TIME UNIT GENERIC BASED ON RECEIVED DATA TIME WINDOW AND TICK RATE INTERVAL
                            unit: 'hour'
                        },
                        ticks: {
                            color: 'white',
                            font: {
                                size: 16
                            },
                            minRotation: 0,
                            maxRotation: 25
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
                            },
                            callback: function(value, index, values) {
                                return value.toLocaleString() + '$';
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