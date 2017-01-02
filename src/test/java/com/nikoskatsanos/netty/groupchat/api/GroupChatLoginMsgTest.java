package com.nikoskatsanos.netty.groupchat.api;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author nikkatsa
 */
public class GroupChatLoginMsgTest {

    private GroupChatLoginMsg loginMsg;

    private final String expectedJson = "{\"userName\":\"John Doe\"}";
    private final String wrappedLoginMsg = String.format("{\"msgType\":\"LOGIN\",\"msg\":%s}", this.expectedJson);
    private final String wrappedRogueMsg = String.format("{\"msgType\":\"LOGOUT\",\"msg\":%s}", this.expectedJson);

    @Before
    public void setupGroupChatLoginMsgTest() {
        this.loginMsg = new GroupChatLoginMsg("John Doe");
    }

    @Test
    public void testSerializeGroupChatLoginMsg() {
        assertEquals(this.expectedJson, BaseGroupChatMsgCodec.toJson(this.loginMsg));
    }

    @Test
    public void testDeserializeGroupChatLoginMsg() throws IOException {
        assertEquals(this.loginMsg, BaseGroupChatMsgCodec.fromJson(this.wrappedLoginMsg));
    }

    @Test(expected = IOException.class)
    public void testDeserializeRogueMsg() throws IOException {
        BaseGroupChatMsgCodec.fromJson(this.wrappedRogueMsg);
    }
}