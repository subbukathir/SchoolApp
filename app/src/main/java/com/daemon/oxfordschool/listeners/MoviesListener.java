package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 4/12/15.
 */
public interface MoviesListener {

    public void onMoviesReceived();
    public void onMoviesReceivedError(String Str_Msg);
}
