package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 3/2/16.
 */
public interface AttendanceListener
{
    public void onAttendanceReceived();
    public void onAttendanceReceivedError(String Str_Msg);
}
