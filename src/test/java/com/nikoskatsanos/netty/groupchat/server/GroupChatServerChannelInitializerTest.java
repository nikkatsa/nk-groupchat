package com.nikoskatsanos.netty.groupchat.server;

import com.nikoskatsanos.netty.groupchat.server.GroupChatServerChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * @author nikkatsa
 */
public class GroupChatServerChannelInitializerTest {

    @Spy
    private GroupChatServerChannelInitializer channelInitializer;

    @Before
    public void setupGroupChatServerChannelInitializerTest() {
        final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
        this.channelInitializer = new GroupChatServerChannelInitializer(channelGroup);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() throws Exception {
        final EmbeddedChannel channel = new EmbeddedChannel(this.channelInitializer);

        Mockito.verify(this.channelInitializer, Mockito.times(1)).initChannel(channel);

        channel.finish();
    }
}