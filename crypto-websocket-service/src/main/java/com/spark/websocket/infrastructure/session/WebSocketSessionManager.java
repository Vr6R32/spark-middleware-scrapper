package com.spark.websocket.infrastructure.session;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public interface WebSocketSessionManager {

    void registerSession(WebSocketSession session);
    void removeSession(String sessionId);
    void setSessionAttribute(String sessionId, String attributeName, Object attributeValue);
    List<WebSocketSession> getAllActiveSessions();

}
