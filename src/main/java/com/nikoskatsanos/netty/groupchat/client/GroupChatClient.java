package com.nikoskatsanos.netty.groupchat.client;

import com.nikoskatsanos.jutils.core.threading.NamedThreadFactory;
import com.nikoskatsanos.nkjutils.yalf.YalfLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker13;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author nikkatsa
 */
public class GroupChatClient implements Callable<Channel> {

    private static final YalfLogger log = YalfLogger.getLogger(GroupChatClient.class);

    private final InetSocketAddress serverAddress;
    private final URI wsURI;
    private final EventLoopGroup mainLoop;
    private Channel channel;

    private GroupChatClient(final String host, final int port) {
        this(InetSocketAddress.createUnresolved(host, port));
    }

    private GroupChatClient(final InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress;
        this.wsURI = URI.create(String.format("ws://%s:%d", this.serverAddress.getHostName(), this.serverAddress.getPort()));
        this.mainLoop = new NioEventLoopGroup(1, new NamedThreadFactory("MainLoop", true));
    }

    @Override
    public Channel call() throws Exception {
        log.info("Establishing connection to Web Socket server at %s", this.wsURI.toString());

        final WebSocketClientHandshaker wsHandshaker = new WebSocketClientHandshaker13(this.wsURI, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()
                , 65_000);
        final Bootstrap client = new Bootstrap().group(this.mainLoop).channel(NioSocketChannel.class).remoteAddress(this.serverAddress).handler(new
                WebSocketClientChannelInitializer(wsHandshaker)).validate();
        this.channel = client.connect().syncUninterruptibly().channel();

        this.ping();
        do {
        } while (!wsHandshaker.isHandshakeComplete());

        log.info("Connected to Web Socket Server %s", this.wsURI.toString());
        return null;
    }

    public final void ping() {
        this.mainLoop.submit(() -> {
            ensureChannelOpen();
            this.channel.writeAndFlush(new PingWebSocketFrame());
        });
    }

    public final void send(final String msg) {
        this.mainLoop.submit(() -> {
            ensureChannelOpen();
            log.info(">> %s", msg);
            final TextWebSocketFrame wsMsg = new TextWebSocketFrame(msg);
            channel.writeAndFlush(wsMsg);
        });
    }

    private void ensureChannelOpen() {
        Objects.requireNonNull(this.channel, () -> "Connection to web socket server is not established");
        if (!this.channel.isOpen() && !this.channel.isWritable()) {
            throw new RuntimeException(String.format("Cannot send a message to web socket server %s", wsURI.toString()));
        }
    }

    public final void shutDown() {
        log.warn("Closing web socket client at %s", this.wsURI.toString());
        if (!this.mainLoop.isShutdown()) {
            this.mainLoop.shutdownGracefully().syncUninterruptibly();
        }
    }

    private class WebSocketClientChannelInitializer extends ChannelInitializer<Channel> {

        private final WebSocketClientHandshaker wsHandshaker;

        public WebSocketClientChannelInitializer(final WebSocketClientHandshaker wsHandshaker) {
            this.wsHandshaker = wsHandshaker;
        }

        @Override
        protected void initChannel(final Channel ch) throws Exception {
            final ChannelPipeline pipeline = ch.pipeline();

            pipeline.addLast(new HttpClientCodec());
            pipeline.addLast(new HttpObjectAggregator(65_000));
            pipeline.addLast(new WebSocketClientProtocolHandler(this.wsHandshaker));
            pipeline.addLast(new GroupChatClientStdOutLoggingHandler());
        }
    }

    private class GroupChatClientStdOutLoggingHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, final TextWebSocketFrame msg) throws Exception {
            log.info(msg.text());
        }

        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
            log.error(cause.getMessage(), cause);
        }
    }

    public static final class GroupChatClientBuilder {

        private volatile String host;
        private volatile int port;
        private volatile InetSocketAddress serverAddress;

        private GroupChatClientBuilder() {
        }

        public static final GroupChatClientBuilder newBuilder() {
            return new GroupChatClientBuilder();
        }

        public final GroupChatClientBuilder withHost(final String host) {
            this.host = host;
            return this;
        }

        public final GroupChatClientBuilder withPort(final int port) {
            this.port = port;
            return this;
        }

        public final GroupChatClientBuilder withServerAddress(final InetSocketAddress serverAddress) {
            this.serverAddress = serverAddress;
            return this;
        }

        public final GroupChatClient build() {
            if (Objects.isNull(this.serverAddress)) {
                Objects.requireNonNull(this.host, () -> "ServerAddress or Host needs to be specified");

                if (this.port <= 0) {
                    throw new IllegalArgumentException(String.format("A valid port needs to be specified. Port = %d", this.port));
                }

                this.serverAddress = InetSocketAddress.createUnresolved(this.host, this.port);
            }

            return new GroupChatClient(this.serverAddress);
        }
    }
}
