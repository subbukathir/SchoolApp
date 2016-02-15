package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class CFees implements Parcelable
{
    private String StudentId;
    private String Status;
    private String Amount;
    private String ClassId;
    private String TermFeesId;
    private String TutionFees;
    private String DevelopmentFund;
    private String ExamFees;
    private String TotalFees;
    private String LibraryFees;
    private String Name;
    private String FirstName;
    private String LastName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(StudentId);
        dest.writeString(Status);
        dest.writeString(Amount);
        dest.writeString(ClassId);
        dest.writeString(TermFeesId);
        dest.writeString(TutionFees);
        dest.writeString(DevelopmentFund);
        dest.writeString(ExamFees);
        dest.writeString(TotalFees);
        dest.writeString(LibraryFees);
        dest.writeString(Name);
        dest.writeString(FirstName);
        dest.writeString(LastName);

    }

    public CFees(Parcel source) {
        this.StudentId = source.readString();
        this.Status = source.readString();
        this.Amount = source.readString();
        this.ClassId = source.readString();
        this.TermFeesId = source.readString();
        this.TutionFees = source.readString();
        this.DevelopmentFund = source.readString();
        this.ExamFees = source.readString();
        this.TotalFees = source.readString();
        this.LibraryFees = source.readString();
        this.Name = source.readString();
        this.FirstName = source.readString();
        this.LastName = source.readString();
    }

    public static final Creator<CFees> CREATOR = new Creator<CFees>()
    {
        public CFees createFromParcel(Parcel in)
        {
            return new CFees(in);
        }
        public CFees[] newArray(int size)
        {
            return new CFees[size];
        }
    };

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getTermFeesId() {
        return TermFeesId;
    }

    public void setTermFeesId(String termFeesId) {
        TermFeesId = termFeesId;
    }

    public String getTutionFees() {
        return TutionFees;
    }

    public void setTutionFees(String tutionFees) {
        TutionFees = tutionFees;
    }

    public String getDevelopmentFund() {
        return DevelopmentFund;
    }

    public void setDevelopmentFund(String developmentFund) {
        DevelopmentFund = developmentFund;
    }

    public String getExamFees() {
        return ExamFees;
    }

    public void setExamFees(String examFees) {
        ExamFees = examFees;
    }

    public String getTotalFees() {
        return TotalFees;
    }

    public void setTotalFees(String totalFees) {
        TotalFees = totalFees;
    }

    public String getLibraryFees() {
        return LibraryFees;
    }

    public void setLibraryFees(String libraryFees) {
        LibraryFees = libraryFees;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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
}
