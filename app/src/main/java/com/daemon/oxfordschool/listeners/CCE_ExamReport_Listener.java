package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 4/12/15.
 */
public interface CCE_ExamReport_Listener {

    public void onCCEExamReportReceived();
    public void onCCEExamReportReceivedError(String Str_Msg);
}
