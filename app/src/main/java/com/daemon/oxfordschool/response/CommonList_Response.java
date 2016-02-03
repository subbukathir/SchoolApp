package com.daemon.oxfordschool.response;

import com.daemon.oxfordschool.classes.CEvents;
import com.daemon.oxfordschool.classes.Common_Class;

import java.util.ArrayList;

/**
 * Created by daemonsoft on 28/1/16.
 */
public class CommonList_Response
{
    private String success;
    private String message;
    private ArrayList<Common_Class> cclass;

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

    public ArrayList<Common_Class> getCclass() {
        return cclass;
    }

    public void setCclass(ArrayList<Common_Class> cclass) {
        this.cclass = cclass;
    }

}
