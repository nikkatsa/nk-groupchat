package com.nikoskatsanos.netty.groupchat.api;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author nikkatsa
 */
public class BaseGroupChatMsgCodecTest {

    @Test
    public void testToJson() {
        final BaseGroupChatMsg msgI = new GroupChatLogoutMsg();
        assertEquals("{\"action\":\"logout\"}", BaseGroupChatMsgCodec.toJson(msgI));
        final BaseGroupChatMsg msgII = new GroupChatLoginMsg("John Doe");
        assertEquals("{\"userName\":\"John Doe\"}", BaseGroupChatMsgCodec.toJson(msgII));
        final BaseGroupChatMsg msgIII = new GroupChatMsg("hello world");
        assertEquals("{\"msg\":\"hello world\"}", BaseGroupChatMsgCodec.toJson(msgIII));
    }

    @Test(expected = IOException.class)
    public void testFromJson_emptyString() throws IOException {
        BaseGroupChatMsgCodec.fromJson("");
    }

    @Test(expected = NullPointerException.class)
    public void testFromJson_nullString() throws IOException {
        BaseGroupChatMsgCodec.fromJson(null);
    }

    @Test(expected = IOException.class)
    public void testFromJson_withoutMsgType() throws IOException {
        BaseGroupChatMsgCodec.fromJson("{\"msg\":{\"action\":\"logout\"}}");
    }

    @Test(expected = IOException.class)
    public void testFromJson_withInvalidMsgType() throws IOException {
        BaseGroupChatMsgCodec.fromJson("{\"msgType\": 1 ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromJson_withInvalidMsgTypeValue() throws IOException {
        BaseGroupChatMsgCodec.fromJson("{\"msgType\": \"FOO\" ");
    }

    @Test(expected = IOException.class)
    public void testFromJson_withoutMsg() throws IOException {
        BaseGroupChatMsgCodec.fromJson("{\"msgType\": \"LOGIN\", \"msg1\" : {} ");
    }

    @Test(expected = IOException.class)
    public void testFromJson_withInvalidMsg() throws IOException {
        BaseGroupChatMsgCodec.fromJson("{\"msgType\": \"LOGIN\", \"msg\" : \"Hello\" ");
    }
}