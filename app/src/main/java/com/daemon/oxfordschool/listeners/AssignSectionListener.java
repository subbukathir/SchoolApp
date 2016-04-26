package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 19/4/16.
 */
public interface AssignSectionListener {

    public void onSectionAssigned(String Str_Msg);
    public void onSectionAssignedError(String Str_Msg);
}
