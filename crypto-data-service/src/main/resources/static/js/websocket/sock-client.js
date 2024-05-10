let stompClient;
let sessionId;

document.addEventListener('DOMContentLoaded', function() {
    connectSocket();
});

function extractSessionId(socketUrl) {
    const parts = socketUrl.split('/');
    return parts[5];
}

function connectSocket(){
    if (stompClient && stompClient.connected) {
        stompClient.disconnect(function() {
            console.log('Disconnected');
        });
    }

    // TODO SET INJECT URL BY VALUE FROM APP.YML
    // let socketUrl = document.getElementById('websocketUrl').value;
    let socketUrl = 'http://localhost:9002/websocket'

    const socket = new SockJS(socketUrl);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {

        sessionId = extractSessionId(socket._transport.url);

        stompClient.subscribe('/ws/session/' + sessionId, function (response) {
            const incomingEvent = JSON.parse(response.body);
            handleIncomingEvent(incomingEvent);
        });

        stompClient.send("/ws/online", {}, JSON.stringify({ testMessage: "CONNECT MESSAGE" }));

    }, function(error) {
        console.log('Connection error: ', error);
    });
}