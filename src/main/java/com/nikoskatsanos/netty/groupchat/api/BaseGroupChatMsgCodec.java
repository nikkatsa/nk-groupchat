package com.nikoskatsanos.netty.groupchat.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

/**
 * <p>Codec for transforming a {@link com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsg} to/from JSON format</p>
 *
 * @author nikkatsa
 */
public class BaseGroupChatMsgCodec {

    private static final ObjectMapper decoder;

    private static final JsonFactory jsonMsgFactory;

    static {
        decoder = new ObjectMapper();
        jsonMsgFactory = decoder.getFactory();
    }

    private BaseGroupChatMsgCodec() {
    }

    public static final <T extends BaseGroupChatMsg> String toJson(final T msg) {
        try {
            return decoder.writeValueAsString(msg);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static final <T extends BaseGroupChatMsg> String toJson(final GroupChatWrappedMsg<T> msg) {
        try {
            return decoder.writeValueAsString(msg);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param wrappedJsonMsg A {@link com.nikoskatsanos.netty.groupchat.api.GroupChatWrappedMsg} in its string representation
     * @param <T>
     * @return A {@link com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsg} by incrementally parsing the {@code wrappedJsonMsg} and its {@code msgType}
     * field
     * @throws java.io.IOException {@link java.lang.Exception} thrown if the {@code wrappedJsonMsg} is invalid
     */
    public static <T extends BaseGroupChatMsg> T fromJson(final String wrappedJsonMsg) throws IOException {
        Objects.requireNonNull(String.format("JSON string expected for group chat message, but null found"));

        final JsonParser parser = jsonMsgFactory.createParser(wrappedJsonMsg);

        JsonToken token = parser.nextToken();
        if (token != JsonToken.START_OBJECT) {
            throw new IOException(String.format("%s expected, %s found. Cannot deserialize content [%s]", JsonToken.START_OBJECT, token, wrappedJsonMsg));
        }
        token = parser.nextToken();
        if (token != JsonToken.FIELD_NAME || !"msgType".equals(parser.getCurrentName())) {
            throw new IOException(String.format("%s \"msgType\" expected, but found %s : \"%s\". Cannot deserialize [%s]", JsonToken.FIELD_NAME, token.name()
                    , parser.getCurrentName(), wrappedJsonMsg));
        }

        token = parser.nextToken();
        if (token != JsonToken.VALUE_STRING) {
            throw new IOException(String.format("%s for msgType expected but %s found. Cannot deserialize content [%s]", JsonToken.VALUE_STRING, token,
                    wrappedJsonMsg));
        }
        final GroupChatWrappedMsg.GroupChatMsgType msgType = GroupChatWrappedMsg.GroupChatMsgType.valueOf(parser.getText());

        token = parser.nextToken();
        if (token != JsonToken.FIELD_NAME || !"msg".equals(parser.getCurrentName())) {
            throw new IOException(String.format("%s \"msg\" expected, but found %s : \"%s\". Cannot deserialize [%s]", JsonToken.FIELD_NAME, token.name(),
                    parser.getCurrentName(), wrappedJsonMsg));
        }

        token = parser.nextToken();
        if (token != JsonToken.START_OBJECT) {
            throw new IOException(String.format("%s expected for field msg, but %s found. Cannot deserialize content [%s]", JsonToken.START_OBJECT, token,
                    wrappedJsonMsg));
        }

        switch (msgType) {
            case LOGIN:
                return (T) parser.readValueAs(GroupChatLoginMsg.class);
            case LOGOUT:
                return (T) parser.readValueAs(GroupChatLogoutMsg.class);
            case MSG:
                return (T) parser.readValueAs(GroupChatMsg.class);
            default:
                throw new IOException(String.format("Unhandled msgType %s", msgType));
        }
    }
}
