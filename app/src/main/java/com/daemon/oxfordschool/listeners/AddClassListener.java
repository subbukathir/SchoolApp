package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 19/4/16.
 */
public interface AddClassListener {

    public void onClassUpdated(String Str_Msg);
    public void onClassUpdatedError(String Str_Msg);
}
