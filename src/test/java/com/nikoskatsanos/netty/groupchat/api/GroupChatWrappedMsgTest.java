package com.nikoskatsanos.netty.groupchat.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        final ObjectMapper objectMapper = new ObjectMapper();

        String s = objectMapper.writeValueAsString(loginMsg);
        System.out.println(s);

//        BaseGroupChatMsg baseGroupChatMsg = GroupChatWrappedMsg.toGroupChatMsg(s);
//        if (baseGroupChatMsg instanceof GroupChatLoginMsg) {
//            System.out.println(((GroupChatLoginMsg) baseGroupChatMsg).getUserName());
//        }
    }
}