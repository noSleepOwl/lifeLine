package com.github.vipulasri.timelineview.sample.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.github.vipulasri.timelineview.sample.Unit.Util;

import java.time.LocalTime;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineModel implements Parcelable, Comparable<TimeLineModel> {
    private OrderStatus mStatus;
    private DateseModel dateseModel;

    public static final Parcelable.Creator<TimeLineModel> CREATOR = new Parcelable.Creator<TimeLineModel>() {
        @Override
        public TimeLineModel createFromParcel(Parcel source) {
            return new TimeLineModel(source);
        }

        @Override
        public TimeLineModel[] newArray(int size) {
            return new TimeLineModel[size];
        }
    };

    protected TimeLineModel(Parcel in) {
        dateseModel.setMessage(in.readString());
//        this.mDate = in.readString();
        int tmpMStatus = in.readInt();
        this.mStatus = tmpMStatus == -1 ? null : OrderStatus.values()[tmpMStatus];
    }

    public TimeLineModel() {
    }

    public TimeLineModel(DateseModel dateseModel) {
        this.dateseModel = dateseModel;
    }


    public String getMessage() {
        return dateseModel.getMessage();
    }


    public String getDate() {
        return dateseModel.getCreateDate() + " " + dateseModel.getCreateTime();
    }


    public OrderStatus getStatus() {
        return mStatus;
    }

    public void setStatus(OrderStatus mStatus) {
        this.mStatus = mStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMessage());
        dest.writeString(getDate());
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());
    }


    @Override
    public int compareTo(@NonNull TimeLineModel other) {

        LocalTime selfTime = Util.formatTime(getCreateTime());
        LocalTime otherTime = Util.formatTime(other.getCreateTime());
        return selfTime.isBefore(otherTime) ? 1 : -1;
    }

    public String getCreateTime() {
        return this.dateseModel.getCreateTime().trim();
    }


}
