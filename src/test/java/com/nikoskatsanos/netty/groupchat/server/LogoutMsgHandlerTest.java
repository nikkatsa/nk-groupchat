package com.nikoskatsanos.netty.groupchat.server;

import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsg;
import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsgCodec;
import com.nikoskatsanos.netty.groupchat.api.GroupChatLoginMsg;
import com.nikoskatsanos.netty.groupchat.api.GroupChatLogoutMsg;
import com.nikoskatsanos.netty.groupchat.api.GroupChatWrappedMsg;
import com.nikoskatsanos.netty.groupchat.server.LoginMsgHandler;
import com.nikoskatsanos.netty.groupchat.server.LogoutMsgHandler;
import com.nikoskatsanos.netty.groupchat.server.WebSocketClientHandshakeHandler;
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
public class LogoutMsgHandlerTest {

    private EmbeddedChannel channel;
    private ChannelGroup channelGroup;
    private UsersRegistry usersRegistry;
    private WebSocketClientHandshakeHandler handshakeHandler;
    private LoginMsgHandler loginMsgHandler;
    private LogoutMsgHandler logoutMsgHandler;

    @Before
    public void setupLogoutMsgHandlerTest() {
        this.channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
        this.usersRegistry = new UsersRegistry();
        this.handshakeHandler = new WebSocketClientHandshakeHandler();
        this.loginMsgHandler = new LoginMsgHandler(this.channelGroup, this.usersRegistry);
        this.logoutMsgHandler = new LogoutMsgHandler(this.channelGroup, this.usersRegistry);
        this.channel = new EmbeddedChannel(this.handshakeHandler, this.loginMsgHandler, this.logoutMsgHandler);
    }

    @After
    public void tearDownLogoutMsgHandlerTest() {
        if (this.channel != null) {
            this.channel.close();
        }
    }

    @Test
    public void testLogout() {
        assertFalse(this.channel.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>
                (GroupChatWrappedMsg.GroupChatMsgType.LOGIN, new GroupChatLoginMsg("John Doe"))))));

        assertEquals(1, this.channelGroup.size());
        assertEquals(1, this.channel.outboundMessages().size());
        assertNotNull(this.channel.readOutbound());

        this.channel.writeInbound(new TextWebSocketFrame(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>(GroupChatWrappedMsg
                .GroupChatMsgType.LOGOUT, new GroupChatLogoutMsg()))));

        assertEquals(0, this.channelGroup.size());
        assertEquals(1, this.channel.outboundMessages().size());
        final TextWebSocketFrame msg = this.channel.<TextWebSocketFrame>readOutbound();
        assertNotNull(msg);
        assertEquals("John Doe left the chat room", msg.text());
    }
}