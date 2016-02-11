package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.adapter.AttendanceStaffAdapter;
import com.daemon.oxfordschool.adapter.EventsAdapter;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.GetAttendance;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.classes.CResult;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.StudentAttendance;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.AttendanceListener;
import com.daemon.oxfordschool.listeners.Attendance_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.DateSetListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.response.Attendance_Response;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Fragment_Attendance_Add extends Fragment implements Attendance_List_Item_Click_Listener
{

    public static String MODULE = "Fragment_Attendance_Add ";
    public static String TAG = "";

    TextView text_view_empty;
    Button btn_save;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    FragmentManager mManager;
    SharedPreferences mPreferences;
    Toolbar mToolbar;
    User mUser;
    int mSelectedPosition=0;
    ArrayList<StudentAttendance> mListAttendance = new ArrayList<StudentAttendance>();
    AppCompatActivity mActivity;
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_UserId,Str_ClassId,Str_SectionId,Str_Date="";
    Bundle Args;
    String Str_Mode="";
    int mMode=0;

    public Fragment_Attendance_Add()
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
            Args = getArguments();
            if(Args!=null)
            {
                mMode =  Args.getInt(AppUtils.B_MODE);
                Str_UserId = Args.getString(AppUtils.B_USER_ID);
                Str_ClassId = Args.getString(AppUtils.B_CLASS_ID);
                Str_SectionId = Args.getString(AppUtils.B_SECTION_ID);
                Str_Date = Args.getString(AppUtils.B_DATE);
                mListAttendance = Args.getParcelableArrayList(AppUtils.B_ATTENDANCE_LIST);
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
        View rootView = inflater.inflate(R.layout.fragment_add_attendance, container, false);
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
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            btn_save =  (Button) view.findViewById(R.id.btn_attendance_save);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_attendance);
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
            showAttendanceList();
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
            text_view_empty.setTypeface(font.getHelveticaRegular());
            btn_save.setTypeface(font.getHelveticaRegular());
            if(mMode==AppUtils.MODE_UPDATE)recycler_view.setEnabled(false);
            SetActionBar();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void SetActionBar()
    {
        try
        {
            if (mActivity != null)
            {
                mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                mActivity.setSupportActionBar(mToolbar);
                if(mMode==AppUtils.MODE_ADD) mToolbar.setTitle(R.string.lbl_take_attendance);
                else mToolbar.setTitle(R.string.lbl_view_attendance);
                mToolbar.setSubtitle(ConvertedDate());
                final ActionBar ab = mActivity.getSupportActionBar();
                ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                ab.setDisplayHomeAsUpEnabled(true);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onAttendanceListItemClicked(int position) {

    }

    public void showAttendanceList()
    {
        TAG = "showAttendanceList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListAttendance.size()>0)
            {
                AttendanceStaffAdapter adapter = new AttendanceStaffAdapter(mListAttendance,this);
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
            textView.setText(R.string.lbl_no_student);
            textView.setTypeface(font.getHelveticaRegular());
            ((ViewGroup)recycler_view.getParent().getParent()).addView(emptyView);
            recycler_view.setEmptyView(emptyView);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

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
            date = sdf1.parse(Str_Date);
            Str_ReturnValue = format1.format(date);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return Str_ReturnValue;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        TAG = "onOptionsItemSelected";
        Log.d(MODULE, TAG);

        switch (item.getItemId())
        {
            case android.R.id.home:
                FragmentDrawer.mDrawerLayout.closeDrawer(GravityCompat.START);
                mManager = mActivity.getSupportFragmentManager();
                mManager.popBackStack();
                return true;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
