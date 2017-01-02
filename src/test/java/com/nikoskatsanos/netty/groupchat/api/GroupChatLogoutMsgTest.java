package com.nikoskatsanos.netty.groupchat.api;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author nikkatsa
 */
public class GroupChatLogoutMsgTest {

    private final String expectedJsonString = "{\"action\":\"logout\"}";
    private final String wrappedLogoutMsg = String.format("{\"msgType\":\"LOGOUT\",\"msg\":%s}", this.expectedJsonString);
    private final String rogueWrappedLogoutMsg = String.format("{\"msgType\":\"LOGOUT\",\"msg\": {\"actions\":\"logout\"} }");

    @Test
    public void testSerializeGroupChatLogoutMsg() {
        assertEquals(this.expectedJsonString, BaseGroupChatMsgCodec.toJson(new GroupChatLogoutMsg()));
    }

    @Test
    public void testDeserializeGroupChatLogoutMsg() throws IOException {
        assertTrue(BaseGroupChatMsgCodec.fromJson(this.wrappedLogoutMsg) instanceof GroupChatLogoutMsg);
    }

    @Test(expected = UnrecognizedPropertyException.class)
    public void testDeserializeRogueGroupChatLogoutMsg() throws IOException {
        BaseGroupChatMsgCodec.fromJson(this.rogueWrappedLogoutMsg);
    }
}