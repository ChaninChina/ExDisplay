package com.chanin.lincc.exdisplay.model;

import java.util.ArrayList;

public class MessageDemoEvent {

    private ArrayList<ExClass> datas;


    public MessageDemoEvent(ArrayList<ExClass> datas) {
        this.datas = datas;
    }


    public ArrayList<ExClass> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<ExClass> datas) {
        this.datas = datas;
    }
}
