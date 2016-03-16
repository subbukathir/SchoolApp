package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.adapter.HomeWorkAdapter;
import com.daemon.oxfordschool.asyncprocess.GetHomeWorkList;
import com.daemon.oxfordschool.asyncprocess.GetStudentProfile;
import com.daemon.oxfordschool.classes.CHomework;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.DateSetListener;
import com.daemon.oxfordschool.listeners.HomeWorkListListener;
import com.daemon.oxfordschool.listeners.Homework_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.ViewStudentProfileListener;
import com.daemon.oxfordschool.response.HomeWorkList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Fragment_HomeWork_Student extends Fragment implements HomeWorkListListener,Homework_List_Item_Click_Listener,
        ViewStudentProfileListener,DateSetListener
{

    public static String MODULE = "Fragment_HomeWork_Student ";
    public static String TAG = "";

    TextView tv_lbl_select_date,text_view_empty,tv_name,tv_class,tv_section;
    ImageView imageView;
    Bitmap mDecodedImage;
    Button btn_select_date;
    RelativeLayout layout_empty;
    SwipeRefreshLayout swipeRefreshLayout;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;

    SharedPreferences mPreferences;
    User mUser,mStudent;
    ArrayList<CHomework> mListHomeWork =new ArrayList<CHomework>();
    HomeWorkList_Response response;

    AppCompatActivity mActivity;
    String Str_Id="",Str_Date="" ,Str_EncodeImage="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_Url = ApiConstants.STUDENT_PROFILE_URL;
    String Str_HomeWorkList_Url = ApiConstants.HOMEWORK_LIST_URL;

    public Fragment_HomeWork_Student()
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
            Str_Date=GetTodayDate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_homework_student, container, false);
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
            tv_lbl_select_date = (TextView) view.findViewById(R.id.tv_lbl_select_date);
            btn_select_date = (Button) view.findViewById(R.id.btn_select_date);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_homework);
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
            tv_lbl_select_date.setTypeface(font.getHelveticaRegular());
            text_view_empty.setTypeface(font.getHelveticaRegular());
            btn_select_date.setTypeface(font.getHelveticaRegular());
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
            btn_select_date.setOnClickListener(_OnClickListener);
            btn_select_date.setText(ConvertedDate());
            setEmptyText();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setEmptyText()
    {
        StringBuilder Str_EmptyMessage = new StringBuilder();
        Str_EmptyMessage.append(getString(R.string.lbl_no_homework)).append(" ");
        Str_EmptyMessage.append(Str_Date);
        text_view_empty.setText(Str_EmptyMessage.toString());
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

    View.OnClickListener _OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectDate(view);
        }
    };

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
    public void onHomeWorkListReceived() {
        TAG = "onHomeWorkListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getHomeWorksList();
            showHomeWorkList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onHomeWorkListReceivedError(String Str_Msg) {
        TAG = "onHomeWorkListReceivedError";
        Log.d(MODULE, TAG);
        try
        {
            setEmptyText();
            showEmptyView();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onHomeWorkListItemClicked(int position) {

    }

    public void getHomeWorksList()
    {
        TAG = "getHomeWorksList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_HOMEWORK_LIST,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                response = (HomeWorkList_Response) AppUtils.fromJson(Str_Json, new TypeToken<HomeWorkList_Response>(){}.getType());
                mListHomeWork = response.getChomework();
                Log.d(MODULE, TAG + " mListHomeWork : " + mListHomeWork.size());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showHomeWorkList()
    {
        TAG = "showHomeWorkList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListHomeWork.size()>0)
            {
                HomeWorkAdapter adapter = new HomeWorkAdapter(mListHomeWork,this);
                recycler_view.setAdapter(adapter);
                layout_empty.setVisibility(View.GONE);
                recycler_view.setVisibility(View.VISIBLE);
            }
            else
            {
                setEmptyText();
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

    public void getHomeWorks(String Str_Date)
    {
        TAG = "getHomeWorks";
        Log.d(MODULE, TAG);
        try
        {
            new GetHomeWorkList(Str_HomeWorkList_Url,Payload_HomeWork(Str_Date),this).getHomeWorks();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public JSONObject Payload_HomeWork(String Str_Date)
    {
        TAG = "Payload";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("ClassId", mStudent.getClassId());
            obj.put("SectionId", mStudent.getSectionId());
            obj.put("HomeWorkDate", Str_Date);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
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

    public void selectDate(View view) {
        DialogFragment newFragment = new SelectDateFragment(Fragment_HomeWork_Student.this);
        newFragment.show(mActivity.getSupportFragmentManager(), "DatePicker");
    }

    public void populateSetDate(int year, int month, int day) {
        Str_Date = year + "-" + month + "-"+day;
        btn_select_date.setText(ConvertedDate());
        getHomeWorks(Str_Date);
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
}
