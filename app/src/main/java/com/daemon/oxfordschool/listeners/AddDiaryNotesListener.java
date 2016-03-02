package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 8/2/16.
 */
public interface AddDiaryNotesListener
{
    public void onDiaryNotesReceived(String Str_Msg);
    public void onDiaryNotesReceivedError(String Str_Msg);
}
