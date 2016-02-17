package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class Result implements Parcelable
{
    private String ExamResultId;
    private String ExamId;
    private String ExamType;
    private String ExamName;
    private String ObtainedMarks;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(ExamResultId);
        dest.writeString(ExamId);
        dest.writeString(ExamType);
        dest.writeString(ExamName);
        dest.writeString(ObtainedMarks);
    }

    public Result(Parcel source) {
        this.ExamResultId = source.readString();
        this.ExamId = source.readString();
        this.ExamType = source.readString();
        this.ExamName = source.readString();
        this.ObtainedMarks = source.readString();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>()
    {
        public Result createFromParcel(Parcel in)
        {
            return new Result(in);
        }
        public Result[] newArray(int size)
        {
            return new Result[size];
        }
    };

    public String getExamResultId() {
        return ExamResultId;
    }

    public void setExamResultId(String examResultId) {
        ExamResultId = examResultId;
    }

    public String getExamId() {
        return ExamId;
    }

    public void setExamId(String examId) {
        ExamId = examId;
    }

    public String getExamType() {
        return ExamType;
    }

    public void setExamType(String examType) {
        ExamType = examType;
    }

    public String getExamName() {
        return ExamName;
    }

    public void setExamName(String examName) {
        ExamName = examName;
    }

    public String getObtainedMarks() {
        return ObtainedMarks;
    }

    public void setObtainedMarks(String obtainedMarks) {
        ObtainedMarks = obtainedMarks;
    }

}
