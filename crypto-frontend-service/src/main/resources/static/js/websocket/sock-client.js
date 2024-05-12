let stompClient;

document.addEventListener('DOMContentLoaded', function() {
    connectSocket();
});

function connectSocket() {
    let reconnectInterval = 5000;
    let maxReconnectAttempts = 10;
    let reconnectAttempts = 0;

    function setupWebSocket() {
        if (stompClient && stompClient.connected) {
            stompClient.disconnect(function() {
                console.log('Disconnected');
            });
        }

        // TODO SET INJECT URL BY VALUE FROM APP.YML
        let socketUrl = `http://localhost:9002/websocket?timezone=${getUserTimeZone()}`;
        const socket = new SockJS(socketUrl);
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            reconnectAttempts = 0;

            stompClient.debug = function (msg) {
                // OVERRIDING THIS METHOD TURNS OFF CONSOLE PRINTING
            };

            let sessionId = extractSessionId(socket._transport.url);
            stompClient.subscribe('/ws/session/' + sessionId, function (response) {
                const incomingEvent = JSON.parse(response.body);
                handleIncomingEvent(incomingEvent);
            });

        }, function(error) {
            console.log('WebSocket Connection error: ', error);
            handleReconnect();
        });
    }

    function handleReconnect() {
        if (reconnectAttempts < maxReconnectAttempts) {
            reconnectAttempts++;
            console.log(`Attempting to reconnect (${reconnectAttempts}/${maxReconnectAttempts})...`);
            setTimeout(setupWebSocket, reconnectInterval);
        } else {
            console.log('Max reconnect attempts reached. Stopping reconnection attempts.');
        }
    }
    setupWebSocket();
}
