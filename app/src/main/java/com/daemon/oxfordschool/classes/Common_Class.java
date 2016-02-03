package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class Common_Class implements Parcelable
{
    private String ID;
    private String Name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(ID);
        dest.writeString(Name);
    }

    public Common_Class(Parcel source) {
        this.ID = source.readString();
        this.Name = source.readString();
    }

    public static final Parcelable.Creator<Common_Class> CREATOR = new Parcelable.Creator<Common_Class>()
    {
        public Common_Class createFromParcel(Parcel in)
        {
            return new Common_Class(in);
        }
        public Common_Class[] newArray(int size)
        {
            return new Common_Class[size];
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

}
