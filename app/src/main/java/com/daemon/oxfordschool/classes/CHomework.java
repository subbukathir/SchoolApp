package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class CHomework implements Parcelable
{
    private String HomeWorkId;
    private String ClassId;
    private String SectionId;
    private String SubjectId;
    private String Assignment_I;
    private String Assignment_II;
    private String HomeWorkDate;
    private String ClassName;
    private String SectionName;
    private String SubjectName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(HomeWorkId);
        dest.writeString(ClassId);
        dest.writeString(SectionId);
        dest.writeString(SubjectId);
        dest.writeString(Assignment_I);
        dest.writeString(Assignment_II);
        dest.writeString(HomeWorkDate);
        dest.writeString(ClassName);
        dest.writeString(SectionName);
        dest.writeString(SubjectName);
    }

    public CHomework(Parcel source) {
        this.HomeWorkId = source.readString();
        this.ClassId = source.readString();
        this.SectionId = source.readString();
        this.SubjectId = source.readString();
        this.Assignment_I = source.readString();
        this.Assignment_II = source.readString();
        this.HomeWorkDate = source.readString();
        this.ClassName = source.readString();
        this.SectionName = source.readString();
        this.SubjectName = source.readString();
    }

    public static final Parcelable.Creator<CHomework> CREATOR = new Parcelable.Creator<CHomework>()
    {
        public CHomework createFromParcel(Parcel in)
        {
            return new CHomework(in);
        }
        public CHomework[] newArray(int size)
        {
            return new CHomework[size];
        }
    };

    public String getHomeWorkId() {
        return HomeWorkId;
    }

    public void setHomeWorkId(String homeWorkId) {
        HomeWorkId = homeWorkId;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getSectionId() {
        return SectionId;
    }

    public void setSectionId(String sectionId) {
        SectionId = sectionId;
    }

    public String getSubjectId() {
        return SubjectId;
    }

    public void setSubjectId(String subjectId) {
        SubjectId = subjectId;
    }

    public String getAssignment_I() {
        return Assignment_I;
    }

    public void setAssignment_I(String assignment_I) {
        Assignment_I = assignment_I;
    }

    public String getAssignment_II() {
        return Assignment_II;
    }

    public void setAssignment_II(String assignment_II) {
        Assignment_II = assignment_II;
    }

    public String getHomeWorkDate() {
        return HomeWorkDate;
    }

    public void setHomeWorkDate(String homeWorkDate) {
        HomeWorkDate = homeWorkDate;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getSectionName() {
        return SectionName;
    }

    public void setSectionName(String sectionName) {
        SectionName = sectionName;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        SubjectName = subjectName;
    }

}
