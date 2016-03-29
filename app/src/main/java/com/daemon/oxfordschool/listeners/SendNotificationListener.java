package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 18/3/16.
 */
public interface SendNotificationListener
{
    public void onSendNotificationReceived(String Str_Msg);
    public void onSendNotificationReceivedError(String Str_Msg);
}
