package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class User implements Parcelable
{
    private String ID;
    private String UserId;
    private String UserType;
    private String FirstName;
    private String LastName;
    private String Email;
    private String Address1;
    private String Address2;
    private String Address3;
    private String District;
    private String State;
    private String Mobile_Number;
    private String User_Password;
    private String StudentId;
    private String ParentId;
    private String ClassId;
    private String SectionId;
    private String ClassName;
    private String SectionName;


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getAddress2() {
        return Address2;
    }

    public void setAddress2(String address2) {
        Address2 = address2;
    }

    public String getAddress3() {
        return Address3;
    }

    public void setAddress3(String address3) {
        Address3 = address3;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getMobile_Number() {
        return Mobile_Number;
    }

    public void setMobile_Number(String mobile_Number) {
        Mobile_Number = mobile_Number;
    }

    public String getUser_Password() {
        return User_Password;
    }

    public void setUser_Password(String user_Password) {
        User_Password = user_Password;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(ID);
        dest.writeString(UserType);
        dest.writeString(FirstName);
        dest.writeString(LastName);
        dest.writeString(Email);
        dest.writeString(Address1);
        dest.writeString(Address2);
        dest.writeString(Address3);
        dest.writeString(District);
        dest.writeString(State);
        dest.writeString(Mobile_Number);
        dest.writeString(User_Password);
        dest.writeString(StudentId);
        dest.writeString(ParentId);
        dest.writeString(ClassId);
        dest.writeString(SectionId);
        dest.writeString(ClassName);
        dest.writeString(SectionName);

    }

    public User(Parcel source) {
        this.ID = source.readString();
        this.UserType = source.readString();
        this.FirstName = source.readString();
        this.LastName = source.readString();
        this.Email = source.readString();
        this.Address1 = source.readString();
        this.Address1 = source.readString();
        this.Address1 = source.readString();
        this.District = source.readString();
        this.State = source.readString();
        this.Mobile_Number = source.readString();
        this.User_Password = source.readString();
        this.StudentId = source.readString();
        this.ParentId = source.readString();
        this.ClassId = source.readString();
        this.SectionId = source.readString();
        this.ClassName = source.readString();
        this.SectionName = source.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>()
    {
        public User createFromParcel(Parcel in)
        {
            return new User(in);
        }
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };

}
