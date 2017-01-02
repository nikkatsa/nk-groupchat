package com.nikoskatsanos.netty.groupchat.base.handlers;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.nikoskatsanos.nkjutils.synthetic.metrics.MetricsFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * <p>{@link io.netty.channel.ChannelInboundHandlerAdapter} which stores some basic connection metrics</p>
 *
 * @author nikkatsa
 */
@ChannelHandler.Sharable
public class StatsHandler extends ChannelInboundHandlerAdapter {

    private final Counter allConnections;
    private final Counter activeConnections;
    private final Meter requestRate;

    public StatsHandler() {
        this.allConnections = MetricsFactory.createCounter("AllConnections");
        this.activeConnections = MetricsFactory.createCounter("ActiveConnections");
        this.requestRate = MetricsFactory.createMeter("RequestRate");
    }

    @Override
    public void channelRegistered(final ChannelHandlerContext ctx) throws Exception {
        this.allConnections.inc();
        this.activeConnections.inc();

        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        this.activeConnections.dec();

        super.channelUnregistered(ctx);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        this.requestRate.mark();

        super.channelRead(ctx, msg);
    }

    public Counter getAllConnections() {
        return allConnections;
    }

    public Counter getActiveConnections() {
        return activeConnections;
    }

    public Meter getRequestRate() {
        return requestRate;
    }
}
