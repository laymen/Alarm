package com.microsoft.mimickeralarm.model;

import java.util.UUID;

/**
 * Created by Administrator on 2017/3/10 0010.
 */
public class Weeks {
    private UUID mId;
    private  String mMonthDay;

    public UUID getmId() {
        return mId;
    }

    public void setmId(UUID mId) {
        this.mId = mId;
    }

    public String getmMonthDay() {
        return mMonthDay;
    }

    public void setmMonthDay(String mMonthDay) {
        this.mMonthDay = mMonthDay;
    }
}
