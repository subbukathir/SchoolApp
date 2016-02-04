package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 4/12/15.
 */
public interface ExamResultListener {

    public void onExamResultReceived();
    public void onExamResultReceivedError(String Str_Msg);
}
