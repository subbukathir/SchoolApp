package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class CEvents implements Parcelable
{
    private String ID;
    private String Name;
    private String Description;
    private String OrganizerId;
    private String StartDate;
    private String EndDate;
    private String Organizer_First_Name;
    private String Organizer_Last_Name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(ID);
        dest.writeString(Name);
        dest.writeString(Description);
        dest.writeString(OrganizerId);
        dest.writeString(EndDate);
        dest.writeString(Organizer_First_Name);
        dest.writeString(Organizer_Last_Name);
    }

    public CEvents(Parcel source) {
        this.ID = source.readString();
        this.Name = source.readString();
        this.Description = source.readString();
        this.OrganizerId = source.readString();
        this.StartDate = source.readString();
        this.EndDate = source.readString();
        this.Organizer_First_Name = source.readString();
        this.Organizer_Last_Name = source.readString();
    }

    public static final Parcelable.Creator<CEvents> CREATOR = new Parcelable.Creator<CEvents>()
    {
        public CEvents createFromParcel(Parcel in)
        {
            return new CEvents(in);
        }
        public CEvents[] newArray(int size)
        {
            return new CEvents[size];
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

    public String getOrganizerId() {
        return OrganizerId;
    }

    public void setOrganizerId(String organizerId) {
        OrganizerId = organizerId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getOrganizer_First_Name() {
        return Organizer_First_Name;
    }

    public void setOrganizer_First_Name(String organizer_First_Name) {
        Organizer_First_Name = organizer_First_Name;
    }

    public String getOrganizer_Last_Name() {
        return Organizer_Last_Name;
    }

    public void setOrganizer_Last_Name(String organizer_Last_Name) {
        Organizer_Last_Name = organizer_Last_Name;
    }

}
