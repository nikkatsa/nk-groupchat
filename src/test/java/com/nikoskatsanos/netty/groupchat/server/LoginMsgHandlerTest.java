package com.nikoskatsanos.netty.groupchat.server;

import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsg;
import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsgCodec;
import com.nikoskatsanos.netty.groupchat.api.GroupChatLoginMsg;
import com.nikoskatsanos.netty.groupchat.api.GroupChatMsg;
import com.nikoskatsanos.netty.groupchat.api.GroupChatWrappedMsg;
import com.nikoskatsanos.netty.groupchat.server.LoginMsgHandler;
import com.nikoskatsanos.netty.groupchat.server.WebSocketClientHandshakeHandler;
import io.netty.channel.ChannelId;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author nikkatsa
 */
public class LoginMsgHandlerTest {

    private EmbeddedChannel channel;
    private WebSocketClientHandshakeHandler handshakeHandler;
    private LoginMsgHandler loginMsgHandler;
    private ChannelGroup channelGroup;

    @Before
    public void setupLoginMsgHandlerTest() {
        this.channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
        this.handshakeHandler = new WebSocketClientHandshakeHandler();
        this.loginMsgHandler = new LoginMsgHandler(this.channelGroup);
    }

    @After
    public void tearDownLoginMsgHandlerTest() {
        if (this.channel != null && this.channel.isOpen()) {
            this.channel.close();
        }
    }

    @Test
    public void testUserLogin() throws InterruptedException {
        this.channel = new EmbeddedChannel(this.handshakeHandler, this.loginMsgHandler);
        assertFalse(this.channel.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>
                (GroupChatWrappedMsg.GroupChatMsgType.LOGIN, new GroupChatLoginMsg("John Doe"))))));

        assertEquals(1, this.channelGroup.size());
        assertEquals(1, this.channel.outboundMessages().size());

        final TextWebSocketFrame produced = this.channel.<TextWebSocketFrame>readOutbound();
        assertNotNull(produced);
        assertEquals("John Doe joined the chat", produced.text());
        assertEquals(1, this.loginMsgHandler.getUsers().size());
    }

    @Test
    public void testUserTwiceLogin() {
        this.channel = new EmbeddedChannel(this.handshakeHandler, this.loginMsgHandler);
        assertFalse(this.channel.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>
                (GroupChatWrappedMsg.GroupChatMsgType.LOGIN, new GroupChatLoginMsg("John Doe"))))));
        assertFalse(this.channel.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>
                (GroupChatWrappedMsg.GroupChatMsgType.LOGIN, new GroupChatLoginMsg("John Doe 2"))))));

        assertEquals(2, this.channel.outboundMessages().size());
        final TextWebSocketFrame msgI = this.channel.<TextWebSocketFrame>readOutbound();
        assertNotNull(msgI);
        assertEquals("John Doe joined the chat", msgI.text());

        final TextWebSocketFrame msgII = this.channel.<TextWebSocketFrame>readOutbound();
        assertNotNull(msgII);
        assertEquals("You are already logged in as John Doe", msgII.text());
    }

    @Test
    public void testUserLogInWithSameUserName() {
        final ChannelId mockChannelId = Mockito.mock(ChannelId.class);
        Mockito.when(mockChannelId.toString()).thenReturn("embedded2");

        this.channel = new EmbeddedChannel(this.handshakeHandler, this.loginMsgHandler);
        final EmbeddedChannel channelII = Mockito.spy(new EmbeddedChannel(mockChannelId, this.handshakeHandler, this.loginMsgHandler));
        Mockito.doReturn(new InetSocketAddress("embedded-2", 80)).when(channelII).remoteAddress();

        assertFalse(this.channel.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>
                (GroupChatWrappedMsg.GroupChatMsgType.LOGIN, new GroupChatLoginMsg("John Doe"))))));
        assertFalse(channelII.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>(GroupChatWrappedMsg
                .GroupChatMsgType.LOGIN, new GroupChatLoginMsg("John Doe"))))));

        assertEquals(1, this.loginMsgHandler.getUsers().size());
    }

    @Test
    public void testSendMessageWithoutLogin() {
        this.channel = new EmbeddedChannel(this.handshakeHandler, this.loginMsgHandler);
        this.channel.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>(GroupChatWrappedMsg
                .GroupChatMsgType.MSG, new GroupChatMsg("hello")))));

        final TextWebSocketFrame msg = this.channel.<TextWebSocketFrame>readOutbound();
        assertNotNull(msg);
        assertEquals("You are not logged in!", msg.text());
    }

    @Test
    public void testMessageAfterLogin() {
        this.channel = new EmbeddedChannel(this.handshakeHandler, this.loginMsgHandler);
        assertFalse(this.channel.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>
                (GroupChatWrappedMsg.GroupChatMsgType.LOGIN, new GroupChatLoginMsg("John Doe"))))));

        assertEquals(1, this.channelGroup.size());
        assertEquals(1, this.channel.outboundMessages().size());

        final TextWebSocketFrame msgI = this.channel.<TextWebSocketFrame>readOutbound();
        assertNotNull(msgI);
        assertEquals("John Doe joined the chat", msgI.text());

        assertTrue(this.channel.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>
                (GroupChatWrappedMsg.GroupChatMsgType.MSG, new GroupChatMsg("hello"))))));
        final GroupChatMsg msgII = this.channel.<GroupChatMsg>readInbound();
        assertNotNull(msgII);
        assertEquals("hello", msgII.getMsg());
        assertEquals("John Doe", msgII.getVerifiedUserName());
        assertNotNull(msgII.getRemoteAddress());
    }
}