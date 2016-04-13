package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class Discipline implements Parcelable
{
    private String ID;
    private String StudentId;
    private String SelfControl;
    private String ObeyRules;
    private String ObeyStaff;
    private String DressCode;
    private String TimeKeeping;
    private String Conduct;
    private String success;
    private String message;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(ID);
        dest.writeString(StudentId);
        dest.writeString(SelfControl);
        dest.writeString(ObeyRules);
        dest.writeString(ObeyStaff);
        dest.writeString(DressCode);
        dest.writeString(TimeKeeping);
        dest.writeString(Conduct);
        dest.writeString(success);
        dest.writeString(message);
    }

    public Discipline(Parcel source) {
        this.ID = source.readString();
        this.StudentId = source.readString();
        this.SelfControl = source.readString();
        this.ObeyRules = source.readString();
        this.ObeyStaff = source.readString();
        this.DressCode = source.readString();
        this.TimeKeeping = source.readString();
        this.Conduct = source.readString();
        this.success = source.readString();
        this.message = source.readString();
    }

    public static final Creator<Discipline> CREATOR = new Creator<Discipline>()
    {
        public Discipline createFromParcel(Parcel in)
        {
            return new Discipline(in);
        }
        public Discipline[] newArray(int size)
        {
            return new Discipline[size];
        }
    };

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getSelfControl() {
        return SelfControl;
    }

    public void setSelfControl(String selfControl) {
        SelfControl = selfControl;
    }

    public String getObeyRules() {
        return ObeyRules;
    }

    public void setObeyRules(String obeyRules) {
        ObeyRules = obeyRules;
    }

    public String getObeyStaff() {
        return ObeyStaff;
    }

    public void setObeyStaff(String obeyStaff) {
        ObeyStaff = obeyStaff;
    }

    public String getDressCode() {
        return DressCode;
    }

    public void setDressCode(String dressCode) {
        DressCode = dressCode;
    }

    public String getTimeKeeping() {
        return TimeKeeping;
    }

    public void setTimeKeeping(String timeKeeping) {
        TimeKeeping = timeKeeping;
    }

    public String getConduct() {
        return Conduct;
    }

    public void setConduct(String conduct) {
        Conduct = conduct;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
