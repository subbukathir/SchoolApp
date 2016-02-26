package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 25/02/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.asyncprocess.AddEvent;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.AddEventListener;
import com.daemon.oxfordschool.listeners.DateSetListener;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Fragment_Add_Event extends Fragment implements DateSetListener, AddEventListener
{

    public static String MODULE = "Fragment_Add_Event ";
    public static String TAG = "";

    SharedPreferences mPreferences;
    Toolbar mToolbar;
    User mUser;
    Button btn_start_date,btn_end_date,btn_add_event;

    ArrayList<User> mListStudents =new ArrayList<User>();
    Integer mSuccess;

    AppCompatActivity mActivity;
    String Str_Id="",Str_Start_Date="",Str_End_Date="",Str_Event_Name="",Str_Description="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_Date="";

    EditText et_add_event_name, et_add_description;
    public Boolean flag=true;
    TextInputLayout til_add_event_name,til_add_description;
    TextView tv_lbl_start_date,tv_lbl_end_date;
    String Str_Event_Url = ApiConstants.ADD_EVENT_URL;
    Bundle mSavedInstanceState;

    public Fragment_Add_Event()
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
            Str_Date=GetTodayDate();
            ConvertedDate();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_add_event, container, false);
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

            tv_lbl_start_date = (TextView) view.findViewById(R.id.tv_lbl_select_start_date);
            tv_lbl_end_date = (TextView) view.findViewById(R.id.tv_lbl_select_end_date);
            btn_start_date = (Button) view.findViewById(R.id.btn_select_start_date);
            btn_end_date = (Button) view.findViewById(R.id.btn_select_end_date);


            et_add_event_name = (EditText) view.findViewById(R.id.et_add_event_name);
            et_add_description = (EditText) view.findViewById(R.id.et_add_description);

            til_add_event_name= (TextInputLayout) view.findViewById(R.id.til_add_event_name);
            til_add_description= (TextInputLayout) view.findViewById(R.id.til_add_description);

            btn_add_event = (Button) view.findViewById(R.id.btn_add_event);
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
                mToolbar.setTitle(R.string.lbl_add_event);
                mToolbar.setSubtitle("");
                FragmentDrawer.mDrawerLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        FragmentDrawer.mDrawerToggle.syncState();
                    }
                });
                mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            tv_lbl_start_date.setTypeface(font.getHelveticaBold());
            tv_lbl_end_date.setTypeface(font.getHelveticaBold());
            et_add_event_name.setTypeface(font.getHelveticaRegular());
            et_add_description.setTypeface(font.getHelveticaRegular());

            btn_start_date.setTypeface(font.getHelveticaRegular());
            btn_end_date.setTypeface(font.getHelveticaRegular());

            btn_start_date.setOnClickListener(_OnClickListener);
            btn_end_date.setOnClickListener(_OnClickListener);
            btn_add_event.setOnClickListener(_OnClickListener);
            btn_start_date.setText(ConvertedDate());
            btn_end_date.setText(ConvertedDate());


            et_add_event_name.addTextChangedListener(new MyTextWatcher(et_add_event_name));
            et_add_description.addTextChangedListener(new MyTextWatcher(et_add_description));
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

    @Override
    public void onAddEventReceived(String Str_Msg)
    {
        TAG = "onAddEventReceived";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        AppUtils.DialogMessage(mActivity,Str_Msg);
    }

    @Override
    public void onAddEventReceivedError(String Str_Msg)
    {
        TAG = "onAddEventReceivedError";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        AppUtils.DialogMessage(mActivity, Str_Msg);
    }

    View.OnClickListener _OnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.btn_select_start_date:
                     selectDate(view);
                     break;
                case R.id.btn_select_end_date:
                     flag=false;
                     selectDate(view);
                     break;
                case R.id.btn_add_event:
                     if(IsValid()) goto_Add_Event();
                     break;

                default:
                     break;
            }
        }
    };

    public void goto_Add_Event()
    {
        TAG = "goto_Add_Event";
        Log.d(MODULE, TAG);
        AppUtils.showProgressDialog(mActivity);
        et_add_event_name.setText("");
        et_add_description.setText("");
        new AddEvent(Str_Event_Url,this,Payload_Add_Event()).addEvent();
    }

    public JSONObject Payload_Add_Event()
    {
        TAG = "Payload_Add_Event";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try {
            obj.put("UserId", Str_Id);
            obj.put("Name", Str_Event_Name);
            obj.put("Description",Str_Description);
            obj.put("StartDate",Str_Start_Date);
            obj.put("EndDate",Str_End_Date);
            obj.put("Mode","0");
            obj.put("EventId","");

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
            Str_Start_Date = Str_TodayDate;
            Str_End_Date = Str_TodayDate;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return Str_TodayDate;
    }

    public void selectDate(View view) {
        DialogFragment newFragment = new SelectDateFragment(Fragment_Add_Event.this);
        newFragment.show(mActivity.getSupportFragmentManager(), "DatePicker");
    }

    public void populateSetDate(int year, int month, int day) {
        Str_Date = year + "-" + month + "-"+day;
        if(flag)
        {
            Str_Start_Date=Str_Date;
            btn_start_date.setText(ConvertedDate());
        }
        else
        {
            Str_End_Date=Str_Date;
            btn_end_date.setText(ConvertedDate());
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

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_add_assignment_I:
                    validateEventName();
                    break;
                case R.id.et_add_assignment_II:
                    validateDescription();
                    break;
                default:
                    break;

            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        }
    }

    private boolean validateEventName() {
        if (et_add_event_name.getText().toString().trim().isEmpty()) {
            til_add_event_name.setError(getString(R.string.lbl_msg_blank_event_name));
            requestFocus(et_add_event_name);
            return false;
        }else if(et_add_event_name.getText().length()<3){
            til_add_event_name.setError(getString(R.string.lbl_msg_valid_event_name));
            requestFocus(et_add_event_name);
            return false;
        }else {
            Str_Event_Name=et_add_event_name.getText().toString().trim();
            til_add_event_name.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDescription() {
        if (et_add_description.getText().toString().trim().isEmpty()) {
            til_add_description.setError(getString(R.string.lbl_msg_blank_description));
            requestFocus(et_add_description);
            return false;
        }else if(et_add_description.getText().length()<3  ){
            til_add_description.setError(getString(R.string.lbl_msg_valid_description));
            requestFocus(et_add_description);
            return false;
        }else {
            Str_Description=et_add_description.getText().toString().trim();
            til_add_description.setErrorEnabled(false);
        }

        return true;
    }

    public boolean IsValid() {
        TAG = "IsValid";
        Log.d(MODULE, TAG);

        boolean IsValid = true;

        if(validateEventName() && validateDescription()==true) IsValid=true;
        else IsValid=false;

        return IsValid;
    }
}