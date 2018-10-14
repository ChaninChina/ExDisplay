package com.chanin.lincc.exdisplay.model;

public class MessageDetail {

    public String content;
    public String time;

    public MessageDetail(String content, String time) {
        this.content = content;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
