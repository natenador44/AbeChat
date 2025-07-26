package com.abechat.server.websocket.payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DirectMessage.class, name = "DIRECT_MESSAGE")
})
public sealed interface Payload permits DirectMessage {}

