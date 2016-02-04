package com.daemon.oxfordschool.response;

import com.daemon.oxfordschool.classes.CResult;

import java.util.ArrayList;


/**
 * Created by daemonsoft on 4/2/16.
 */
public class Attendance_Response
{
    private String success;
    private String message;
    private String WorkingDays;
    private String PresentDays;
    private String Percentage;
    private ArrayList<CResult> cresult;

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

    public String getWorkingDays() {
        return WorkingDays;
    }

    public void setWorkingDays(String workingDays) {
        WorkingDays = workingDays;
    }

    public String getPresentDays() {
        return PresentDays;
    }

    public void setPresentDays(String presentDays) {
        PresentDays = presentDays;
    }

    public String getPercentage() {
        return Percentage;
    }

    public void setPercentage(String percentage) {
        Percentage = percentage;
    }

    public ArrayList<CResult> getCresult() {
        return cresult;
    }

    public void setCresult(ArrayList<CResult> cresult) {
        this.cresult = cresult;
    }
}
