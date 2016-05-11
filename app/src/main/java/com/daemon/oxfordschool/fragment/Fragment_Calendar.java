package com.daemon.oxfordschool.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.asyncprocess.GetEventsList;
import com.daemon.oxfordschool.asyncprocess.GetHolidayList;
import com.daemon.oxfordschool.classes.CEvents;
import com.daemon.oxfordschool.classes.CHolidays;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.decorators.EventDecorator;
import com.daemon.oxfordschool.decorators.HighlightWeekendsDecorator;
import com.daemon.oxfordschool.decorators.HolidayDecorator;
import com.daemon.oxfordschool.decorators.MySelectorDecorator;
import com.daemon.oxfordschool.decorators.OneDayDecorator;
import com.daemon.oxfordschool.listeners.EventsListListener;
import com.daemon.oxfordschool.listeners.HolidayList_Listener;
import com.daemon.oxfordschool.response.EventsList_Response;
import com.daemon.oxfordschool.response.HolidaysList_Response;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.Font;

public class Fragment_Calendar extends Fragment implements OnDateSelectedListener, OnMonthChangedListener,
        HolidayList_Listener,EventsListListener
{
    public static String MODULE = "Fragment_Calendar";
    public static String TAG = "";

    AppCompatActivity mActivity;
    private Font font= MyApplication.getInstance().getFontInstance();
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    MaterialCalendarView widget;
    LinearLayout ll_view_calendar;
    Toolbar mToolbar;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    int mTitleSize=0;float mDensity=0;
    TextView text_view_cal_header,text_view_cal_description;
    SharedPreferences mPreferences;
    HolidaysList_Response response;
    EventsList_Response eventsListResponse;
    ArrayList<CHolidays> mListHolidays =new ArrayList<CHolidays>();
    ArrayList<CEvents> mListEvents =new ArrayList<CEvents>();
    String Str_EventsUrl = ApiConstants.EVENTS_LIST_URL;

    Animation slideOut;
    Animation slideIn;
    Animation slideDown;
    Animation scaleUp;

    public Fragment_Calendar()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TAG = "onCreate";
        Log.d(MODULE, TAG);
        try
        {
            mActivity = (AppCompatActivity) getActivity();
            setHasOptionsMenu(true);
            mDensity =  mActivity.getResources().getDisplayMetrics().density;
            mTitleSize = (int) (mActivity.getResources().getDimension(R.dimen.lbl_size) / mDensity);

            slideOut = AnimationUtils.loadAnimation( mActivity,R.anim.slide_out_right);
            slideIn = AnimationUtils.loadAnimation( mActivity,R.anim.slide_in_left);
            slideDown = AnimationUtils.loadAnimation( mActivity,R.anim.slide_down);
            scaleUp = AnimationUtils.loadAnimation(mActivity, R.anim.scale_up);

            new GetHolidayList(ApiConstants.HOLIDAYS_URL,this).getHolidays();
            new GetEventsList(Str_EventsUrl,this).getEvents();
            if (mActivity.getCurrentFocus() != null)
            {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_calender, container, false);
        TAG = "onCreateView";
        Log.d(MODULE, TAG);
        initView(rootView);
        return rootView;
    }

    public void initView(View view)
    {
        TAG = "initView";
        Log.d(MODULE, TAG);
        try
        {
            widget = (MaterialCalendarView) view.findViewById(R.id.calendarView);
            ll_view_calendar = (LinearLayout) view.findViewById(R.id.ll_view_calendar);
            text_view_cal_header= (TextView) view.findViewById(R.id.text_view_cal_header);
            text_view_cal_description = (TextView) view.findViewById(R.id.text_view_cal_description);
            setProperties();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        TAG = "onStart";
        Log.d(MODULE, TAG);

        try
        {
            if (mActivity.getCurrentFocus() != null)
            {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void SetActionBar()
    {
        TAG = "SetActionBar";
        Log.d(MODULE, TAG);
        try
        {
            if (mActivity != null)
            {
                mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                mActivity.setSupportActionBar(mToolbar);
                mToolbar.setTitle(R.string.lbl_calendar);
                mToolbar.setSubtitle("");
                if(!MainActivity.mTwoPane)
                {
                    FragmentDrawer.mDrawerLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            FragmentDrawer.mDrawerToggle.syncState();
                        }
                    });
                    mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                else mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }


    public void setProperties()
    {
        TAG = "setProperties";
        Log.d(MODULE, TAG);
        try
        {
            text_view_cal_header.setTypeface(font.getHelveticaRegular());
            text_view_cal_description.setTypeface(font.getHelveticaRegular());
            widget.setFirstDayOfWeek(Calendar.MONDAY);
            widget.setOnDateChangedListener(this);
            widget.setOnMonthChangedListener(this);
            widget.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
            widget.setArrowColor(getResources().getColor(R.color.color_app));
            widget.setSelectionColor(getResources().getColor(R.color.color_app));
            widget.addDecorators(
                    new MySelectorDecorator(mActivity),
                    new HighlightWeekendsDecorator(),
                    oneDayDecorator
            );

            CalendarDay day= CalendarDay.today();
            showDescription(day);

            slideDown.setAnimationListener(animationWidgetListener);
            widget.startAnimation(slideDown);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getHolidaysList()
    {
        TAG = "getHolidaysList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_HOLIDAY_LIST, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if (Str_Json.length() > 0)
            {
                response = (HolidaysList_Response) AppUtils.fromJson(Str_Json, new TypeToken<HolidaysList_Response>() { }.getType());
                mListHolidays = response.getCholidays();
                Log.d(MODULE, TAG + " mListHolidays : " + mListHolidays.size());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setHolidaysList()
    {
        TAG = "setHolidaysList";
        Log.d(MODULE, TAG);
        try
        {
           if(mListHolidays.size()>0)
           {
               ArrayList<CalendarDay> dates = new ArrayList<>();
               for (int i = 0; i < mListHolidays.size(); i++) {
                   String hdate = mListHolidays.get(i).getHolidayDate();
                   String[] array_datetime = hdate.split("-");
                   int mYear = Integer.parseInt(array_datetime[0]);
                   int mMonth = Integer.parseInt(array_datetime[1]);
                   int mDate = Integer.parseInt(array_datetime[2]);
                   CalendarDay day = CalendarDay.from(mYear,(mMonth-1),mDate);
                   dates.add(day);
               }
               widget.addDecorator(new HolidayDecorator(dates));
           }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onHolidayListReceived() {
        TAG = "onHolidayListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getHolidaysList();
            setHolidaysList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onHolidayListReceivedError(String Str_Msg) {
        TAG = "onHolidayListReceivedError";
        Log.d(MODULE, TAG);
        try
        {
            AppUtils.showDialog(mActivity, Str_Msg);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onEventsReceived() {
        TAG = "onEventsReceived";
        Log.d(MODULE, TAG);
        try
        {
            getEventsList();
            showEventsList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onEventsReceivedError(String Str_Msg) {
        TAG = "onEventsReceivedError";
        Log.d(MODULE, TAG);
        try
        {

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getEventsList()
    {
        TAG = "getEventsList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_EVENTS_LIST,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                eventsListResponse = (EventsList_Response) AppUtils.fromJson(Str_Json, new TypeToken<EventsList_Response>(){}.getType());
                mListEvents = eventsListResponse.getCevents();
                Log.d(MODULE, TAG + " mListStudents : " + mListEvents.size());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showEventsList()
    {
        TAG = "showEventsList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListEvents.size()>0)
            {
                ArrayList<CalendarDay> dates = new ArrayList<>();
                for (int i = 0; i < mListEvents.size(); i++)
                {
                    String startDateTime = mListEvents.get(i).getStartDate();
                    CalendarDay startDay = GetCalendarDay(startDateTime);

                    String endDateTime = mListEvents.get(i).getEndDate();
                    CalendarDay endDay = GetCalendarDay(endDateTime);

                    if(startDay.equals(endDay))
                    {
                        dates.add(startDay);
                    }
                    else
                    {
                        Date startDate = startDay.getDate();
                        Date endDate = endDay.getDate();
                        dates.addAll(getDaysBetweenDates(startDate,endDate));
                    }

                }
                widget.addDecorator(new EventDecorator(Color.BLUE,dates));
            }
            else
            {

            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public CalendarDay GetCalendarDay(String Str_DateTime)
    {
        try
        {
            String[] startDate = Str_DateTime.split(" ");
            String[] array_datetime = startDate[0].split("-");
            int mYear = Integer.parseInt(array_datetime[0]);
            int mMonth = Integer.parseInt(array_datetime[1]);
            int mDate = Integer.parseInt(array_datetime[2]);
            CalendarDay day = CalendarDay.from(mYear,(mMonth-1),mDate);
            return day;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_list_view).setVisible(false);
        menu.findItem(R.id.action_chart_view).setVisible(false);
        menu.findItem(R.id.action_help).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if(FragmentDrawer.mDrawerLayout.isDrawerOpen(GravityCompat.START))
                    FragmentDrawer.mDrawerLayout.closeDrawer(GravityCompat.START);
                else
                    FragmentDrawer.mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_help:
                 showHelpDialog();
                 break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        TAG = "onDateSelected";
        Log.d(MODULE, TAG);
        try
        {
            showDescription(date);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void showDescription(CalendarDay date)
    {
        TAG = "showDescription";
        Log.d(MODULE, TAG);
        try
        {
            boolean isHolidayFound=false;
            for(int i=0;i<mListHolidays.size(); i++) {
                String[] dtime = mListHolidays.get(i).getHolidayDate().split(" ");
                String[] cdate = dtime[0].split("-");
                int year = Integer.parseInt(cdate[0]);
                int month=Integer.parseInt(cdate[1]);
                int day = Integer.parseInt(cdate[2]);
                if((date.getYear()==year) && ((date.getMonth()+1)==month) &&
                        (date.getDay()==day)){
                    text_view_cal_header.setText(getString(R.string.lbl_holiday));
                    text_view_cal_description.setText(mListHolidays.get(i).getDescription());
                    isHolidayFound=true;
                    break;
                }
            }
            if(!isHolidayFound)
            {
                boolean isEventFound=false;

                for(int i=0;i<mListEvents.size(); i++) {
                    String[] dtime = mListEvents.get(i).getStartDate().split(" ");
                    String[] cdate = dtime[0].split("-");
                    int year = Integer.parseInt(cdate[0]);
                    int month=Integer.parseInt(cdate[1]);
                    int day = Integer.parseInt(cdate[2]);
                    if((date.getYear()==year) && ((date.getMonth()+1)==month) &&
                            (date.getDay()==day)){
                        text_view_cal_header.setText(getString(R.string.lbl_event));
                        text_view_cal_description.setText(mListEvents.get(i).getDescription());
                        isEventFound=true;
                        break;
                    }
                }

                if(!isEventFound)
                {
                    text_view_cal_header.setText(ConvertedDate());
                    text_view_cal_description.setText(getString(R.string.lbl_have_nice_day));
                }
                else
                {
                    ll_view_calendar.startAnimation(slideOut);
                    slideOut.setAnimationListener(animationSlideOutListener);
                }
            }
            else
            {
                ll_view_calendar.startAnimation(slideOut);
                slideOut.setAnimationListener(animationSlideOutListener);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    Animation.AnimationListener animationWidgetListener = new Animation.AnimationListener()
    {
        @Override
        public void onAnimationStart(Animation animation) { }

        @Override
        public void onAnimationEnd(Animation animation)
        {
            scaleUp.setAnimationListener(animationScaleUpListener);
            ll_view_calendar.startAnimation(scaleUp);
        }
        @Override
        public void onAnimationRepeat(Animation animation) { }
    };

    Animation.AnimationListener animationSlideOutListener = new Animation.AnimationListener()
    {
        @Override
        public void onAnimationStart(Animation animation) { }

        @Override
        public void onAnimationEnd(Animation animation)
        {
           ll_view_calendar.startAnimation(slideIn);
        }

        @Override
        public void onAnimationRepeat(Animation animation) { }
    };

    Animation.AnimationListener animationScaleUpListener = new Animation.AnimationListener()
    {
        @Override
        public void onAnimationStart(Animation animation)
        {
            ll_view_calendar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) { }

        @Override
        public void onAnimationRepeat(Animation animation) { }
    };


    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) { }

    private String getSelectedDatesString()
    {
        CalendarDay date = widget.getSelectedDate();
        if (date == null)
        {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }

    public void showHelpDialog()
    {
        TAG = "showHelpDialog";
        Log.d(MODULE, TAG);
        try
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
            LayoutInflater inflater = mActivity.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.calendar_help, null);
            dialogBuilder.setView(dialogView);

            final TextView text_view_holiday = (TextView) dialogView.findViewById(R.id.text_view_holiday);
            final TextView text_view_event = (TextView) dialogView.findViewById(R.id.text_view_event);

            text_view_holiday.setTypeface(font.getHelveticaRegular());
            text_view_event.setTypeface(font.getHelveticaRegular());

            text_view_holiday.setTextColor(Color.parseColor("#c1c1c1"));
            text_view_event.setTextColor(Color.BLUE);

            AlertDialog b = dialogBuilder.create();
            b.setCanceledOnTouchOutside(true);
            b.show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public ArrayList<CalendarDay> getDaysBetweenDates(Date startdate, Date enddate)
    {
        ArrayList<CalendarDay> dates = new ArrayList<CalendarDay>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate))
        {
            Date result = calendar.getTime();
            int year = result.getYear(); int month = result.getMonth(); int day = result.getDay();
            CalendarDay calendarDay = new CalendarDay(year,month,day);
            dates.add(calendarDay);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public String GetTodayDate() {
        String Str_TodayDate = "";
        try
        {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Str_TodayDate = date;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return Str_TodayDate;
    }

    public String ConvertedDate()
    {
        TAG = "ConvertedDate";
        Log.d(MODULE,TAG);
        String Str_ReturnValue="";
        try
        {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat format1 = new SimpleDateFormat("E, MMM dd yyyy");
            Date date;
            date = sdf1.parse(GetTodayDate());
            Str_ReturnValue = format1.format(date);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return Str_ReturnValue;
    }

}
