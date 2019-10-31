package com.foxslip.faisal.bluechat;

public class ChatMessage {

    private String id;
    private String time;
    private String message;
    private boolean isMe;


    public ChatMessage(String id, String time, String message, boolean isMe) {
        this.id = id;
        this.time = time;
        this.message = message;
        this.isMe = isMe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }
}
