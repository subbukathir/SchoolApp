package com.daemon.oxfordschool.response;

import com.daemon.oxfordschool.classes.CFees;
import com.daemon.oxfordschool.classes.CResult;

import java.util.ArrayList;


/**
 * Created by daemonsoft on 4/2/16.
 */
public class FeesList_Response
{
    private String success;
    private String message;
    private ArrayList<CFees> cfees;

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

    public ArrayList<CFees> getCfees() {
        return cfees;
    }

    public void setCfees(ArrayList<CFees> cfees) {
        this.cfees = cfees;
    }
}
