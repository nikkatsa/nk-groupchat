package com.nikoskatsanos.netty.groupchat.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * <p>A {@link com.nikoskatsanos.netty.groupchat.api.BaseGroupChatMsg} which is sent to everybody</p>
 *
 * @author nikkatsa
 */
public class GroupChatMsg extends BaseGroupChatMsg {

    @JsonProperty(required = true, value = "msg")
    private String msg;

    public GroupChatMsg() {
    }

    public GroupChatMsg(final String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupChatMsg)) {
            return false;
        }
        final GroupChatMsg that = (GroupChatMsg) o;
        return Objects.equals(this.msg, that.msg) && Objects.equals(super.verifiedUserName, that.getVerifiedUserName()) && Objects.equals(this.remoteAddress,
                that.getRemoteAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(msg, super.verifiedUserName, super.remoteAddress);
    }
}
