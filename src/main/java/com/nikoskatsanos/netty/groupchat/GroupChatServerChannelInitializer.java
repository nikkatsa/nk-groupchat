package com.nikoskatsanos.netty.groupchat;

import com.nikoskatsanos.netty.groupchat.base.handlers.ExceptionCaughtLoggerHandler;
import com.nikoskatsanos.netty.groupchat.base.handlers.StatsHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * <p>The default Group Chat Server {@link io.netty.channel.ChannelPipeline}</p>
 *
 * @author nikkatsa
 */
public class GroupChatServerChannelInitializer extends ChannelInitializer<Channel> {

    private final StatsHandler statsHandler;
    private final LoginMsgHandler loginMsgHandler;
    private final ChannelGroup allClientsGroup;

    public GroupChatServerChannelInitializer(final ChannelGroup allClientsGroup) {
        this.statsHandler = new StatsHandler();
        this.allClientsGroup = allClientsGroup;
        this.loginMsgHandler = new LoginMsgHandler(this.allClientsGroup);
    }

    @Override
    protected void initChannel(final Channel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(64_000));
        pipeline.addLast(this.statsHandler);
        pipeline.addLast(new WebSocketServerProtocolHandler("/"));
        pipeline.addLast(new WebSocketClientHandshakeHandler());
        pipeline.addLast(this.loginMsgHandler);
        pipeline.addLast(new WebSocketMessageBroadcastHandler(this.allClientsGroup));
        pipeline.addLast(new LogoutMsgHandler(this.allClientsGroup));
        pipeline.addLast(new ExceptionCaughtLoggerHandler());
    }
}
