package com.nikoskatsanos.netty.groupchat;

import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsg;
import com.nikoskatsanos.netty.groupchat.api.GroupChatLoginMsg;
import com.nikoskatsanos.nkjutils.yalf.YalfLogger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nikkatsa
 */
@ChannelHandler.Sharable
public class LoginMsgHandler extends SimpleChannelInboundHandler<BaseGroupChatMsg> {
    private static final YalfLogger log = YalfLogger.getLogger(LoginMsgHandler.class);

    private final ChannelGroup allClientsChannelGroup;
    private final Map<SocketAddress, String> users = new ConcurrentHashMap<>();

    public LoginMsgHandler(ChannelGroup allClientsChannelGroup) {
        this.allClientsChannelGroup = allClientsChannelGroup;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final BaseGroupChatMsg msg) throws Exception {
        if (msg instanceof GroupChatLoginMsg) {
            if (users.containsKey(msg.getRemoteAddress())) {
                log.info("User %s is already logged in", msg.getRemoteAddress());

                ctx.channel().writeAndFlush(new TextWebSocketFrame(String.format("You are already logged in as %s", users.get(msg.getRemoteAddress()))));
                return;
            }

            final String userName = ((GroupChatLoginMsg) msg).getUserName();
            if (users.values().contains(userName)) {
                log.info("User %s tried to login with a user name that already exists", msg.getRemoteAddress());

                ctx.channel().writeAndFlush(new TextWebSocketFrame(String.format("User %s already exists. Please choose a different user name")));
                return;
            }
            users.put(msg.getRemoteAddress(), userName);

            log.info("User %s-%s joined the chat", userName, msg.getRemoteAddress().toString());

            this.allClientsChannelGroup.add(ctx.channel());
            this.allClientsChannelGroup.writeAndFlush(new TextWebSocketFrame(String.format("%s joined the chat", ((GroupChatLoginMsg) msg).getUserName())));
            return;
        } else {
            if (!users.containsKey(msg.getRemoteAddress())) {
                ctx.channel().writeAndFlush(new TextWebSocketFrame("You are not logged in!"));
                return;
            }
            ReferenceCountUtil.retain(msg);

            final String verifiedUserName = users.get(ctx.channel().remoteAddress());
            msg.setVerifiedUserName(verifiedUserName);

            ctx.fireChannelRead(msg);
        }
    }

    public Map<SocketAddress, String> getUsers() {
        return this.users;
    }
}
