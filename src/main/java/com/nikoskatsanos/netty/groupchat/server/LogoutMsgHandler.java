package com.nikoskatsanos.netty.groupchat.server;

import com.nikoskatsanos.netty.groupchat.api.GroupChatLogoutMsg;
import com.nikoskatsanos.nkjutils.yalf.YalfLogger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author nikkatsa
 */
public class LogoutMsgHandler extends SimpleChannelInboundHandler<GroupChatLogoutMsg> {
    private static final YalfLogger log = YalfLogger.getLogger(LogoutMsgHandler.class);

    private final ChannelGroup allClientsGroup;
    private final UsersRegistry usersRegistry;

    public LogoutMsgHandler(ChannelGroup allClientsGroup, UsersRegistry usersRegistry) {
        this.allClientsGroup = allClientsGroup;
        this.usersRegistry = usersRegistry;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final GroupChatLogoutMsg msg) throws Exception {
        log.info("%s[%s] logging out", msg.getVerifiedUserName(), ctx.channel().remoteAddress());
        this.allClientsGroup.writeAndFlush(new TextWebSocketFrame(String.format("%s left the chat room", msg.getVerifiedUserName())));
        ctx.channel().close();
        this.usersRegistry.unregisterUser(ctx.channel().remoteAddress());
    }
}
