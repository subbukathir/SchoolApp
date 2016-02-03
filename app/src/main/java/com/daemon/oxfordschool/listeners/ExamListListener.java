package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 4/12/15.
 */
public interface ExamListListener {

    public void onExamListReceived();
    public void onExamListReceivedError(String Str_Msg);
}
