package com.nikoskatsanos.netty.groupchat.server;

import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsg;
import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsgCodec;
import com.nikoskatsanos.netty.groupchat.api.GroupChatLoginMsg;
import com.nikoskatsanos.netty.groupchat.api.GroupChatMsg;
import com.nikoskatsanos.netty.groupchat.api.GroupChatWrappedMsg;
import com.nikoskatsanos.netty.groupchat.server.LoginMsgHandler;
import com.nikoskatsanos.netty.groupchat.server.WebSocketClientHandshakeHandler;
import com.nikoskatsanos.netty.groupchat.server.WebSocketMessageBroadcastHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author nikkatsa
 */
public class WebSocketMessageBroadcastHandlerTest {

    private EmbeddedChannel channel;
    private ChannelGroup channelGroup;
    private WebSocketClientHandshakeHandler handshakeHandler;
    private LoginMsgHandler loginMsgHandler;
    private WebSocketMessageBroadcastHandler messageBroadcastHandler;

    @Before
    public void setupWebSocketMessageBroadcastHandlerTest() {
        this.channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
        this.handshakeHandler = new WebSocketClientHandshakeHandler();
        this.loginMsgHandler = new LoginMsgHandler(this.channelGroup);
        this.messageBroadcastHandler = new WebSocketMessageBroadcastHandler(this.channelGroup);
        this.channel = new EmbeddedChannel(this.handshakeHandler, this.loginMsgHandler, this.messageBroadcastHandler);
    }

    @After
    public void tearDownWebSocketMessageBroadcastHandlerTest() {
        if (this.channel != null) {
            this.channel.close();
        }
    }

    @Test
    public void testMessageBroadcast() {
        assertFalse(this.channel.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>
                (GroupChatWrappedMsg.GroupChatMsgType.LOGIN, new GroupChatLoginMsg("John Doe"))))));

        assertEquals(1, this.channelGroup.size());
        assertEquals(1, this.channel.outboundMessages().size());
        assertNotNull(this.channel.readOutbound());

        assertFalse(this.channel.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>
                (GroupChatWrappedMsg.GroupChatMsgType.MSG, new GroupChatMsg("Hello World!"))))));

        final TextWebSocketFrame msg = this.channel.<TextWebSocketFrame>readOutbound();
        assertNotNull(msg);
        assertEquals("John Doe says: \"Hello World!\"", msg.text());
    }
}