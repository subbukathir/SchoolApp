package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 8/2/16.
 */
public interface AddExamListener
{
    public void onAddExamReceived(String Str_Msg);
    public void onAddExamReceivedError(String Str_Msg);
}
