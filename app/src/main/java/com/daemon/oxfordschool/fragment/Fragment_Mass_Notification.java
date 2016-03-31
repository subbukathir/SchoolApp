package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 28/03/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.daemon.oxfordschool.asyncprocess.MassNotification;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.SendNotificationListener;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

public class Fragment_Mass_Notification extends Fragment implements SendNotificationListener
{

    public static String MODULE = "Fragment_Mass_Notification ";
    public static String TAG = "";

    SharedPreferences mPreferences;
    Toolbar mToolbar;
    User mUser;
    Button btn_send_notification;

    AppCompatActivity mActivity;
    String Str_Id="",Str_Notification="", Str_Title="";
    private Font font= MyApplication.getInstance().getFontInstance();

    EditText et_notification_message,et_title;
    TextInputLayout til_notification_message,til_title;
    String Str_Send_Notification_Url = ApiConstants.MASS_NOTIFICATION_URL;
    FragmentManager mManager;

    public Fragment_Mass_Notification()
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
            mManager = mActivity.getSupportFragmentManager();
            //getProfile();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_mass_notification, container, false);
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
            et_title = (EditText) view.findViewById(R.id.et_title_notification);
            et_notification_message = (EditText) view.findViewById(R.id.et_notification);

            til_title= (TextInputLayout) view.findViewById(R.id.til_title_notification);
            til_notification_message= (TextInputLayout) view.findViewById(R.id.til_notification);

            btn_send_notification = (Button) view.findViewById(R.id.btn_send_notification);
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

    @Override
    public void onSendNotificationReceived(String Str_Msg)
    {
        TAG = "onSendNotificationReceived";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        AppUtils.DialogMessage(mActivity, Str_Msg);
    }

    @Override
    public void onSendNotificationReceivedError(String Str_Msg)
    {
        TAG = "onSendNotificationReceivedError";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        AppUtils.DialogMessage(mActivity, Str_Msg);
    }

    public void setProperties()
    {
        TAG = "setProperties";
        Log.d(MODULE, TAG);
        try
        {
            et_title.setTypeface(font.getHelveticaRegular());
            et_notification_message.setTypeface(font.getHelveticaRegular());
            btn_send_notification.setOnClickListener(_OnClickListener);
            et_title.addTextChangedListener(new MyTextWatcher(et_title));
            et_notification_message.addTextChangedListener(new MyTextWatcher(et_notification_message));
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

    View.OnClickListener _OnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.btn_send_notification:
                     if(IsValid())
                     {
                         AppUtils.showProgressDialog(mActivity);
                         send_notification();
                     }
                     break;

                default:
                     break;
            }
        }
    };

    public JSONObject Payload_Send_Notification()
    {
        TAG = "Payload_Send_Notification";
        Log.d(MODULE, TAG);

        JSONObject obj=new JSONObject();
        try {
            obj.put("Title", Str_Title);
            obj.put("Message", Str_Notification);
            obj.put("Topic","Mass");

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
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
                case R.id.et_title_notification:
                    validateTitle();
                    break;
                case R.id.et_notification:
                    validateNotification();
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

    private boolean validateTitle() {
        if (et_title.getText().toString().trim().isEmpty()) {
            til_title.setError(getString(R.string.lbl_msg_blank_title));
            requestFocus(et_title);
            return false;
        }else if(et_title.getText().length()<3){
            til_title.setError(getString(R.string.lbl_msg_valid_title));
            requestFocus(et_title);
            return false;
        }else {
            Str_Title=et_title.getText().toString().trim();
            til_title.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateNotification() {
        if (et_notification_message.getText().toString().trim().isEmpty()) {
            til_notification_message.setError(getString(R.string.lbl_msg_blank_message));
            requestFocus(et_title);
            return false;
        }else if(et_notification_message.getText().length()<3){
            til_notification_message.setError(getString(R.string.lbl_msg_valid_message));
            requestFocus(et_notification_message);
            return false;
        }else {
            Str_Notification=et_notification_message.getText().toString().trim();
            til_notification_message.setErrorEnabled(false);
        }
        return true;
    }

    public boolean IsValid() {
        TAG = "IsValid";
        Log.d(MODULE, TAG);

        boolean IsValid = true;

        if((validateTitle()==true)&&(validateNotification()==true)) IsValid=true;
        else IsValid=false;

        return IsValid;
    }

    public void send_notification()
    {
        TAG = "send_notification";
        Log.d(MODULE, TAG);
        new MassNotification(Str_Send_Notification_Url,this,Payload_Send_Notification()).sendNotification();
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_list_view).setVisible(false);
        menu.findItem(R.id.action_chart_view).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}
