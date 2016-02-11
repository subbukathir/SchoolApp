package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 3/2/16.
 */
public interface AttendanceAddListener
{
    public void onAttendanceAddReceived(String Str_Msg);
    public void onAttendanceAddReceivedError(String Str_Msg);
}
