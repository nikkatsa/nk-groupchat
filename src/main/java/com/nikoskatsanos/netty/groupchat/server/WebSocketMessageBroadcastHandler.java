package com.nikoskatsanos.netty.groupchat.server;

import com.nikoskatsanos.netty.groupchat.api.GroupChatMsg;
import com.nikoskatsanos.nkjutils.yalf.YalfLogger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author nikkatsa
 */
public class WebSocketMessageBroadcastHandler extends SimpleChannelInboundHandler<GroupChatMsg> {

    private static final YalfLogger log = YalfLogger.getLogger(WebSocketMessageBroadcastHandler.class);

    private final ChannelGroup allClientsGroup;

    public WebSocketMessageBroadcastHandler(final ChannelGroup allClientsGroup) {
        this.allClientsGroup = allClientsGroup;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final GroupChatMsg msg) throws Exception {
        final String broadcastMsg = String.format("%s says: \"%s\"", msg.getVerifiedUserName(), msg.getMsg());
        log.info("%s", broadcastMsg);
        this.allClientsGroup.writeAndFlush(new TextWebSocketFrame(broadcastMsg));
    }
}
