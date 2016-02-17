package com.daemon.oxfordschool.response;

import com.daemon.oxfordschool.classes.CCEResult;
import com.daemon.oxfordschool.classes.CResult;

import java.util.ArrayList;


/**
 * Created by daemonsoft on 4/2/16.
 */
public class CCE_ExamReport_Response
{
    private String success;
    private String message;
    private ArrayList<CCEResult> cceresult;

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

    public ArrayList<CCEResult> getCceresult() {
        return cceresult;
    }

    public void setCceresult(ArrayList<CCEResult> cceresult) {
        this.cceresult = cceresult;
    }

}
