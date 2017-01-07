package com.nikoskatsanos.netty.groupchat.server;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nikkatsa
 */
public class UsersRegistry {

    private final Map<SocketAddress, String> users;

    public UsersRegistry() {
        this.users = new ConcurrentHashMap<>(32);
    }

    public final boolean containsSocketAddress(final SocketAddress socketAddress) {
        return users.containsKey(socketAddress);
    }

    public final boolean containsUser(final String userName) {
        return users.values().contains(userName);
    }

    public final String getUserForAddress(final SocketAddress socketAddress) {
        return users.get(socketAddress);
    }

    public final void registerUser(final SocketAddress socketAddress, final String user) {
        this.users.put(socketAddress, user);
    }

    public final int size() {
        return this.users.size();
    }

    public final void unregisterUser(final SocketAddress socketAddress) {
        if (socketAddress != null) {
            this.users.remove(socketAddress);
        }
    }
}
