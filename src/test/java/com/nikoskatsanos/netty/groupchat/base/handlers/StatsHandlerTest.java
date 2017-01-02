package com.nikoskatsanos.netty.groupchat.base.handlers;

import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author nikkatsa
 */
public class StatsHandlerTest {

    @Spy
    private StatsHandler statsHandler = new StatsHandler();

    @Before
    public void setupStatsHandlerTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConnectingAndDisconnectingClients() {
        final EmbeddedChannel channelI = new EmbeddedChannel(this.statsHandler);
        EmbeddedChannel channelII = null;
        try {
            assertTrue(channelI.isRegistered());
            assertEquals(1, this.statsHandler.getActiveConnections().getCount());
            assertTrue(this.statsHandler.getAllConnections().getCount() >= 1);

            channelII = new EmbeddedChannel(this.statsHandler);
            assertTrue(channelII.isRegistered());
            assertEquals(2, this.statsHandler.getActiveConnections().getCount());
            assertTrue(this.statsHandler.getAllConnections().getCount() >= 2);

            channelI.close();

            assertEquals(1, this.statsHandler.getActiveConnections().getCount());
            assertTrue(this.statsHandler.getAllConnections().getCount() >= 2);
        } finally {
            if (channelII != null) {
                channelII.close();
            }
        }
    }

    @Test
    public void testRequestRate() {
        final EmbeddedChannel channel = new EmbeddedChannel(this.statsHandler);
        assertTrue(channel.isRegistered());
        assertTrue(channel.isWritable());
        IntStream.range(0, 10).forEach(i -> channel.writeInbound("Hello"));

        assertEquals(10, this.statsHandler.getRequestRate().getCount());
    }
}