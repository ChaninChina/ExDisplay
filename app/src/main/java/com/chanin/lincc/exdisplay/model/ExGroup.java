package com.chanin.lincc.exdisplay.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ExGroup implements Parcelable{


    public String type;
    public String data;
    public String detail;
    public String dealState;
    public String dealTime;
    public String dealRemark;
    public String recordTime;
    public String uniqueId;

    public ExGroup(String type, String data, String detail, String dealState, String dealTime, String dealRemark, String recordTime, String uniqueId) {
        this.type = type;
        this.data = data;
        this.detail = detail;
        this.dealState = dealState;
        this.dealTime = dealTime;
        this.dealRemark = dealRemark;
        this.recordTime = recordTime;
        this.uniqueId = uniqueId;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDealState() {
        return dealState;
    }

    public void setDealState(String dealState) {
        this.dealState = dealState;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public String getDealRemark() {
        return dealRemark;
    }

    public void setDealRemark(String dealRemark) {
        this.dealRemark = dealRemark;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.data);
        dest.writeString(this.detail);
        dest.writeString(this.dealState);
        dest.writeString(this.dealTime);
        dest.writeString(this.dealRemark);
        dest.writeString(this.recordTime);
        dest.writeString(this.uniqueId);
    }

    protected ExGroup(Parcel in) {
        this.type = in.readString();
        this.data = in.readString();
        this.detail = in.readString();
        this.dealState = in.readString();
        this.dealTime = in.readString();
        this.dealRemark = in.readString();
        this.recordTime = in.readString();
        this.uniqueId = in.readString();
    }

    public static final Creator<ExGroup> CREATOR = new Creator<ExGroup>() {
        @Override
        public ExGroup createFromParcel(Parcel source) {
            return new ExGroup(source);
        }

        @Override
        public ExGroup[] newArray(int size) {
            return new ExGroup[size];
        }
    };


    @Override
    public String toString() {
        return "ExGroup{" +
                "type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", detail='" + detail + '\'' +
                ", dealState='" + dealState + '\'' +
                ", dealTime='" + dealTime + '\'' +
                ", dealRemark='" + dealRemark + '\'' +
                ", recordTime='" + recordTime + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                '}';
    }
}
