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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(ExamResultId);
        dest.writeString(ExamId);
        dest.writeString(ClassId);
        dest.writeString(ExamId);
        dest.writeString(SectionId);
        dest.writeString(StudentId);
        dest.writeString(SubjectId);
        dest.writeString(ObtainedTheoryMarks);
        dest.writeString(ObtainedPracticalMarks);
        dest.writeString(ObtainedOtherMarks);
        dest.writeString(ClassName);
        dest.writeString(SectionName);
        dest.writeString(StudentFirstName);
        dest.writeString(StudentLastName);
        dest.writeString(SubjectName);
        dest.writeString(ExamDate);
        dest.writeString(TheoryMarks);
        dest.writeString(PracticalMarks);
        dest.writeString(Marks);
        dest.writeString(ObtainedMarks);
        dest.writeString(Result);
    }

    public CResult(Parcel source) {
        this.ExamResultId = source.readString();
        this.ExamId = source.readString();
        this.ClassId = source.readString();
        this.SectionId = source.readString();
        this.StudentId = source.readString();
        this.SubjectId = source.readString();
        this.ObtainedTheoryMarks = source.readString();
        this.ObtainedPracticalMarks = source.readString();
        this.ObtainedOtherMarks = source.readString();
        this.ClassName = source.readString();
        this.SectionName = source.readString();
        this.StudentFirstName = source.readString();
        this.StudentLastName = source.readString();
        this.SubjectName = source.readString();
        this.ExamDate = source.readString();
        this.TheoryMarks = source.readString();
        this.PracticalMarks = source.readString();
        this.Marks = source.readString();
        this.ObtainedMarks = source.readString();
        this.Result = source.readString();

    }

    public static final Parcelable.Creator<CResult> CREATOR = new Parcelable.Creator<CResult>()
    {
        public CResult createFromParcel(Parcel in)
        {
            return new CResult(in);
        }
        public CResult[] newArray(int size)
        {
            return new CResult[size];
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

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getSubjectId() {
        return SubjectId;
    }

    public void setSubjectId(String subjectId) {
        SubjectId = subjectId;
    }

    public String getObtainedTheoryMarks() {
        return ObtainedTheoryMarks;
    }

    public void setObtainedTheoryMarks(String obtainedTheoryMarks) {
        ObtainedTheoryMarks = obtainedTheoryMarks;
    }

    public String getObtainedPracticalMarks() {
        return ObtainedPracticalMarks;
    }

    public void setObtainedPracticalMarks(String obtainedPracticalMarks) {
        ObtainedPracticalMarks = obtainedPracticalMarks;
    }

    public String getObtainedOtherMarks() {
        return ObtainedOtherMarks;
    }

    public void setObtainedOtherMarks(String obtainedOtherMarks) {
        ObtainedOtherMarks = obtainedOtherMarks;
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

    public String getStudentFirstName() {
        return StudentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        StudentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return StudentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        StudentLastName = studentLastName;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        SubjectName = subjectName;
    }

    public String getExamDate() {
        return ExamDate;
    }

    public void setExamDate(String examDate) {
        ExamDate = examDate;
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

    public String getMarks() {
        return Marks;
    }

    public void setMarks(String marks) {
        Marks = marks;
    }

    public String getObtainedMarks() {
        return ObtainedMarks;
    }

    public void setObtainedMarks(String obtainedMarks) {
        ObtainedMarks = obtainedMarks;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

}
