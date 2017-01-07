package com.nikoskatsanos.netty.groupchat.server;

import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsgCodec;
import com.nikoskatsanos.netty.groupchat.api.GroupChatLoginMsg;
import com.nikoskatsanos.netty.groupchat.api.GroupChatWrappedMsg;
import com.nikoskatsanos.netty.groupchat.server.WebSocketClientHandshakeHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author nikkatsa
 */
public class WebSocketClientHandshakeHandlerTest {

    private WebSocketClientHandshakeHandler handshakeHandler;

    @Before
    public void setupWebSocketClientHandshakeHandlerTest() {
        this.handshakeHandler = new WebSocketClientHandshakeHandler();
    }

    @After
    public void tearDownWebSocketClientHandshakeHandlerTest() {
    }

    @Test
    public void testMessageHandling() {
        final EmbeddedChannel channel = new EmbeddedChannel(this.handshakeHandler);
        assertTrue(channel.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg(GroupChatWrappedMsg.GroupChatMsgType
                .LOGIN, new GroupChatLoginMsg("John Doe"))))));
        assertTrue(channel.finish());

        final GroupChatLoginMsg produced = channel.<GroupChatLoginMsg>readInbound();
        assertEquals("John Doe", produced.getUserName());
        assertNotNull(produced.getRemoteAddress());
        assertEquals(channel.remoteAddress(), produced.getRemoteAddress());
    }

    @Test
    public void testMessageHandling_unparsedJsonString() {
        final EmbeddedChannel channel = new EmbeddedChannel(this.handshakeHandler);
        assertFalse(channel.writeInbound(new TextWebSocketFrame("Hello")));
        assertTrue(channel.finish());

        final TextWebSocketFrame produced = channel.<TextWebSocketFrame>readOutbound();
        assertEquals("Unrecongized message: Hello", produced.text());
    }
}