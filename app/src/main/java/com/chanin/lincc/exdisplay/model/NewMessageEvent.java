package com.chanin.lincc.exdisplay.model;

public class NewMessageEvent {

    private String content;

    public NewMessageEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
