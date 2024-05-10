package com.spark.websocket.infrastructure.model;

public record WebSocketEventMessageResponse(MessageEventType type, Object payload) {
}
