package com.daemon.oxfordschool.response;

import com.daemon.oxfordschool.classes.CEvents;
import com.daemon.oxfordschool.classes.CExam;

import java.util.ArrayList;

/**
 * Created by daemonsoft on 28/1/16.
 */
public class ExamList_Response
{
    private String success;
    private String message;
    private ArrayList<CExam> cexam;

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

    public ArrayList<CExam> getCexam() {
        return cexam;
    }

    public void setCexam(ArrayList<CExam> cexam) {
        this.cexam = cexam;
    }

}
