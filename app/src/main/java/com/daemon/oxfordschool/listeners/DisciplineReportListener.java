package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 8/2/16.
 */
public interface DisciplineReportListener
{
    public void onDisciplineReportReceived();
    public void onDisciplineReportReceivedError(String Str_Msg);
}
