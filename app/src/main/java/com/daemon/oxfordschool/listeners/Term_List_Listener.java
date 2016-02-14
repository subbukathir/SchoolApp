package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 8/2/16.
 */
public interface Term_List_Listener
{
    public void onTermListReceived();
    public void onTermListReceivedError(String Str_Msg);
}
