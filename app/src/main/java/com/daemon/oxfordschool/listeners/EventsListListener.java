package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 4/12/15.
 */
public interface EventsListListener {

    public void onEventsReceived();
    public void onEventsReceivedError(String Str_Msg);
}
