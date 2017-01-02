package com.nikoskatsanos.netty.groupchat.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>Logout message</p>
 *
 * @author nikkatsa
 */
public class GroupChatLogoutMsg extends BaseGroupChatMsg {

    @JsonProperty(required = true, value = "action")
    private final String action = "logout";

    public String getAction() {
        return action;
    }
}
