package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 25/2/16.
 */
public interface AddEventListener
{
    public void onAddEventReceived(String Str_Msg);
    public void onAddEventReceivedError(String Str_Msg);
}
