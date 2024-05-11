package com.spark.websocket.infrastructure.session;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class WebSocketSessionInterceptor implements WebSocketHandlerDecoratorFactory {

    private final WebSocketSessionManager sessionManager;

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                String timeZone = extractTimezoneAndSaveAsSessionAttribute(session);
                sessionManager.registerSession(session);
                super.afterConnectionEstablished(session);
                log.info("REGISTERED NEW SESSION WITH ID -> [{}] USER TIMEZONE -> [{}]", session.getId(), timeZone);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                sessionManager.removeSession(session.getId());
                super.afterConnectionClosed(session, status);
                log.info("UNREGISTERED SESSION WITH ID -> [{}]", session.getId());
            }
        };
    }

    private String extractTimezoneAndSaveAsSessionAttribute(WebSocketSession session) {
        String timeZone = Objects.requireNonNull(session.getUri()).getQuery().split("timezone=")[1];
        session.getAttributes().put("timeZone", timeZone);
        return timeZone;
    }
}