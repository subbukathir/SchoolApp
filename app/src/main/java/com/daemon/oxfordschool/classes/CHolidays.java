package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class CHolidays implements Parcelable
{
    private String ID;
    private String HolidayDate;
    private String Description;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(ID);
        dest.writeString(HolidayDate);
        dest.writeString(Description);
    }

    public CHolidays(Parcel source) {
        this.ID = source.readString();
        this.HolidayDate = source.readString();
        this.Description = source.readString();
    }

    public static final Creator<CHolidays> CREATOR = new Creator<CHolidays>()
    {
        public CHolidays createFromParcel(Parcel in)
        {
            return new CHolidays(in);
        }
        public CHolidays[] newArray(int size)
        {
            return new CHolidays[size];
        }
    };

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getHolidayDate() {
        return HolidayDate;
    }

    public void setHolidayDate(String holidayDate) {
        HolidayDate = holidayDate;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
