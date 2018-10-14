package com.chanin.lincc.exdisplay.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class PushMessage {
    @Id(autoincrement = true)
    private Long id;
    private String content;
    private long time;
    @Generated(hash = 1474794901)
    public PushMessage(Long id, String content, long time) {
        this.id = id;
        this.content = content;
        this.time = time;
    }
    @Generated(hash = 1468533071)
    public PushMessage() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }


}
