package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 8/2/16.
 */
public interface RegistrationListener
{
    public void onRegistrationReceived(String Str_Msg);
    public void onRegistrationReceivedError(String Str_Msg);
}
