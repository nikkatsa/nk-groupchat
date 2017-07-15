package com.nikoskatsanos.netty.groupchat.api;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author nikkatsa
 */
public class GroupChatWrappedMsgTest {

    @Test
    public void testJsonSerialization() throws IOException {
        final GroupChatWrappedMsg<GroupChatLoginMsg> loginMsg = new GroupChatWrappedMsg<>(GroupChatWrappedMsg.GroupChatMsgType.LOGIN, new GroupChatLoginMsg
                ("John Doe"));

        assertEquals("{\"msgType\":\"LOGIN\",\"msg\":{\"userName\":\"John Doe\"}}", BaseGroupChatMsgCodec.toJson(loginMsg));

        final GroupChatWrappedMsg<GroupChatLogoutMsg> logoutMsg = new GroupChatWrappedMsg<>();
        logoutMsg.setMsgType(GroupChatWrappedMsg.GroupChatMsgType.LOGOUT);
        logoutMsg.setMsg(new GroupChatLogoutMsg());

        assertEquals("{\"msgType\":\"LOGOUT\",\"msg\":{\"action\":\"logout\"}}", BaseGroupChatMsgCodec.toJson(logoutMsg));
    }
}