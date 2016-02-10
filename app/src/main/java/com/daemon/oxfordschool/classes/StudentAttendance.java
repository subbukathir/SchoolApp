package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class StudentAttendance implements Parcelable
{
    private String StudentId;
    private String FirstName;
    private String LastName;
    private String AttendanceId;
    private String Status;
    private boolean IsSelected;

    public StudentAttendance() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(StudentId);
        dest.writeString(FirstName);
        dest.writeString(LastName);
        dest.writeString(AttendanceId);
        dest.writeString(Status);
        dest.writeByte((byte) (IsSelected ? 1 : 0));
    }

    public StudentAttendance(Parcel source) {
        this.StudentId = source.readString();
        this.FirstName = source.readString();
        this.LastName = source.readString();
        this.AttendanceId = source.readString();
        this.Status = source.readString();
        this.IsSelected = source.readByte() != 0;
    }

    public static final Creator<StudentAttendance> CREATOR = new Creator<StudentAttendance>()
    {
        public StudentAttendance createFromParcel(Parcel in)
        {
            return new StudentAttendance(in);
        }
        public StudentAttendance[] newArray(int size)
        {
            return new StudentAttendance[size];
        }
    };

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getAttendanceId() {
        return AttendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        AttendanceId = attendanceId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public boolean isSelected() {
        return IsSelected;
    }

    public void setSelected(boolean isSelected) {
        IsSelected = isSelected;
    }

}
