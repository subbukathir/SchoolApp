package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.adapter.StudentPagerAdapter;
import com.daemon.oxfordschool.asyncprocess.DisciplineReport_Process;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.asyncprocess.GetStudentProfile;
import com.daemon.oxfordschool.classes.Discipline;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.DisciplineReportListener;
import com.daemon.oxfordschool.listeners.StudentsListListener;
import com.daemon.oxfordschool.listeners.ViewStudentProfileListener;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_Discipline_Report_Student extends Fragment implements DisciplineReportListener,ViewStudentProfileListener
{

    public static String MODULE = "Fragment_Discipline_Report_Student ";
    public static String TAG = "";

    CoordinatorLayout cl_main;
    TextView tv_lbl_self_control,tv_self_control,tv_lbl_obey_rules,tv_obey_rules,tv_lbl_obey_staff,tv_obey_staff,tv_lbl_dress_code,
     tv_dress_code,tv_lbl_time_keeping,tv_time_keeping,tv_lbl_conduct,tv_conduct,text_view_empty,tv_name,tv_class,tv_section;
    ImageView imageView;
    LinearLayout ll_discipline_report;
    RelativeLayout layout_empty;
    SharedPreferences mPreferences;
    Toolbar mToolbar;
    User mUser,mStudent;

    Discipline mDiscipline;

    AppCompatActivity mActivity;
    Bundle mSavedInstanceState;
    String Str_Id="",Str_ClassId="",Str_SectionId="",Str_StudentId="",Str_EncodeImage="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_StudentList_Url = ApiConstants.STUDENT_LIST;
    String Str_Student_Profile_Url = ApiConstants.STUDENT_PROFILE_URL;
    Bitmap mDecodedImage;
    int mStudentListPosition=0;
    final static String ARG_STUDENT_LIST_POSITION = "Student_List_Position";

    public Fragment_Discipline_Report_Student()
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
            setRetainInstance(false);
            mSavedInstanceState=savedInstanceState;
            getProfile();
            new GetStudentProfile(Str_Student_Profile_Url,Payload(),this).getStudentProfile();
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
        View rootView = inflater.inflate(R.layout.fragment_discipline_student, container, false);
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
            cl_main = (CoordinatorLayout) mActivity.findViewById(R.id.cl_main);
            imageView = (ImageView) view.findViewById(R.id.iv_profile);
            tv_name  = (TextView) view.findViewById(R.id.tv_header_name);
            tv_class  = (TextView) view.findViewById(R.id.tv_class_name);
            tv_section  = (TextView) view.findViewById(R.id.tv_section_name);
            ll_discipline_report = (LinearLayout) view.findViewById(R.id.ll_discipline_report);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            tv_lbl_self_control = (TextView) view.findViewById(R.id.tv_lbl_self_control);
            tv_self_control = (TextView) view.findViewById(R.id.tv_self_control);
            tv_lbl_obey_rules = (TextView) view.findViewById(R.id.tv_lbl_obey_rules);
            tv_obey_rules = (TextView) view.findViewById(R.id.tv_obey_rules);
            tv_lbl_obey_staff = (TextView) view.findViewById(R.id.tv_lbl_obey_staff);
            tv_obey_staff = (TextView) view.findViewById(R.id.tv_obey_staff);
            tv_lbl_dress_code = (TextView) view.findViewById(R.id.tv_lbl_dress_code);
            tv_dress_code = (TextView) view.findViewById(R.id.tv_dress_code);
            tv_lbl_time_keeping = (TextView) view.findViewById(R.id.tv_lbl_time_keeping);
            tv_time_keeping = (TextView) view.findViewById(R.id.tv_time_keeping);
            tv_lbl_conduct = (TextView) view.findViewById(R.id.tv_lbl_conduct);
            tv_conduct = (TextView) view.findViewById(R.id.tv_conduct);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
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
            if(mSavedInstanceState!=null)
            {
                mStudentListPosition=mSavedInstanceState.getInt(ARG_STUDENT_LIST_POSITION);
                Log.d(MODULE,TAG + "mStudentListPosition" + mStudentListPosition);
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
            tv_name.setTypeface(font.getHelveticaRegular());
            tv_class.setTypeface(font.getHelveticaRegular());
            tv_section.setTypeface(font.getHelveticaRegular());
            tv_lbl_self_control.setTypeface(font.getHelveticaRegular());
            tv_self_control.setTypeface(font.getHelveticaRegular());
            tv_lbl_obey_rules .setTypeface(font.getHelveticaRegular());
            tv_obey_rules.setTypeface(font.getHelveticaRegular());
            tv_lbl_obey_staff.setTypeface(font.getHelveticaRegular());
            tv_obey_staff.setTypeface(font.getHelveticaRegular());
            tv_lbl_dress_code.setTypeface(font.getHelveticaRegular());
            tv_dress_code.setTypeface(font.getHelveticaRegular());
            tv_lbl_time_keeping.setTypeface(font.getHelveticaRegular());
            tv_time_keeping.setTypeface(font.getHelveticaRegular());
            tv_lbl_conduct.setTypeface(font.getHelveticaRegular());
            tv_conduct.setTypeface(font.getHelveticaRegular());
            text_view_empty.setTypeface(font.getHelveticaRegular());
            ll_discipline_report.setVisibility(View.VISIBLE);
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
                mToolbar.setTitle(R.string.lbl_discipline_report);
                mToolbar.setSubtitle("");
                if(!MainActivity.mTwoPane)
                {
                    FragmentDrawer.mDrawerLayout.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
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

    public void getStudentProfile()
    {
        TAG = "getStudentsList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_STUDENT_PROFILE, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                mStudent = (User) AppUtils.fromJson(Str_Json, new TypeToken<User>(){}.getType());
                Log.d(MODULE, TAG + " mStudent : " + mStudent.getClassName());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getDisciplineReportFromService()
    {
        TAG = "getDisciplineReportFromService";
        Log.d(MODULE, TAG);
        try
        {
            Str_ClassId = mStudent.getClassId();
            Str_SectionId = mStudent.getSectionId();
            Str_StudentId = mStudent.getStudentId();
            new DisciplineReport_Process(mActivity,this,Payload_Discipline()).GetDisciplineReport();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStudentProfileReceived() {
        TAG = "onStudentProfileReceived";
        Log.d(MODULE, TAG);
        try
        {
            getStudentProfile();
            setProfile();
            getDisciplineReportFromService();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStudentProfileReceivedError(String Str_Msg) {
        TAG = "onStudentProfileReceivedError";
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
    public void onDisciplineReportReceived()
    {
        TAG = "onDisciplineReportReceived";
        Log.d(MODULE, TAG);
        try
        {
            getDisciplineReport();
            showDisciplineReport();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisciplineReportReceivedError(String Str_Msg)
    {
        TAG = "onDisciplineReportReceivedError";
        Log.d(MODULE, TAG);
        try
        {
            text_view_empty.setText(Str_Msg);
            showEmptyView();
            showSnackBar(Str_Msg);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    public void getDisciplineReport()
    {
        TAG = "getDisciplineReport";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_DISCIPLINE, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                mDiscipline = (Discipline) AppUtils.fromJson(Str_Json, new TypeToken<Discipline>(){}.getType());
                Log.d(MODULE, TAG + " mDiscipline : " + mDiscipline.getConduct());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setProfile()
    {
        TAG = "setProfile";
        Log.d(MODULE, TAG);

        try
        {
            StringBuilder Str_Name = new StringBuilder();
            Str_Name.append(mStudent.getFirstName()).append(" ");
            Str_Name.append(mStudent.getLastName());
            StringBuilder Str_ClassName = new StringBuilder();
            Str_ClassName.append(mActivity.getString(R.string.lbl_class)).append(" ");
            Str_ClassName.append(mStudent.getClassName());
            StringBuilder Str_SectionName = new StringBuilder();
            Str_SectionName.append(mActivity.getString(R.string.lbl_section)).append(" ");
            Str_SectionName.append(mStudent.getSectionName());
            tv_name.setText(Str_Name.toString());
            tv_class.setText(Str_ClassName);
            tv_section.setText(Str_SectionName.toString());

            Str_EncodeImage = mStudent.getImageData();


            if(Str_EncodeImage.equals("")) imageView.setImageResource(R.drawable.ic_profile);
            else
            {
                Log.d(MODULE, TAG + "encoded string ***" + Str_EncodeImage);
                byte[] decodedString = Base64.decode(Str_EncodeImage, Base64.DEFAULT);
                mDecodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(mDecodedImage);

            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showDisciplineReport()
    {
        TAG = "showDisciplineReport";
        Log.d(MODULE, TAG);
        try
        {
            if(mDiscipline!=null)
            {
                tv_self_control.setText(mDiscipline.getSelfControl());
                tv_obey_rules.setText(mDiscipline.getObeyRules());
                tv_obey_staff.setText(mDiscipline.getObeyStaff());
                tv_dress_code.setText(mDiscipline.getDressCode());
                tv_time_keeping.setText(mDiscipline.getTimeKeeping());
                tv_conduct.setText(mDiscipline.getConduct());
                layout_empty.setVisibility(View.GONE);
                ll_discipline_report.setVisibility(View.VISIBLE);
            }
            else
            {
                text_view_empty.setText(getString(R.string.lbl_no_discipline_report_found));
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
            layout_empty.setVisibility(View.VISIBLE);
            ll_discipline_report.setVisibility(View.GONE);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public JSONObject Payload()
    {
        TAG = "Payload";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("UserId", Str_Id);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
    }

    public JSONObject Payload_Discipline()
    {
        TAG = "Payload_DiaryNotes";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {

            obj.put("StudentId",Str_StudentId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
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

    public void showSnackBar(String Str_Msg)
    {
        Snackbar snackbar = Snackbar.make(cl_main, Str_Msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.lbl_retry), new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getDisciplineReportFromService();
            }
        });
        // Changing message text color
        snackbar.setActionTextColor(Color.RED);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        textView.setTypeface(font.getHelveticaRegular());
        snackbar.show();
    }

}
