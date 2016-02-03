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

    public SelectDateFragment(Fragment _fragment)
    {
        this._fragment=_fragment;
        mCallBack = (DateSetListener) _fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }
    public void onDateSet(DatePicker view, int yy, int mm, int dd)
    {
        mCallBack.populateSetDate(yy, mm+1, dd);
    }
}
