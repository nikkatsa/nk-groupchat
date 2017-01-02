package com.nikoskatsanos.netty.groupchat.api;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author nikkatsa
 */
public class GroupChatMsgTest {

    private GroupChatMsg groupChatMsg;
    private final String expectedJsonString = "{\"msg\":\"hello world!\"}";
    private final String wrappedGroupChatMsg = String.format("{\"msgType\":\"MSG\", \"msg\":%s}", this.expectedJsonString);

    @Before
    public void setupGroupChatMsgTest() {
        this.groupChatMsg = new GroupChatMsg("hello world!");
        this.groupChatMsg.setVerifiedUserName("John Doe");
    }

    @Test
    public void testSerializeGroupChatMsg() {
        assertEquals(this.expectedJsonString, BaseGroupChatMsgCodec.toJson(this.groupChatMsg));
    }

    @Test
    public void testDeserializeGroupChatMsg() throws IOException {
        final GroupChatMsg groupChatMsg = (GroupChatMsg) BaseGroupChatMsgCodec.fromJson(this.wrappedGroupChatMsg);
        groupChatMsg.setVerifiedUserName("John Doe");
        assertEquals(this.groupChatMsg, groupChatMsg);
    }
}