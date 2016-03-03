package com.daemon.oxfordschool.response;

import com.daemon.oxfordschool.classes.CHomework;
import java.util.ArrayList;

/**
 * Created by daemonsoft on 28/1/16.
 */
public class HomeWorkList_Response
{
    private String success;
    private String message;
    private ArrayList<CHomework> chomework;

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

    public ArrayList<CHomework> getChomework() {
        return chomework;
    }

    public void setChomework(ArrayList<CHomework> chomework) {
        this.chomework = chomework;
    }

}
