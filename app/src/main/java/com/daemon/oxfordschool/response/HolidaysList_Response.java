package com.daemon.oxfordschool.response;

import com.daemon.oxfordschool.classes.CFees;
import com.daemon.oxfordschool.classes.CHolidays;

import java.util.ArrayList;


/**
 * Created by daemonsoft on 4/2/16.
 */
public class HolidaysList_Response
{
    private String success;
    private String message;
    private ArrayList<CHolidays> cholidays;

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

    public ArrayList<CHolidays> getCholidays() {
        return cholidays;
    }

    public void setCholidays(ArrayList<CHolidays> cholidays) {
        this.cholidays = cholidays;
    }
}
