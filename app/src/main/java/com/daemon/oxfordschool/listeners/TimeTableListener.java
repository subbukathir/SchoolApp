package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 3/2/16.
 */
public interface TimeTableListener
{
    public void onTimeTableReceived();
    public void onTimeTableReceivedError(String Str_Msg);
}
