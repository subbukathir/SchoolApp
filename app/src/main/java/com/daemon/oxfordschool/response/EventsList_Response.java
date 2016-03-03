package com.daemon.oxfordschool.response;

import com.daemon.oxfordschool.classes.CEvents;
import java.util.ArrayList;

/**
 * Created by daemonsoft on 28/1/16.
 */
public class EventsList_Response
{
    private String success;
    private String message;
    private ArrayList<CEvents> cevents;

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

    public ArrayList<CEvents> getCevents() {
        return cevents;
    }

    public void setCevents(ArrayList<CEvents> cevents) {
        this.cevents = cevents;
    }

}
