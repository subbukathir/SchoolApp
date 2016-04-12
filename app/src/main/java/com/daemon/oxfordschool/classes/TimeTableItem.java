package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class TimeTableItem implements Parcelable
{
    private String ID;
    private String Name;
    private int Type;
    private String ColorCode;

    public TimeTableItem()
    {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(ID);
        dest.writeString(Name);
        dest.writeInt(Type);
        dest.writeString(ColorCode);
    }

    public TimeTableItem(Parcel source) {
        this.ID = source.readString();
        this.Name = source.readString();
        this.Type = source.readInt();
        this.ColorCode = source.readString();
    }

    public static final Creator<TimeTableItem> CREATOR = new Creator<TimeTableItem>()
    {
        public TimeTableItem createFromParcel(Parcel in)
        {
            return new TimeTableItem(in);
        }
        public TimeTableItem[] newArray(int size)
        {
            return new TimeTableItem[size];
        }
    };

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getColorCode() {
        return ColorCode;
    }

    public void setColorCode(String colorCode) {
        ColorCode = colorCode;
    }

}
