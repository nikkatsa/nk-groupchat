package com.nikoskatsanos.netty.groupchat.client;

import com.nikoskatsanos.jutils.core.networking.NetworkUtils;
import com.nikoskatsanos.jutils.core.threading.NamedThreadFactory;
import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsg;
import com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsgCodec;
import com.nikoskatsanos.netty.groupchat.api.GroupChatLoginMsg;
import com.nikoskatsanos.netty.groupchat.api.GroupChatLogoutMsg;
import com.nikoskatsanos.netty.groupchat.api.GroupChatMsg;
import com.nikoskatsanos.netty.groupchat.api.GroupChatWrappedMsg;
import com.nikoskatsanos.nkjutils.yalf.YalfLogger;
import io.netty.channel.Channel;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author nikkatsa
 */
public class StdInWsClient {
    private static final YalfLogger log = YalfLogger.getLogger(StdInWsClient.class);

    private static final String LOGIN_CMD_OPT = "login";
    private static final String MSG_CMD_OPT = "msg";
    private static final String LOGOUT_CMD_OPT = "logout";
    private static final String EXIT_CMD_OPT = "exit";

    public static void main(final String... args) {

        final GroupChatClient client = GroupChatClient.GroupChatClientBuilder.newBuilder().withHost(NetworkUtils.getLocalAddress().getHostName()).withPort
                (9999).build();
        final ExecutorService stdInClient = Executors.newSingleThreadExecutor(new NamedThreadFactory("StdInClient", false));
        try {
            final Channel channel = stdInClient.submit(client).get();

            log.info(createHelpMessage());

            final DefaultParser cmdParse = new DefaultParser();

            final Options cmdLineOpts = getCmdLineChatOptions();

            boolean exit = false;
            final Scanner in = new Scanner(System.in);
            do {
                final String msg = String.format("-%s", in.nextLine());
                final String[] optArgs = msg.split(" ");
                final String opt = msg.split(" ")[0];
                final String optVal = optArgs.length > 1 ? String.join(" ", Arrays.copyOfRange(optArgs, 1, optArgs.length)) : "";

                try {
                    final CommandLine cmdOpt = cmdParse.parse(cmdLineOpts, new String[]{opt, optVal});
                    final String action = cmdOpt.getOptions().length > 0 ? cmdOpt.getOptions()[0].getOpt() : "Unknown";
                    switch (action) {
                        case "login":
                            client.send(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>(GroupChatWrappedMsg.GroupChatMsgType.LOGIN,
                                    new GroupChatLoginMsg(cmdOpt.getOptions()[0].getValue()))));
                            break;
                        case "msg":
                            client.send(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<BaseGroupChatMsg>(GroupChatWrappedMsg.GroupChatMsgType.MSG, new
                                    GroupChatMsg(cmdOpt.getOptions()[0].getValue()))));
                            break;
                        case "logout":
                            client.send(BaseGroupChatMsgCodec.toJson(new GroupChatWrappedMsg<GroupChatLogoutMsg>(GroupChatWrappedMsg.GroupChatMsgType.LOGOUT,
                                    new GroupChatLogoutMsg())));
                        case "exit":
                            exit = true;
                            break;
                        default:
                            log.info("Unrecognized action %s", action);
                    }
                } catch (final ParseException e) {
                    log.error(e.getMessage(), e);
                }
            } while (!exit);
        } catch (final InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }

        if (!stdInClient.isShutdown()) {
            stdInClient.shutdownNow();
        }
    }

    private static final Options getCmdLineChatOptions() {
        final Options options = new Options();
        options.addOption(Option.builder(LOGIN_CMD_OPT).hasArg(true).argName("UserName").required(false).desc("Login to the group chat by choosing a nick " +
                "name").build());
        options.addOption(Option.builder(MSG_CMD_OPT).hasArg(true).argName("Message").required(false).desc("The message to send to the web socket server")
                .build());
        options.addOption(Option.builder(LOGOUT_CMD_OPT).hasArg(false).required(false).desc("Logout of the group chat").build());
        options.addOption(Option.builder(EXIT_CMD_OPT).hasArg(false).required(false).desc("Exit the application").build());
        return options;
    }

    private static final String createHelpMessage() {
        final StringWriter helpWriter = new StringWriter(256);
        new PrintWriter(helpWriter);
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(new PrintWriter(helpWriter), 120, "${COMMAND} [${ARG}]", "********", getCmdLineChatOptions(), 5, 2, "********");
        return helpWriter.toString();
    }
}
