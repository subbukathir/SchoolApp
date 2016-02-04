package com.daemon.oxfordschool.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daemonsoft on 29/1/16.
 */
public class TimeTable implements Parcelable
{
    private String ID;
    private String ClassId;
    private String SectionId;
    private String Day1Hour1;
    private String Day1Hour2;
    private String Day1Hour3;
    private String Day1Hour4;
    private String Day1Hour5;
    private String Day1Hour6;
    private String Day1Hour7;
    private String Day1Hour8;
    private String Day2Hour1;
    private String Day2Hour2;
    private String Day2Hour3;
    private String Day2Hour4;
    private String Day2Hour5;
    private String Day2Hour6;
    private String Day2Hour7;
    private String Day2Hour8;
    private String Day3Hour1;
    private String Day3Hour2;
    private String Day3Hour3;
    private String Day3Hour4;
    private String Day3Hour5;
    private String Day3Hour6;
    private String Day3Hour7;
    private String Day3Hour8;
    private String Day4Hour1;
    private String Day4Hour2;
    private String Day4Hour3;
    private String Day4Hour4;
    private String Day4Hour5;
    private String Day4Hour6;
    private String Day4Hour7;
    private String Day4Hour8;
    private String Day5Hour1;
    private String Day5Hour2;
    private String Day5Hour3;
    private String Day5Hour4;
    private String Day5Hour5;
    private String Day5Hour6;
    private String Day5Hour7;
    private String Day5Hour8;
    private String Success;
    private String Message;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(ID);
        dest.writeString(ClassId);
        dest.writeString(SectionId);
        dest.writeString(Day1Hour1);
        dest.writeString(Day1Hour2);
        dest.writeString(Day1Hour3);
        dest.writeString(Day1Hour4);
        dest.writeString(Day1Hour5);
        dest.writeString(Day1Hour6);
        dest.writeString(Day1Hour7);
        dest.writeString(Day1Hour8);
        dest.writeString(Day2Hour1);
        dest.writeString(Day2Hour2);
        dest.writeString(Day2Hour3);
        dest.writeString(Day2Hour4);
        dest.writeString(Day2Hour5);
        dest.writeString(Day2Hour6);
        dest.writeString(Day2Hour7);
        dest.writeString(Day2Hour8);
        dest.writeString(Day3Hour1);
        dest.writeString(Day3Hour2);
        dest.writeString(Day3Hour3);
        dest.writeString(Day3Hour4);
        dest.writeString(Day3Hour5);
        dest.writeString(Day3Hour6);
        dest.writeString(Day3Hour7);
        dest.writeString(Day3Hour8);
        dest.writeString(Day4Hour1);
        dest.writeString(Day4Hour2);
        dest.writeString(Day4Hour3);
        dest.writeString(Day4Hour4);
        dest.writeString(Day4Hour5);
        dest.writeString(Day4Hour6);
        dest.writeString(Day4Hour7);
        dest.writeString(Day4Hour8);
        dest.writeString(Day5Hour1);
        dest.writeString(Day5Hour2);
        dest.writeString(Day5Hour3);
        dest.writeString(Day5Hour4);
        dest.writeString(Day5Hour5);
        dest.writeString(Day5Hour6);
        dest.writeString(Day5Hour7);
        dest.writeString(Day5Hour8);
        dest.writeString(Success);
        dest.writeString(Message);


    }

    public TimeTable(Parcel source) {
        this.ID = source.readString();
        this.ClassId = source.readString();
        this.SectionId = source.readString();
        this.Day1Hour1 = source.readString();
        this.Day1Hour2 = source.readString();
        this.Day1Hour3 = source.readString();
        this.Day1Hour4 = source.readString();
        this.Day1Hour5 = source.readString();
        this.Day1Hour6 = source.readString();
        this.Day1Hour7 = source.readString();
        this.Day1Hour8 = source.readString();
        this.Day2Hour1 = source.readString();
        this.Day2Hour2 = source.readString();
        this.Day2Hour3 = source.readString();
        this.Day2Hour4 = source.readString();
        this.Day2Hour5 = source.readString();
        this.Day2Hour6 = source.readString();
        this.Day2Hour7 = source.readString();
        this.Day2Hour8 = source.readString();
        this.Day3Hour1 = source.readString();
        this.Day3Hour2 = source.readString();
        this.Day3Hour3 = source.readString();
        this.Day3Hour4 = source.readString();
        this.Day3Hour5 = source.readString();
        this.Day3Hour6 = source.readString();
        this.Day3Hour7 = source.readString();
        this.Day3Hour8 = source.readString();
        this.Day4Hour1 = source.readString();
        this.Day4Hour2 = source.readString();
        this.Day4Hour3 = source.readString();
        this.Day4Hour4 = source.readString();
        this.Day4Hour5 = source.readString();
        this.Day4Hour6 = source.readString();
        this.Day4Hour7 = source.readString();
        this.Day4Hour8 = source.readString();
        this.Day5Hour1 = source.readString();
        this.Day5Hour2 = source.readString();
        this.Day5Hour3 = source.readString();
        this.Day5Hour4 = source.readString();
        this.Day5Hour5 = source.readString();
        this.Day5Hour6 = source.readString();
        this.Day5Hour7 = source.readString();
        this.Day5Hour8 = source.readString();
        this.Success = source.readString();
        this.Message = source.readString();
    }

    public static final Creator<TimeTable> CREATOR = new Creator<TimeTable>()
    {
        public TimeTable createFromParcel(Parcel in)
        {
            return new TimeTable(in);
        }
        public TimeTable[] newArray(int size)
        {
            return new TimeTable[size];
        }
    };

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getDay1Hour1() {
        return Day1Hour1;
    }

    public void setDay1Hour1(String day1Hour1) {
        Day1Hour1 = day1Hour1;
    }

    public String getDay1Hour2() {
        return Day1Hour2;
    }

    public void setDay1Hour2(String day1Hour2) {
        Day1Hour2 = day1Hour2;
    }

    public String getDay1Hour3() {
        return Day1Hour3;
    }

    public void setDay1Hour3(String day1Hour3) {
        Day1Hour3 = day1Hour3;
    }

    public String getDay1Hour4() {
        return Day1Hour4;
    }

    public void setDay1Hour4(String day1Hour4) {
        Day1Hour4 = day1Hour4;
    }

    public String getDay1Hour5() {
        return Day1Hour5;
    }

    public void setDay1Hour5(String day1Hour5) {
        Day1Hour5 = day1Hour5;
    }

    public String getDay1Hour6() {
        return Day1Hour6;
    }

    public void setDay1Hour6(String day1Hour6) {
        Day1Hour6 = day1Hour6;
    }

    public String getDay1Hour7() {
        return Day1Hour7;
    }

    public void setDay1Hour7(String day1Hour7) {
        Day1Hour7 = day1Hour7;
    }

    public String getDay1Hour8() {
        return Day1Hour8;
    }

    public void setDay1Hour8(String day1Hour8) {
        Day1Hour8 = day1Hour8;
    }

    public String getDay2Hour1() {
        return Day2Hour1;
    }

    public void setDay2Hour1(String day2Hour1) {
        Day2Hour1 = day2Hour1;
    }

    public String getDay2Hour2() {
        return Day2Hour2;
    }

    public void setDay2Hour2(String day2Hour2) {
        Day2Hour2 = day2Hour2;
    }

    public String getDay2Hour3() {
        return Day2Hour3;
    }

    public void setDay2Hour3(String day2Hour3) {
        Day2Hour3 = day2Hour3;
    }

    public String getDay2Hour4() {
        return Day2Hour4;
    }

    public void setDay2Hour4(String day2Hour4) {
        Day2Hour4 = day2Hour4;
    }

    public String getDay2Hour5() {
        return Day2Hour5;
    }

    public void setDay2Hour5(String day2Hour5) {
        Day2Hour5 = day2Hour5;
    }

    public String getDay2Hour6() {
        return Day2Hour6;
    }

    public void setDay2Hour6(String day2Hour6) {
        Day2Hour6 = day2Hour6;
    }

    public String getDay2Hour7() {
        return Day2Hour7;
    }

    public void setDay2Hour7(String day2Hour7) {
        Day2Hour7 = day2Hour7;
    }

    public String getDay2Hour8() {
        return Day2Hour8;
    }

    public void setDay2Hour8(String day2Hour8) {
        Day2Hour8 = day2Hour8;
    }

    public String getDay3Hour1() {
        return Day3Hour1;
    }

    public void setDay3Hour1(String day3Hour1) {
        Day3Hour1 = day3Hour1;
    }

    public String getDay3Hour2() {
        return Day3Hour2;
    }

    public void setDay3Hour2(String day3Hour2) {
        Day3Hour2 = day3Hour2;
    }

    public String getDay3Hour3() {
        return Day3Hour3;
    }

    public void setDay3Hour3(String day3Hour3) {
        Day3Hour3 = day3Hour3;
    }

    public String getDay3Hour4() {
        return Day3Hour4;
    }

    public void setDay3Hour4(String day3Hour4) {
        Day3Hour4 = day3Hour4;
    }

    public String getDay3Hour5() {
        return Day3Hour5;
    }

    public void setDay3Hour5(String day3Hour5) {
        Day3Hour5 = day3Hour5;
    }

    public String getDay3Hour6() {
        return Day3Hour6;
    }

    public void setDay3Hour6(String day3Hour6) {
        Day3Hour6 = day3Hour6;
    }

    public String getDay3Hour7() {
        return Day3Hour7;
    }

    public void setDay3Hour7(String day3Hour7) {
        Day3Hour7 = day3Hour7;
    }

    public String getDay3Hour8() {
        return Day3Hour8;
    }

    public void setDay3Hour8(String day3Hour8) {
        Day3Hour8 = day3Hour8;
    }

    public String getDay4Hour1() {
        return Day4Hour1;
    }

    public void setDay4Hour1(String day4Hour1) {
        Day4Hour1 = day4Hour1;
    }

    public String getDay4Hour2() {
        return Day4Hour2;
    }

    public void setDay4Hour2(String day4Hour2) {
        Day4Hour2 = day4Hour2;
    }

    public String getDay4Hour3() {
        return Day4Hour3;
    }

    public void setDay4Hour3(String day4Hour3) {
        Day4Hour3 = day4Hour3;
    }

    public String getDay4Hour4() {
        return Day4Hour4;
    }

    public void setDay4Hour4(String day4Hour4) {
        Day4Hour4 = day4Hour4;
    }

    public String getDay4Hour5() {
        return Day4Hour5;
    }

    public void setDay4Hour5(String day4Hour5) {
        Day4Hour5 = day4Hour5;
    }

    public String getDay4Hour6() {
        return Day4Hour6;
    }

    public void setDay4Hour6(String day4Hour6) {
        Day4Hour6 = day4Hour6;
    }

    public String getDay4Hour7() {
        return Day4Hour7;
    }

    public void setDay4Hour7(String day4Hour7) {
        Day4Hour7 = day4Hour7;
    }

    public String getDay4Hour8() {
        return Day4Hour8;
    }

    public void setDay4Hour8(String day4Hour8) {
        Day4Hour8 = day4Hour8;
    }

    public String getDay5Hour1() {
        return Day5Hour1;
    }

    public void setDay5Hour1(String day5Hour1) {
        Day5Hour1 = day5Hour1;
    }

    public String getDay5Hour2() {
        return Day5Hour2;
    }

    public void setDay5Hour2(String day5Hour2) {
        Day5Hour2 = day5Hour2;
    }

    public String getDay5Hour3() {
        return Day5Hour3;
    }

    public void setDay5Hour3(String day5Hour3) {
        Day5Hour3 = day5Hour3;
    }

    public String getDay5Hour4() {
        return Day5Hour4;
    }

    public void setDay5Hour4(String day5Hour4) {
        Day5Hour4 = day5Hour4;
    }

    public String getDay5Hour5() {
        return Day5Hour5;
    }

    public void setDay5Hour5(String day5Hour5) {
        Day5Hour5 = day5Hour5;
    }

    public String getDay5Hour6() {
        return Day5Hour6;
    }

    public void setDay5Hour6(String day5Hour6) {
        Day5Hour6 = day5Hour6;
    }

    public String getDay5Hour7() {
        return Day5Hour7;
    }

    public void setDay5Hour7(String day5Hour7) {
        Day5Hour7 = day5Hour7;
    }

    public String getDay5Hour8() {
        return Day5Hour8;
    }

    public void setDay5Hour8(String day5Hour8) {
        Day5Hour8 = day5Hour8;
    }

    public String getSuccess() {
        return Success;
    }

    public void setSuccess(String success) {
        Success = success;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

}
