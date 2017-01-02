package com.nikoskatsanos.netty.groupchat.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * <p>User should send his user name upon joining the group chat</p>
 *
 * @author nikkatsa
 */
public class GroupChatLoginMsg extends BaseGroupChatMsg {

    @JsonProperty(required = true, value = "userName")
    private String userName;

    public GroupChatLoginMsg() {
    }

    public GroupChatLoginMsg(final String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    @Override
    public int hashCode() {
        return this.userName.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GroupChatLoginMsg)) {
            return false;
        }
        final GroupChatLoginMsg other = (GroupChatLoginMsg) obj;
        if (Objects.nonNull(this.userName) && Objects.nonNull(other.getUserName())) {
            if (this.userName.equals(other.getUserName())) {
                return true;
            }
        }
        return false;
    }
}
