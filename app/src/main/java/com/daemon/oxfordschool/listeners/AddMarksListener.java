package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 8/2/16.
 */
public interface AddMarksListener
{
    public void onAddMarksReceived(String Str_Msg);
    public void onAddMarksReceivedError(String Str_Msg);
}
