package com.daemon.oxfordschool.response;

import com.daemon.oxfordschool.classes.CResult;
import com.daemon.oxfordschool.classes.Common_Class;

import java.util.ArrayList;

/**
 * Created by daemonsoft on 28/1/16.
 */
public class ExamResult_Response
{
    private String success;
    private String message;
    private String TotalMarks;
    private String TotalObtainedMarks;
    private String Average;
    private String Grade;
    private ArrayList<CResult> cresult;

    public String getTotalMarks() {
        return TotalMarks;
    }

    public void setTotalMarks(String totalMarks) {
        TotalMarks = totalMarks;
    }

    public String getTotalObtainedMarks() {
        return TotalObtainedMarks;
    }

    public void setTotalObtainedMarks(String totalObtainedMarks) {
        TotalObtainedMarks = totalObtainedMarks;
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

    public ArrayList<CResult> getCresult() {
        return cresult;
    }

    public void setCresult(ArrayList<CResult> cresult) {
        this.cresult = cresult;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
