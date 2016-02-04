package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class CResult implements Parcelable
{
    private String ExamResultId;
    private String ExamId;
    private String ClassId;
    private String SectionId;
    private String StudentId;
    private String SubjectId;
    private String ObtainedTheoryMarks;
    private String ObtainedPracticalMarks;
    private String ObtainedOtherMarks;
    private String ClassName;
    private String SectionName;
    private String StudentFirstName;
    private String StudentLastName;
    private String SubjectName;
    private String ExamDate;
    private String TheoryMarks;
    private String PracticalMarks;
    private String Marks;
    private String ObtainedMarks;
    private String Result;

    /**----Attendance result*/

    private String AttendanceId;
    private String AttendanceDate;
    private String IsPresent;
    private String IsHalfDay;
    private String IsAfterNoon;

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(AttendanceId);
        dest.writeString(AttendanceDate);
        dest.writeString(IsPresent);
        dest.writeString(IsHalfDay);
        dest.writeString(IsAfterNoon);

    }
    public CResult(Parcel source)
    {
        this.AttendanceId=source.readString();
        this.AttendanceDate=source.readString();
        this.IsPresent=source.readString();
        this.IsHalfDay=source.readString();
        this.IsAfterNoon=source.readString();

    }
    @Override
    public int describeContents() {
        return 0;
    }

public static final Parcelable.Creator<CResult> CREATOR =new Parcelable.Creator<CResult>() {
    @Override
    public CResult createFromParcel(Parcel in) {
        return new CResult(in);
    }

    @Override
    public CResult[] newArray(int size) {
        return new CResult[size];
    }
};

    public String getExamResultId() {
        return ExamResultId;
    }

    public void setExamResultId(String examResultId) {
        ExamResultId = examResultId;
    }

    public String getAttendanceId() {
        return AttendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        AttendanceId = attendanceId;
    }

    public String getAttendanceDate() {
        return AttendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        AttendanceDate = attendanceDate;
    }

    public String getIsPresent() {
        return IsPresent;
    }

    public void setIsPresent(String isPresent) {
        IsPresent = isPresent;
    }

    public String getIsHalfDay() {
        return IsHalfDay;
    }

    public void setIsHalfDay(String isHalfDay) {
        IsHalfDay = isHalfDay;
    }

    public String getIsAfterNoon() {
        return IsAfterNoon;
    }

    public void setIsAfterNoon(String isAfterNoon) {
        IsAfterNoon = isAfterNoon;
    }
}
