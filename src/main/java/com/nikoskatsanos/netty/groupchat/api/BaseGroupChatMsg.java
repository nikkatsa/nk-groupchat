package com.nikoskatsanos.netty.groupchat.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.net.SocketAddress;

/**
 * <p>Base class that all messages should extend for JSON serialization/deserialization purposes</p>
 *
 * @author nikkatsa
 */
public abstract class BaseGroupChatMsg implements Serializable {

    @JsonIgnore
    protected transient String verifiedUserName;

    @JsonIgnore
    protected transient SocketAddress remoteAddress;

    public BaseGroupChatMsg() {
    }

    public BaseGroupChatMsg(final String verifiedUserName, final SocketAddress remoteAddress) {
        this.verifiedUserName = verifiedUserName;
        this.remoteAddress = remoteAddress;
    }

    public String getVerifiedUserName() {
        return verifiedUserName;
    }

    public void setVerifiedUserName(final String verifiedUserName) {
        this.verifiedUserName = verifiedUserName;
    }

    public SocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public void setRemoteAddress(final SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
