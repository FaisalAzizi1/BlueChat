package com.foxslip.faisal.bluechat;

import java.util.List;

public class Conversation {

    private String id;
    private String timeAndDate;
    private List<ChatMessage> conversation;
    private List<String> is_me;
    private String me;
    private String other;

    public Conversation(String id, String timeAndDate, List<ChatMessage> conversation, List<String> is_me, String me, String other) {
        this.id = id;
        this.timeAndDate = timeAndDate;
        this.conversation = conversation;
        this.is_me = is_me;
        this.me = me;
        this.other = other;
    }

    public Conversation(String id, String timeAndDate, List<ChatMessage> conversation, String me, String other) {
        this.id = id;
        this.timeAndDate = timeAndDate;
        this.conversation = conversation;
        this.me = me;
        this.other = other;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimeAndDate() {
        return timeAndDate;
    }

    public void setTimeAndDate(String timeAndDate) {
        this.timeAndDate = timeAndDate;
    }

    public List<ChatMessage> getConversation() {
        return conversation;
    }

    public void setConversation(List<ChatMessage> conversation) {
        this.conversation = conversation;
    }

    public List<String> getIs_me() {
        return is_me;
    }

    public void setIs_me(List<String> is_me) {
        this.is_me = is_me;
    }

    public String getMe() {
        return me;
    }

    public void setMe(String me) {
        this.me = me;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
