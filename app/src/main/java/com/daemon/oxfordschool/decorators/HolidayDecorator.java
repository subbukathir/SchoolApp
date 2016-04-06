package com.daemon.oxfordschool.decorators;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by daemonsoft on 5/4/16.
 */
public class HolidayDecorator implements DayViewDecorator  {

    private static final int color = Color.parseColor("#c1c1c1");
    private HashSet<CalendarDay> dates;
    public HolidayDecorator(Collection<CalendarDay> dates) {
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(new ColorDrawable(color));
    }

}
