package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 28/1/16.
 */
public interface LoginListener
{
    public void onLoginSuccess();
    public void onLoginFailed(String msg);
}
