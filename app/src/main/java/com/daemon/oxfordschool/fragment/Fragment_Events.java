package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.adapter.EventsAdapter;
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.asyncprocess.GetEventsList;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.classes.CEvents;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.Event_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.EventsListListener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.EventsList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_Events extends Fragment implements EventsListListener,Event_List_Item_Click_Listener
{

    public static String MODULE = "Fragment_Events ";
    public static String TAG = "";

    SwipeRefreshLayout swipeRefreshLayout;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;

    SharedPreferences mPreferences;
    User mUser;
    ArrayList<CEvents> mListEvents =new ArrayList<CEvents>();
    EventsList_Response response;

    AppCompatActivity mActivity;
    String Str_Id="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_Url = ApiConstants.EVENTS_LIST_URL;

    public Fragment_Events()
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
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS,Context.MODE_PRIVATE);
            getProfile();
            new GetEventsList(Str_Url,this).getEvents();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
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
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_events);
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
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
        }
        catch (Exception ex)
        {

        }
    }

    public void getProfile()
    {
        TAG = "getProfile";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_LOGIN_PROFILE,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                mUser = (User) AppUtils.fromJson(Str_Json, new TypeToken<User>(){}.getType());
                Str_Id = mUser.getID();
                Log.d(MODULE, TAG + " Str_Id : " + Str_Id);
            }
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

    @Override
    public void onEventListItemClicked(int position) {

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
                response = (EventsList_Response) AppUtils.fromJson(Str_Json, new TypeToken<EventsList_Response>(){}.getType());
                mListEvents = response.getCevents();
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
                EventsAdapter adapter = new EventsAdapter(mListEvents,this);
                recycler_view.setAdapter(adapter);
            }
            else
            {
                showEmptyView();
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showEmptyView()
    {
        TAG = "showEmptyView";
        Log.d(MODULE, TAG);

        try
        {
            View emptyView  = mActivity.getLayoutInflater().inflate(R.layout.view_list_empty,null);
            LinearLayout.LayoutParams params = AppUtils.getMatchParentParams();
            emptyView.setLayoutParams(params);
            TextView textView = (TextView) emptyView.findViewById(R.id.text_view_empty);
            textView.setText(R.string.lbl_no_events);
            textView.setTypeface(font.getHelveticaRegular());
            ((ViewGroup)recycler_view.getParent().getParent()).addView(emptyView);
            recycler_view.setEmptyView(emptyView);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }



}
