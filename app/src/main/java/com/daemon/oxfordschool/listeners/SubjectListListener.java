package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 28/1/16.
 */
public interface SubjectListListener
{
    public void onSubjectListReceived();
    public void onSubjectListReceivedError(String Str_Msg);
}
