package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 4/12/15.
 */
public interface HolidayList_Listener {

    public void onHolidayListReceived();
    public void onHolidayListReceivedError(String Str_Msg);
}
