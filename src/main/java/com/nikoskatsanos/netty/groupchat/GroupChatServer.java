package com.nikoskatsanos.netty.groupchat;

import com.nikoskatsanos.jutils.core.threading.NamedThreadFactory;
import com.nikoskatsanos.nkjutils.yalf.YalfLogger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author nikkatsa
 */
public class GroupChatServer implements Callable<ChannelFuture> {
    private static final YalfLogger log = YalfLogger.getLogger(GroupChatServer.class);

    private final InetSocketAddress serverAddress;

    private final EventLoopGroup mainLoop;
    private final EventLoopGroup workerLoop;
    private final Class<? extends ServerChannel> transport;
    private final ChannelGroup allClientsGroup;

    public GroupChatServer(InetSocketAddress serverAddress) {
        this(serverAddress, new NioEventLoopGroup(1, new NamedThreadFactory("Main-Loop", true)), new NioEventLoopGroup(50, new NamedThreadFactory
                ("Worker-Loop", true)), NioServerSocketChannel.class);
    }

    protected GroupChatServer(InetSocketAddress serverAddress, EventLoopGroup mainLoop, EventLoopGroup workerLoop, Class<? extends ServerChannel> transport) {
        this.serverAddress = serverAddress;
        this.mainLoop = mainLoop;
        this.workerLoop = workerLoop;
        this.transport = transport;
        this.allClientsGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    }

    @Override
    public ChannelFuture call() throws Exception {
        log.info("Starting GroupChat server [%s, %s]", this.serverAddress.toString(), this.transport.getName());

        final ServerBootstrap server = new ServerBootstrap().group(this.mainLoop, this.workerLoop).channel(this.transport).localAddress(this.serverAddress)
                .childHandler(new GroupChatServerChannelInitializer(this.allClientsGroup)).validate();
        final ChannelFuture channelFuture = server.bind();
        channelFuture.syncUninterruptibly();

        log.info("Started GroupChat server at %s", this.serverAddress.toString());
        return channelFuture;
    }

    public final void shutdown() {
        log.warn("Group chat server shutdown initiated");

        if (Objects.nonNull(this.workerLoop) && !this.workerLoop.isShutdown()) {
            log.info("Shutting down workers");
            this.workerLoop.shutdownGracefully();
        }

        if (Objects.nonNull(this.mainLoop) && !this.mainLoop.isShutdown()) {
            this.mainLoop.shutdownGracefully();
        }
    }
}
