package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 28/1/16.
 */
public interface DiaryNotesListListener
{
    public void onDiaryNotesListReceived();
    public void onDiaryNotesListReceivedError(String Str_Msg);
}
