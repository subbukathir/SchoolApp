package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class CExam implements Parcelable
{
    private String ExamId;
    private String ExamType;
    private String ExamDate;
    private String ClassId;
    private String SubjectId;
    private String TheoryMarks;
    private String PracticalMarks;
    private String OtherMarks;
    private String ClassName;
    private String ExamName;
    private String SubjectName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(ExamId);
        dest.writeString(ExamType);
        dest.writeString(ExamDate);
        dest.writeString(ClassId);
        dest.writeString(SubjectId);
        dest.writeString(TheoryMarks);
        dest.writeString(PracticalMarks);
        dest.writeString(OtherMarks);
        dest.writeString(ClassName);
        dest.writeString(ExamName);
        dest.writeString(SubjectName);

    }

    public CExam(Parcel source) {
        this.ExamId = source.readString();
        this.ExamType = source.readString();
        this.ExamDate = source.readString();
        this.ClassId = source.readString();
        this.TheoryMarks = source.readString();
        this.PracticalMarks = source.readString();
        this.OtherMarks = source.readString();
        this.ClassName = source.readString();
        this.ExamName = source.readString();
        this.SubjectName = source.readString();
    }

    public static final Parcelable.Creator<CExam> CREATOR = new Parcelable.Creator<CExam>()
    {
        public CExam createFromParcel(Parcel in)
        {
            return new CExam(in);
        }
        public CExam[] newArray(int size)
        {
            return new CExam[size];
        }
    };


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

    public String getExamDate() {
        return ExamDate;
    }

    public void setExamDate(String examDate) {
        ExamDate = examDate;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getSubjectId() {
        return SubjectId;
    }

    public void setSubjectId(String subjectId) {
        SubjectId = subjectId;
    }

    public String getTheoryMarks() {
        return TheoryMarks;
    }

    public void setTheoryMarks(String theoryMarks) {
        TheoryMarks = theoryMarks;
    }

    public String getPracticalMarks() {
        return PracticalMarks;
    }

    public void setPracticalMarks(String practicalMarks) {
        PracticalMarks = practicalMarks;
    }

    public String getOtherMarks() {
        return OtherMarks;
    }

    public void setOtherMarks(String otherMarks) {
        OtherMarks = otherMarks;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getExamName() {
        return ExamName;
    }

    public void setExamName(String examName) {
        ExamName = examName;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        SubjectName = subjectName;
    }


}
