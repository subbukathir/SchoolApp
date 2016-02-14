package com.daemon.oxfordschool.listeners;

/**
 * Created by daemonsoft on 8/2/16.
 */
public interface PaymentDetailsListener
{
    public void onPaymentDetailsReceived();
    public void onPaymentDetailsReceivedError(String Str_Msg);
}
