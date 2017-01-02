package com.nikoskatsanos.netty.groupchat.base.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.Mockito.times;

/**
 * @author nikkatsa
 */
public class ExceptionCaughtLoggerHandlerTest {

    @Spy
    private ExceptionCaughtLoggerHandler exceptionCaughtLoggerHandler = new ExceptionCaughtLoggerHandler();

    @Before
    public void setupExceptionCaughtLoggerHandlerTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testThrowingException() throws Exception {

        final EmbeddedChannel embeddedChannel = new EmbeddedChannel(new MockInboundThrowingExceptionHandler(), exceptionCaughtLoggerHandler);
        try {
            embeddedChannel.writeInbound("Hello");
        } catch (final RuntimeException e) {
            Mockito.verify(this.exceptionCaughtLoggerHandler, times(1)).exceptionCaught(ArgumentMatchers.any(), ArgumentMatchers.isA(RuntimeException.class));
        } finally {
            if (embeddedChannel != null) {
                embeddedChannel.close();
            }
        }
    }

    private static class MockInboundThrowingExceptionHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
            throw new RuntimeException("An exception was thrown");
        }
    }
}