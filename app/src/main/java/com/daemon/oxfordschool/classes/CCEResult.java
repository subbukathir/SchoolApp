package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class CCEResult implements Parcelable
{
    private String SubjectName;
    private ArrayList<Result> result;
    private String Average;
    private String Grade;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(SubjectName);
        dest.writeList(result);
        dest.writeString(Average);
        dest.writeString(Grade);
    }

    public CCEResult(Parcel source)
    {
        result = new ArrayList<Result>();
        this.SubjectName = source.readString();
        this.result = source.readArrayList(Result.class.getClassLoader());
        this.Average = source.readString();
        this.Grade = source.readString();
        this.SubjectName = source.readString();
    }

    public static final Creator<CCEResult> CREATOR = new Creator<CCEResult>()
    {
        public CCEResult createFromParcel(Parcel in)
        {
            return new CCEResult(in);
        }
        public CCEResult[] newArray(int size)
        {
            return new CCEResult[size];
        }
    };

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        SubjectName = subjectName;
    }

    public ArrayList<Result> getResult() {
        return result;
    }

    public void setResult(ArrayList<Result> result) {
        this.result = result;
    }

    public String getAverage() {
        return Average;
    }

    public void setAverage(String average) {
        Average = average;
    }

    public String getGrade() {
        return Grade;
    }

    public void setGrade(String grade) {
        Grade = grade;
    }

}
