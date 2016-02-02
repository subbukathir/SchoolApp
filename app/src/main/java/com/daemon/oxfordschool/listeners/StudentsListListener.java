package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 4/12/15.
 */
public interface StudentsListListener {

    public void onStudentsReceived();
    public void onStudentsReceivedError(String Str_Msg);
}
