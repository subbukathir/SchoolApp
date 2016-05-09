package com.daemon.oxfordschool.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;


import com.daemon.oxfordschool.listeners.DateSetListener;

import java.util.Calendar;

/**
 * Created by dae-subbu on 12/5/15.
 */
public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    DateSetListener mCallBack;
    Fragment _fragment;
    int yy,mm,dd;
    String Str_Date="";
    final Calendar calendar = Calendar.getInstance();

    public SelectDateFragment()
    {
        yy = calendar.get(Calendar.YEAR);
        mm = calendar.get(Calendar.MONTH);
        dd = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void setListener(Fragment _fragment)
    {
        this._fragment=_fragment;
        mCallBack = (DateSetListener) _fragment;
    }

    public void setDate(String Str_Date)
    {
        this.Str_Date=Str_Date;
        String[] date = Str_Date.split("-");
        yy=Integer.parseInt(date[0]);
        mm=Integer.parseInt(date[1]);
        mm=mm-1;
        dd=Integer.parseInt(date[2]);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd)
    {
        mCallBack.populateSetDate(yy, mm+1, dd);
    }

}
