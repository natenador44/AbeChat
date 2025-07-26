package com.abechat.server.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Component
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        var handler = new WebSocketHandler();
        // for the web ui, requires active session
        registry.addHandler(handler, "/user/connect");
        // for the API, requires everything an API user requires
        registry.addHandler(handler, "/api/user/connect");
    }
}
