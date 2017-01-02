package com.nikoskatsanos.netty.groupchat.base.handlers;

import com.nikoskatsanos.nkjutils.yalf.YalfLogger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * <p>Should be used as the last {@link io.netty.channel.ChannelInboundHandler} on a {@link io.netty.channel.ChannelPipeline}. It simply overrides {@link
 * io.netty.channel.ChannelInboundHandler#exceptionCaught(io.netty.channel.ChannelHandlerContext, Throwable)} and logs any uncaught exception</p>
 *
 * @author nikkatsa
 */
public class ExceptionCaughtLoggerHandler extends ChannelInboundHandlerAdapter {

    private static final YalfLogger log = YalfLogger.getLogger(ExceptionCaughtLoggerHandler.class);

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);

        super.exceptionCaught(ctx, cause);
    }
}
