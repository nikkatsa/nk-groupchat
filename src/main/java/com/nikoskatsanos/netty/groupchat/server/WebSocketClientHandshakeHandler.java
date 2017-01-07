package com.nikoskatsanos.netty.groupchat.server;

import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsg;
import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsgCodec;
import com.nikoskatsanos.nkjutils.yalf.YalfLogger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * <p>Object responsible for performing the Websocket handshake with the client and also translating client messages to {@link
 * com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsg}</p>
 *
 * @author nikkatsa
 */
@ChannelHandler.Sharable
public class WebSocketClientHandshakeHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final YalfLogger log = YalfLogger.getLogger(WebSocketClientHandshakeHandler.class);

    public WebSocketClientHandshakeHandler() {
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            final String client = ctx.channel().remoteAddress().toString();
            log.info("%s successfully handshaked", client);
            return;
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final TextWebSocketFrame msg) throws Exception {
        try {
            final BaseGroupChatMsg baseGroupChatMsg = BaseGroupChatMsgCodec.fromJson(msg.text());
            baseGroupChatMsg.setRemoteAddress(ctx.channel().remoteAddress());
            ctx.fireChannelRead(baseGroupChatMsg);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(String.format("Unrecongized message: %s", msg.text())));
            return;
        }
    }
}
