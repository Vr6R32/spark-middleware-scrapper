package com.spark.websocket.infrastructure.session;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryWebSocketSessionManager implements WebSocketSessionManager {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void registerSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public void setSessionAttribute(String sessionId, String attributeName, Object attributeValue) {
        WebSocketSession session = getSessionById(sessionId);
        if (session != null && session.isOpen()) {
            session.getAttributes().put(attributeName, attributeValue);
        }
    }
    public List<WebSocketSession> getAllActiveSessions() {
        return new CopyOnWriteArrayList<>(sessions.values());
    }

    private WebSocketSession getSessionById(String sessionId) {
        return sessions.get(sessionId);
    }
}