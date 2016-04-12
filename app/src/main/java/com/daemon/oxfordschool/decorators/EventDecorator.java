package com.daemon.oxfordschool.decorators;

import android.graphics.drawable.ColorDrawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by daemonsoft on 5/4/16.
 */
public class EventDecorator implements DayViewDecorator  {

    private int color;
    private HashSet<CalendarDay> dates;
    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.color = color;
        this.dates = new HashSet<>(dates);
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }
    @Override
    public void decorate(DayViewFacade view) {
        //view.setBackgroundDrawable(new ColorDrawable(color));
        view.addSpan(new DotSpan(20, color));
    }

}
