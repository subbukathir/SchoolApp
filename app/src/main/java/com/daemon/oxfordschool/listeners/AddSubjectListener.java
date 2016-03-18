package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 18/3/16.
 */
public interface AddSubjectListener
{
    public void onAddSubjectReceived(String Str_Msg);
    public void onAddSubjectReceivedError(String Str_Msg);
}
