package com.nikoskatsanos.netty.groupchat.server;

import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsg;
import com.nikoskatsanos.netty.groupchat.api.GroupChatLoginMsg;
import com.nikoskatsanos.nkjutils.yalf.YalfLogger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;

/**
 * @author nikkatsa
 */
@ChannelHandler.Sharable
public class LoginMsgHandler extends SimpleChannelInboundHandler<BaseGroupChatMsg> {
    private static final YalfLogger log = YalfLogger.getLogger(LoginMsgHandler.class);

    private final ChannelGroup allClientsChannelGroup;
    private final UsersRegistry usersRegistry;

    public LoginMsgHandler(ChannelGroup allClientsChannelGroup, UsersRegistry usersRegistry) {
        this.allClientsChannelGroup = allClientsChannelGroup;
        this.usersRegistry = usersRegistry;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final BaseGroupChatMsg msg) throws Exception {
        if (msg instanceof GroupChatLoginMsg) {
            if (usersRegistry.containsSocketAddress(msg.getRemoteAddress())) {
                log.info("User %s is already logged in", msg.getRemoteAddress());

                ctx.channel().writeAndFlush(new TextWebSocketFrame(String.format("You are already logged in as %s", usersRegistry.getUserForAddress(msg
                        .getRemoteAddress()))));
                return;
            }

            final String userName = ((GroupChatLoginMsg) msg).getUserName();
            if (usersRegistry.containsUser(userName)) {
                log.info("User %s tried to login with a user name that already exists", msg.getRemoteAddress());

                ctx.channel().writeAndFlush(new TextWebSocketFrame(String.format("User %s already exists. Please choose a different user name", userName)));
                return;
            }
            usersRegistry.registerUser(msg.getRemoteAddress(), userName);

            log.info("User %s-%s joined the chat", userName, msg.getRemoteAddress().toString());

            this.allClientsChannelGroup.add(ctx.channel());
            this.allClientsChannelGroup.writeAndFlush(new TextWebSocketFrame(String.format("%s joined the chat", ((GroupChatLoginMsg) msg).getUserName())));
            return;
        } else {
            if (!usersRegistry.containsSocketAddress(ctx.channel().remoteAddress())) {
                ctx.channel().writeAndFlush(new TextWebSocketFrame("You are not logged in!"));
                return;
            }
            ReferenceCountUtil.retain(msg);

            final String verifiedUserName = usersRegistry.getUserForAddress(ctx.channel().remoteAddress());
            msg.setVerifiedUserName(verifiedUserName);

            ctx.fireChannelRead(msg);
        }
    }

    public final UsersRegistry getUsersRegistry() {
        return this.usersRegistry;
    }
}
