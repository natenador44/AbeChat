package com.abechat.server.websocket.payload;

import java.util.UUID;

public record DirectMessage(UUID fromUserId, UUID toUserId, String message) implements Payload {}
