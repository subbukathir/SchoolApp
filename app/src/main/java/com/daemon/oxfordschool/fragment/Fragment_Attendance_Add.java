package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.adapter.AttendanceStaffAdapter;
import com.daemon.oxfordschool.asyncprocess.AttendanceAdd_Process;
import com.daemon.oxfordschool.classes.StudentAttendance;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.AttendanceAddListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Fragment_Attendance_Add extends Fragment implements AttendanceAddListener
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
    ArrayList<StudentAttendance> mListAttendance = new ArrayList<StudentAttendance>();
    AppCompatActivity mActivity;
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_UserId,Str_ClassId,Str_SectionId,Str_Date="";
    Bundle Args;
    int mMode=0;
    String Str_Url = ApiConstants.ADD_ATTENDANCE;

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
            mManager=mActivity.getSupportFragmentManager();
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
            btn_save.setOnClickListener(_OnClickListener);
            if(mMode==AppUtils.MODE_UPDATE)
            {
                btn_save.setEnabled(false);
                btn_save.setClickable(false);
            }
            else
            {
                btn_save.setEnabled(true);
                btn_save.setClickable(true);
            }
            setActionBarFont();
            SetActionBar();
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

    private void setActionBarFont()
    {
        TextView titleTextView = null;
        try
        {
            TextView subTitleView = (TextView) mToolbar.getChildAt(1);
            subTitleView.setTypeface(font.getHelveticaRegular());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    View.OnClickListener _OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sendAttendance();
        }
    };

    @Override
    public void onAttendanceAddReceived(String Str_Msg) {
        TAG = "onAttendanceAddReceived";
        Log.d(MODULE, TAG);
        try
        {
            AppUtils.DialogMessage(mActivity,Str_Msg);
            mManager.popBackStack();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onAttendanceAddReceivedError(String Str_Msg) {

        TAG = "onAttendanceAddReceivedError";
        Log.d(MODULE, TAG);
        try
        {
            AppUtils.DialogMessage(mActivity, Str_Msg);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public void showAttendanceList()
    {
        TAG = "showAttendanceList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListAttendance.size()>0)
            {
                AttendanceStaffAdapter adapter = new AttendanceStaffAdapter(mListAttendance,this,mMode);
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

    public JSONObject Payload_AttendanceAdd()
    {
        TAG = "Payload_AttendanceAdd";
        Log.d(MODULE,TAG);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("UserId",Str_UserId);
            obj.put("ClassId",Str_ClassId);
            obj.put("SectionId", Str_SectionId);
            obj.put("AttendanceDetails", getStatusDetails());
            obj.put("AttendanceDate", Str_Date);
            obj.put("Mode", "0");
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        Log.d(MODULE, TAG + " obj : " + obj.toString());
        return obj;
    }

    public JSONArray getStatusDetails()
    {
        TAG = "getStatusDetails";
        Log.d(MODULE,TAG);

        JSONArray jsonArray = new JSONArray();

        try
        {
            for(int i=0;i<mListAttendance.size();i++)
            {
                Log.d(MODULE,TAG + " ParentId :  " + mListAttendance.get(i).getParentId());
                JSONObject obj = new JSONObject();
                obj.put("StudentId",mListAttendance.get(i).getStudentId());
                obj.put("ParentId",mListAttendance.get(i).getParentId());
                if(mListAttendance.get(i).isSelected()) obj.put("IsPresent","0");
                else obj.put("IsPresent","1");
                jsonArray.put(obj);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return jsonArray;
    }

    public void sendAttendance()
    {
        TAG = "sendAttendance";
        Log.d(MODULE,TAG);
        try
        {
            new AttendanceAdd_Process(mActivity,Payload_AttendanceAdd(),this).AddAttendance();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_list_view).setVisible(false);
        menu.findItem(R.id.action_chart_view).setVisible(false);
        menu.findItem(R.id.action_help).setVisible(false);
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
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
