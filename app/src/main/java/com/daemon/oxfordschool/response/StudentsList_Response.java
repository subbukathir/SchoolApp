package com.daemon.oxfordschool.response;


import com.daemon.oxfordschool.classes.User;

import java.util.ArrayList;

/**
 * Created by daemonsoft on 28/1/16.
 */
public class StudentsList_Response
{
    private String success;
    private String message;
    private ArrayList<User> cstudents;

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

    public ArrayList<User> getCstudents() {
        return cstudents;
    }

    public void setCstudents(ArrayList<User> cstudents) {
        this.cstudents = cstudents;
    }





}
