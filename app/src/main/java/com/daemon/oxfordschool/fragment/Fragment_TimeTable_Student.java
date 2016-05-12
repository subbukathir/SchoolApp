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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.adapter.TimeTableAdapter;
import com.daemon.oxfordschool.asyncprocess.AllSubjectList_Process;
import com.daemon.oxfordschool.asyncprocess.GetStudentProfile;
import com.daemon.oxfordschool.asyncprocess.GetTimeTable;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.TimeTable;
import com.daemon.oxfordschool.classes.TimeTableItem;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.SubjectListListener;
import com.daemon.oxfordschool.listeners.TimeTableListener;
import com.daemon.oxfordschool.listeners.ViewStudentProfileListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.StudentsList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_TimeTable_Student extends Fragment implements ViewStudentProfileListener,TimeTableListener,SubjectListListener
{

    public static String MODULE = "Fragment_TimeTable_Student";
    public static String TAG = "";

    public static int HEADER=0;
    public static int NON_HEADER=1;

    CoordinatorLayout cl_main;
    TextView tv_name,tv_class,tv_section,text_view_empty;
    ImageView imageView;;

    SharedPreferences mPreferences;
    User mUser,mStudent;
    GridView grid_view;
    RelativeLayout layout_empty;

    ArrayList<Common_Class> mSubjectList;
    TimeTable mTimeTable;

    StudentsList_Response studentListResponse;
    CommonList_Response response;

    AppCompatActivity mActivity;
    Bitmap mDecodedImage;
    String Str_Id="",Str_ClassId="",Str_SectionId="",Str_EncodeImage="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_Url = ApiConstants.STUDENT_PROFILE_URL;
    String Str_TimeTable_Url = ApiConstants.TIME_TABLE_URL;
    ArrayList<TimeTableItem> mListSubjectId = new ArrayList<TimeTableItem>();
    TimeTableAdapter adapter;
    Toolbar mToolbar;

    public Fragment_TimeTable_Student()
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
            setHasOptionsMenu(true);
            new GetStudentProfile(Str_Url,Payload(),this).getStudentProfile();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_timetable_student, container, false);
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
            tv_section = (TextView) view.findViewById(R.id.tv_section_name);
            grid_view=(GridView) view.findViewById(R.id.grid_view);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            setProperties();
            SetActionBar();
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
            tv_name.setTypeface(font.getHelveticaRegular());
            tv_class.setTypeface(font.getHelveticaRegular());
            tv_section.setTypeface(font.getHelveticaRegular());
            text_view_empty.setTypeface(font.getHelveticaRegular());
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
                mToolbar.setTitle(R.string.lbl_time_table);
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
                Str_ClassId = mUser.getClassId();
                Log.d(MODULE, TAG + " Str_Id : " + Str_Id);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getSubjectsListFromService()
    {
        TAG = "getSubjectsListFromService";
        Log.d(MODULE, TAG);
        try
        {
            new AllSubjectList_Process(this).GetSubjectsList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getTimeTableFromService()
    {
        TAG = "getTimeTableFromService";
        Log.d(MODULE, TAG);
        try
        {
            Str_ClassId = mStudent.getClassId();
            Str_SectionId = mStudent.getSectionId();
            new GetTimeTable(Str_TimeTable_Url,Payload_TimeTable(),this).getTimeTable();
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
            getSubjectsListFromService();

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
    public void onTimeTableReceived() {
        TAG = "onTimeTableReceived";
        Log.d(MODULE, TAG);
        try
        {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getTimeTable();
                    showTimeTable();
                }
            });

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onTimeTableReceivedError(String Str_Msg) {
        TAG = "onTimeTableReceivedError";
        Log.d(MODULE, TAG + Str_Msg);
        try
        {
            showSnackBar(Str_Msg,1);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getTimeTable()
    {
        TAG = "getTimeTable";
        Log.d(MODULE, TAG);
        try
        {
            mListSubjectId.clear();
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_TIMETABLE,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                mTimeTable = (TimeTable) AppUtils.fromJson(Str_Json, new TypeToken<TimeTable>(){}.getType());
                Log.d(MODULE, TAG + " mTimeTable : " + mTimeTable.getDay1Hour1());

                TimeTableItem item = new TimeTableItem();item.setName(" ");item.setType(NON_HEADER);
                TimeTableItem itemDay1 = new TimeTableItem();itemDay1.setName(getString(R.string.lbl_monday));itemDay1.setType(HEADER);
                TimeTableItem itemDay2 = new TimeTableItem();itemDay2.setName(getString(R.string.lbl_tuesday));itemDay2.setType(HEADER);
                TimeTableItem itemDay3 = new TimeTableItem();itemDay3.setName(getString(R.string.lbl_wednesday));itemDay3.setType(HEADER);
                TimeTableItem itemDay4 = new TimeTableItem();itemDay4.setName(getString(R.string.lbl_thursday));itemDay4.setType(HEADER);
                TimeTableItem itemDay5 = new TimeTableItem();itemDay5.setName(getString(R.string.lbl_friday));itemDay5.setType(HEADER);

                TimeTableItem itemDay0Hour0 = new TimeTableItem();itemDay0Hour0.setName(getString(R.string.lbl_1hour));itemDay0Hour0.setType(HEADER);
                TimeTableItem itemDay1Hour1 = new TimeTableItem();itemDay1Hour1.setName(mTimeTable.getDay1Hour1());itemDay1Hour1.setType(NON_HEADER);
                TimeTableItem itemDay2Hour1 = new TimeTableItem();itemDay2Hour1.setName(mTimeTable.getDay2Hour1());itemDay2Hour1.setType(NON_HEADER);
                TimeTableItem itemDay3Hour1 = new TimeTableItem();itemDay3Hour1.setName(mTimeTable.getDay3Hour1());itemDay3Hour1.setType(NON_HEADER);
                TimeTableItem itemDay4Hour1 = new TimeTableItem();itemDay4Hour1.setName(mTimeTable.getDay4Hour1());itemDay4Hour1.setType(NON_HEADER);
                TimeTableItem itemDay5Hour1 = new TimeTableItem();itemDay5Hour1.setName(mTimeTable.getDay5Hour1());itemDay5Hour1.setType(NON_HEADER);

                TimeTableItem itemDay0Hour1 = new TimeTableItem();itemDay0Hour1.setName(getString(R.string.lbl_2hour));itemDay0Hour1.setType(HEADER);
                TimeTableItem itemDay1Hour2 = new TimeTableItem();itemDay1Hour2.setName(mTimeTable.getDay1Hour2());itemDay1Hour2.setType(NON_HEADER);
                TimeTableItem itemDay2Hour2 = new TimeTableItem();itemDay2Hour2.setName(mTimeTable.getDay2Hour2());itemDay2Hour2.setType(NON_HEADER);
                TimeTableItem itemDay3Hour2 = new TimeTableItem();itemDay3Hour2.setName(mTimeTable.getDay3Hour2());itemDay3Hour2.setType(NON_HEADER);
                TimeTableItem itemDay4Hour2 = new TimeTableItem();itemDay4Hour2.setName(mTimeTable.getDay4Hour2());itemDay4Hour2.setType(NON_HEADER);
                TimeTableItem itemDay5Hour2 = new TimeTableItem();itemDay5Hour2.setName(mTimeTable.getDay5Hour2());itemDay5Hour2.setType(NON_HEADER);

                TimeTableItem itemDay0Hour2 = new TimeTableItem();itemDay0Hour2.setName(getString(R.string.lbl_3hour));itemDay0Hour2.setType(HEADER);
                TimeTableItem itemDay1Hour3 = new TimeTableItem();itemDay1Hour3.setName(mTimeTable.getDay1Hour3());itemDay1Hour3.setType(NON_HEADER);
                TimeTableItem itemDay2Hour3 = new TimeTableItem();itemDay2Hour3.setName(mTimeTable.getDay2Hour3());itemDay2Hour3.setType(NON_HEADER);
                TimeTableItem itemDay3Hour3 = new TimeTableItem();itemDay3Hour3.setName(mTimeTable.getDay3Hour3());itemDay3Hour3.setType(NON_HEADER);
                TimeTableItem itemDay4Hour3 = new TimeTableItem();itemDay4Hour3.setName(mTimeTable.getDay4Hour3());itemDay4Hour3.setType(NON_HEADER);
                TimeTableItem itemDay5Hour3 = new TimeTableItem();itemDay5Hour3.setName(mTimeTable.getDay5Hour3());itemDay5Hour3.setType(NON_HEADER);

                TimeTableItem itemDay0Hour3 = new TimeTableItem();itemDay0Hour3.setName(getString(R.string.lbl_4hour));itemDay0Hour3.setType(HEADER);
                TimeTableItem itemDay1Hour4 = new TimeTableItem();itemDay1Hour4.setName(mTimeTable.getDay1Hour4());itemDay1Hour4.setType(NON_HEADER);
                TimeTableItem itemDay2Hour4 = new TimeTableItem();itemDay2Hour4.setName(mTimeTable.getDay2Hour4());itemDay2Hour4.setType(NON_HEADER);
                TimeTableItem itemDay3Hour4 = new TimeTableItem();itemDay3Hour4.setName(mTimeTable.getDay3Hour4());itemDay3Hour4.setType(NON_HEADER);
                TimeTableItem itemDay4Hour4 = new TimeTableItem();itemDay4Hour4.setName(mTimeTable.getDay4Hour4());itemDay4Hour4.setType(NON_HEADER);
                TimeTableItem itemDay5Hour4 = new TimeTableItem();itemDay5Hour4.setName(mTimeTable.getDay5Hour4());itemDay5Hour4.setType(NON_HEADER);

                TimeTableItem itemDay0Hour4 = new TimeTableItem();itemDay0Hour4.setName(getString(R.string.lbl_5hour));itemDay0Hour4.setType(HEADER);
                TimeTableItem itemDay1Hour5 = new TimeTableItem();itemDay1Hour5.setName(mTimeTable.getDay1Hour5());itemDay1Hour5.setType(NON_HEADER);
                TimeTableItem itemDay2Hour5 = new TimeTableItem();itemDay2Hour5.setName(mTimeTable.getDay2Hour5());itemDay2Hour5.setType(NON_HEADER);
                TimeTableItem itemDay3Hour5 = new TimeTableItem();itemDay3Hour5.setName(mTimeTable.getDay3Hour5());itemDay3Hour5.setType(NON_HEADER);
                TimeTableItem itemDay4Hour5 = new TimeTableItem();itemDay4Hour5.setName(mTimeTable.getDay4Hour5());itemDay4Hour5.setType(NON_HEADER);
                TimeTableItem itemDay5Hour5 = new TimeTableItem();itemDay5Hour5.setName(mTimeTable.getDay5Hour5());itemDay5Hour5.setType(NON_HEADER);

                TimeTableItem itemDay0Hour5 = new TimeTableItem();itemDay0Hour5.setName(getString(R.string.lbl_6hour));itemDay0Hour5.setType(HEADER);
                TimeTableItem itemDay1Hour6 = new TimeTableItem();itemDay1Hour6.setName(mTimeTable.getDay1Hour6());itemDay1Hour6.setType(NON_HEADER);
                TimeTableItem itemDay2Hour6 = new TimeTableItem();itemDay2Hour6.setName(mTimeTable.getDay2Hour6());itemDay2Hour6.setType(NON_HEADER);
                TimeTableItem itemDay3Hour6 = new TimeTableItem();itemDay3Hour6.setName(mTimeTable.getDay3Hour6());itemDay3Hour6.setType(NON_HEADER);
                TimeTableItem itemDay4Hour6 = new TimeTableItem();itemDay4Hour6.setName(mTimeTable.getDay4Hour6());itemDay4Hour6.setType(NON_HEADER);
                TimeTableItem itemDay5Hour6 = new TimeTableItem();itemDay5Hour6.setName(mTimeTable.getDay5Hour6());itemDay5Hour6.setType(NON_HEADER);

                TimeTableItem itemDay0Hour6 = new TimeTableItem();itemDay0Hour6.setName(getString(R.string.lbl_7hour));itemDay0Hour6.setType(HEADER);
                TimeTableItem itemDay1Hour7 = new TimeTableItem();itemDay1Hour7.setName(mTimeTable.getDay1Hour7());itemDay1Hour7.setType(NON_HEADER);
                TimeTableItem itemDay2Hour7 = new TimeTableItem();itemDay2Hour7.setName(mTimeTable.getDay2Hour7());itemDay2Hour7.setType(NON_HEADER);
                TimeTableItem itemDay3Hour7 = new TimeTableItem();itemDay3Hour7.setName(mTimeTable.getDay3Hour7());itemDay3Hour7.setType(NON_HEADER);
                TimeTableItem itemDay4Hour7 = new TimeTableItem();itemDay4Hour7.setName(mTimeTable.getDay4Hour7());itemDay4Hour7.setType(NON_HEADER);
                TimeTableItem itemDay5Hour7 = new TimeTableItem();itemDay5Hour7.setName(mTimeTable.getDay5Hour7());itemDay5Hour7.setType(NON_HEADER);

                TimeTableItem itemDay0Hour7 = new TimeTableItem();itemDay0Hour7.setName(getString(R.string.lbl_8hour));itemDay0Hour7.setType(HEADER);
                TimeTableItem itemDay1Hour8 = new TimeTableItem();itemDay1Hour8.setName(mTimeTable.getDay1Hour8());itemDay1Hour8.setType(NON_HEADER);
                TimeTableItem itemDay2Hour8 = new TimeTableItem();itemDay2Hour8.setName(mTimeTable.getDay2Hour8());itemDay2Hour8.setType(NON_HEADER);
                TimeTableItem itemDay3Hour8 = new TimeTableItem();itemDay3Hour8.setName(mTimeTable.getDay3Hour8());itemDay3Hour8.setType(NON_HEADER);
                TimeTableItem itemDay4Hour8 = new TimeTableItem();itemDay4Hour8.setName(mTimeTable.getDay4Hour8());itemDay4Hour8.setType(NON_HEADER);
                TimeTableItem itemDay5Hour8 = new TimeTableItem();itemDay5Hour8.setName(mTimeTable.getDay5Hour8());itemDay5Hour8.setType(NON_HEADER);

                mListSubjectId.add(item);mListSubjectId.add(itemDay1);mListSubjectId.add(itemDay2);mListSubjectId.add(itemDay3);
                mListSubjectId.add(itemDay4);mListSubjectId.add(itemDay5);

                mListSubjectId.add(itemDay0Hour0);mListSubjectId.add(itemDay1Hour1);mListSubjectId.add(itemDay2Hour1);
                mListSubjectId.add(itemDay3Hour1);mListSubjectId.add(itemDay4Hour1);mListSubjectId.add(itemDay5Hour1);

                mListSubjectId.add(itemDay0Hour1);mListSubjectId.add(itemDay1Hour2);mListSubjectId.add(itemDay2Hour2);
                mListSubjectId.add(itemDay3Hour2);mListSubjectId.add(itemDay4Hour2);mListSubjectId.add(itemDay5Hour2);

                mListSubjectId.add(itemDay0Hour2);mListSubjectId.add(itemDay1Hour3);mListSubjectId.add(itemDay2Hour3);
                mListSubjectId.add(itemDay3Hour3);mListSubjectId.add(itemDay4Hour3);mListSubjectId.add(itemDay5Hour3);

                mListSubjectId.add(itemDay0Hour3);mListSubjectId.add(itemDay1Hour4);mListSubjectId.add(itemDay2Hour4);
                mListSubjectId.add(itemDay3Hour4);mListSubjectId.add(itemDay4Hour4);mListSubjectId.add(itemDay5Hour4);

                mListSubjectId.add(itemDay0Hour4);mListSubjectId.add(itemDay1Hour5);mListSubjectId.add(itemDay2Hour5);
                mListSubjectId.add(itemDay3Hour5);mListSubjectId.add(itemDay4Hour5);mListSubjectId.add(itemDay5Hour5);

                mListSubjectId.add(itemDay0Hour5);mListSubjectId.add(itemDay1Hour6);mListSubjectId.add(itemDay2Hour6);
                mListSubjectId.add(itemDay3Hour6);mListSubjectId.add(itemDay4Hour6);mListSubjectId.add(itemDay5Hour6);

                mListSubjectId.add(itemDay0Hour6);mListSubjectId.add(itemDay1Hour7);mListSubjectId.add(itemDay2Hour7);
                mListSubjectId.add(itemDay3Hour7);mListSubjectId.add(itemDay4Hour7);mListSubjectId.add(itemDay5Hour7);

                mListSubjectId.add(itemDay0Hour7);mListSubjectId.add(itemDay1Hour8);mListSubjectId.add(itemDay2Hour8);
                mListSubjectId.add(itemDay3Hour8);mListSubjectId.add(itemDay4Hour8);mListSubjectId.add(itemDay5Hour8);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showTimeTable()
    {
        TAG = "showTimeTable";
        Log.d(MODULE, TAG);
        try
        {
            if(mListSubjectId.size()>0)
            {
                adapter = new TimeTableAdapter(mActivity,mListSubjectId,mSubjectList);
                grid_view.setAdapter(adapter);
                grid_view.setVisibility(View.VISIBLE);
                layout_empty.setVisibility(View.GONE);
            }
            else
            {
                text_view_empty.setText(R.string.lbl_timetable_not_found);
                showEmptyView();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public JSONObject Payload_TimeTable()
    {
        TAG = "Payload_TimeTable";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("ClassId", Str_ClassId);
            obj.put("SectionId", Str_SectionId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
    }

    @Override
    public void onSubjectListReceived()
    {
        TAG = "onSubjectListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getSubjects();
            getTimeTableFromService();
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onSubjectListReceivedError(String Str_Msg) {
        showSnackBar(Str_Msg,0);
    }

    public void getSubjects()
    {
        TAG = "getSubjects";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_SUBJECT_LIST, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if (Str_Json.length() > 0)
            {
                response = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>() { }.getType());
                mSubjectList = response.getCclass();
                Log.d(MODULE, TAG + " mSubjectList : " + mSubjectList.size());
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
                Str_ClassId = mStudent.getClassId();
                Log.d(MODULE, TAG + " mStudent : " + mStudent.getClassName());
                Log.d(MODULE, TAG + " Str_ClassId : " + Str_ClassId);
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
            tv_section.setText(Str_SectionName);
            Str_EncodeImage = mUser.getImageData();

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

    public JSONObject PayloadSection()
    {
        TAG = "PayloadSection";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("ClassId", Str_ClassId);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
    }

    public void showEmptyView()
    {
        TAG = "showEmptyView";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        try
        {
            layout_empty.setVisibility(View.VISIBLE);
            grid_view.setVisibility(View.GONE);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_list_view).setVisible(false);
        menu.findItem(R.id.action_chart_view).setVisible(false);
        menu.findItem(R.id.action_help).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    public void showSnackBar(String Str_Msg,final int mService)
    {
        Snackbar snackbar = Snackbar.make(cl_main, Str_Msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.lbl_retry), new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mService==0) getSubjectsListFromService();
                else if(mService==1) getTimeTableFromService();
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
