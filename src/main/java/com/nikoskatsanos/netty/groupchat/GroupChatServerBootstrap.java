package com.nikoskatsanos.netty.groupchat;

import com.nikoskatsanos.jutils.core.networking.NetworkUtils;
import com.nikoskatsanos.jutils.core.threading.NamedThreadFactory;
import com.nikoskatsanos.nkjutils.yalf.YalfLogger;
import io.netty.channel.ChannelFuture;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Group chat server bootstrap</p>
 *
 * @author nikkatsa
 */
public class GroupChatServerBootstrap {

    private static final YalfLogger log = YalfLogger.getLogger(GroupChatServerBootstrap.class);

    private static final int GROUP_CHAT_SERVER_DEFAULT_PORT = 9999;
    private static final String PORT_CMD = "p";

    public static void main(final String... args) {
        try {
            final CommandLine cmd = new DefaultParser().parse(createCmdOptions(), args);

            final int port = cmd.hasOption(PORT_CMD) ? Integer.parseInt(cmd.getOptionValue(PORT_CMD)) : GROUP_CHAT_SERVER_DEFAULT_PORT;
            final InetSocketAddress serverAddress = new InetSocketAddress(NetworkUtils.getLocalAddress().getHostName(), port);

            final GroupChatServer groupChatServer = new GroupChatServer(serverAddress);
            final ExecutorService groupChatServerCaller = Executors.newSingleThreadExecutor(new NamedThreadFactory("GroupChatServerInitializer", true));
            final ChannelFuture channelFuture = groupChatServerCaller.submit(groupChatServer).get();

            Runtime.getRuntime().addShutdownHook(new NamedThreadFactory("ShutdownHook", true).newThread(() -> {
                log.info("GroupChat server shutting down");
                groupChatServer.shutdown();
                groupChatServerCaller.shutdownNow();
            }));

            channelFuture.channel().closeFuture().syncUninterruptibly();

        } catch (final ParseException e) {
            log.error(e.getMessage(), e);
        } catch (final InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static final Options createCmdOptions() {
        final Options options = new Options();

        final Option portOption = Option.builder(PORT_CMD).longOpt("port").desc("Port the group chat server will be " + "running on").hasArg(true).type
                (Integer.class).required(false).build();
        options.addOption(portOption);

        return options;
    }
}
