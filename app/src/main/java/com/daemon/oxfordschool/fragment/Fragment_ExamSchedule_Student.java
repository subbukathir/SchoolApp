package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.adapter.ExamListAdapter;
import com.daemon.oxfordschool.asyncprocess.ExamTypeList_Process;
import com.daemon.oxfordschool.asyncprocess.GetExamList;
import com.daemon.oxfordschool.asyncprocess.GetStudentProfile;
import com.daemon.oxfordschool.classes.CExam;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ExamListListener;
import com.daemon.oxfordschool.listeners.ExamTypeListListener;
import com.daemon.oxfordschool.listeners.Exam_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.ViewStudentProfileListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.daemon.oxfordschool.response.ExamList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_ExamSchedule_Student extends Fragment implements ViewStudentProfileListener,ExamTypeListListener,
        ExamListListener,Exam_List_Item_Click_Listener
{

    public static String MODULE = "Fragment_ExamSchedule_Student ";
    public static String TAG = "";

    TextView tv_lbl_select_exam_type,text_view_empty,tv_lbl_subject_name,tv_lbl_exam_date,tv_name,tv_class,tv_section;
    ImageView imageView;
    Spinner spinner_exam_type;
    RelativeLayout layout_empty;
    SwipeRefreshLayout swipeRefreshLayout;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;

    SharedPreferences mPreferences;
    User mUser,mStudent;
    ViewPager vp_student;
    ArrayList<CExam> mListExams =new ArrayList<CExam>();
    ArrayList<Common_Class> mListExamType =new ArrayList<Common_Class>();

    ExamList_Response response;
    CommonList_Response examListResponse;

    AppCompatActivity mActivity;
    int mSelectedPosition;
    Bitmap mDecodedImage;
    String Str_Id="",Str_ExamTypeId="",Str_ClassId="",Str_EncodeImage="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_Url = ApiConstants.STUDENT_PROFILE_URL;
    String Str_ExamList_Url = ApiConstants.EXAM_LIST_URL;

    public Fragment_ExamSchedule_Student()
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
            new GetStudentProfile(Str_Url,Payload(),this).getStudentProfile();
            new ExamTypeList_Process(mActivity,this).GetExamTypeList();
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
        View rootView = inflater.inflate(R.layout.fragment_exam_schedule_student, container, false);
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

            imageView = (ImageView) view.findViewById(R.id.iv_profile);
            tv_name  = (TextView) view.findViewById(R.id.tv_header_name);
            tv_class  = (TextView) view.findViewById(R.id.tv_class_name);
            tv_section  = (TextView) view.findViewById(R.id.tv_section_name);
            tv_lbl_select_exam_type = (TextView) view.findViewById(R.id.tv_select_exam_type);
            spinner_exam_type = (Spinner) view.findViewById(R.id.spinner_exam_type);
            tv_lbl_subject_name = (TextView) view.findViewById(R.id.tv_lbl_subject_name);
            tv_lbl_exam_date = (TextView) view.findViewById(R.id.tv_lbl_exam_date);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_exam_list);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
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
            layout_empty.setVisibility(View.GONE);
            tv_name.setTypeface(font.getHelveticaRegular());
            tv_class.setTypeface(font.getHelveticaRegular());
            tv_section.setTypeface(font.getHelveticaRegular());
            tv_lbl_select_exam_type.setTypeface(font.getHelveticaRegular());
            tv_lbl_subject_name.setTypeface(font.getHelveticaBold());
            tv_lbl_exam_date.setTypeface(font.getHelveticaBold());
            text_view_empty.setTypeface(font.getHelveticaRegular());
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
            spinner_exam_type.setOnItemSelectedListener(_OnItemSelectedListener);
            text_view_empty.setText(getString(R.string.lbl_no_exams));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    AdapterView.OnItemSelectedListener _OnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

            Str_ExamTypeId = mListExamType.get(position).getID();
            getExamListFromService(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

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

    public void getExamListFromService(int position)
    {
        TAG = "getExamListFromService";
        Log.d(MODULE, TAG);
        try
        {
            Str_ClassId = mStudent.getClassId();
            new GetExamList(Str_ExamList_Url,Payload_ExamList(),this).getExams();
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
    public void onExamTypeListReceived()
    {
        TAG = "onExamTypeListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getExamTypeList();
            setExamTypeList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onExamTypeListReceivedError(String Str_Msg)
    {
        TAG = "onClassListReceived";
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
    public void onExamListReceived() {
        TAG = "onExamTypeListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getExamList();
            showExamList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onExamListReceivedError(String Str_Msg) {
        TAG = "onClassListReceived";
        Log.d(MODULE, TAG);

        try
        {
            showEmptyView();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onExamListItemClicked(int position) {

    }

    public void getExamTypeList()
    {
        TAG = "getExamTypeList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_EXAM_TYPE_LIST,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                examListResponse = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>(){}.getType());
                mListExamType = examListResponse.getCclass();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setExamTypeList()
    {
        TAG = "setExamTypeList";
        Log.d(MODULE, TAG);
        try
        {
           if(mListExamType.size()>0)
           {
               String[] items = AppUtils.getArray(mListExamType);
               ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
               adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               spinner_exam_type.setAdapter(adapter);
           }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getExamList()
    {
        TAG = "getExamList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_EXAM_LIST,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                response = (ExamList_Response) AppUtils.fromJson(Str_Json, new TypeToken<ExamList_Response>(){}.getType());
                mListExams = response.getCexam();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showExamList()
    {
        TAG = "showExamList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListExams.size()>0)
            {
                ExamListAdapter adapter = new ExamListAdapter(mListExams,this);
                recycler_view.setAdapter(adapter);
                layout_empty.setVisibility(View.GONE);
                recycler_view.setVisibility(View.VISIBLE);
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
            layout_empty.setVisibility(View.VISIBLE);
            recycler_view.setVisibility(View.GONE);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public JSONObject Payload_ExamList()
    {
        TAG = "Payload_ExamList";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("ExamType", Str_ExamTypeId);
            obj.put("ClassId", Str_ClassId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
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
