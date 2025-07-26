package com.abechat.server.websocket;

import com.abechat.server.websocket.payload.Payload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    // https://www.devglan.com/spring-boot/spring-websocket-integration-example-without-stomp

    @Override
    protected void handleTextMessage(WebSocketSession session, @NonNull TextMessage message) throws Exception {
        // I think session is an individual client
        // message is what was sent in from that client
        logger.info("Message received from {}: {}", session.getRemoteAddress(), message.getPayload());
        Payload payload = MAPPER.readValue(message.getPayload(), Payload.class);
        session.sendMessage(new TextMessage(MAPPER.writeValueAsString(payload)));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Connection established from {}", session.getRemoteAddress());
    }
}
