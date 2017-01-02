package com.nikoskatsanos.netty.groupchat.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;

/**
 * <p></p>
 *
 * @author nikkatsa
 */
public class GroupChatWrappedMsg<T extends BaseGroupChatMsg> implements Serializable {

    @JsonProperty(required = true, value = "msgType")
    private GroupChatMsgType msgType;
    @JsonProperty(required = true, value = "msg")
    private T msg;

    public GroupChatWrappedMsg() {
    }

    public GroupChatWrappedMsg(final GroupChatMsgType msgType, final T msg) {
        this.msgType = msgType;
        this.msg = msg;
    }

    public GroupChatMsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(final GroupChatMsgType msgType) {
        this.msgType = msgType;
    }

    public T getMsg() {
        return msg;
    }

    public void setMsg(final T msg) {
        this.msg = msg;
    }

    public static enum GroupChatMsgType {
        LOGIN, LOGOUT, MSG
    }

}
