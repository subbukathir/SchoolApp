package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 28/1/16.
 */
public interface ClassListListener
{
    public void onClassListReceived();
    public void onClassListReceivedError(String Str_Msg);
}
