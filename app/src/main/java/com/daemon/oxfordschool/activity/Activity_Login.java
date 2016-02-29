package com.daemon.oxfordschool.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.asyncprocess.UserLogin_Process;
import com.daemon.oxfordschool.listeners.LoginListener;
import com.daemon.oxfordschool.Utils.AppUtils;

import com.daemon.oxfordschool.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daemonsoft on 28/1/16.
 */
public class Activity_Login extends AppCompatActivity implements LoginListener
{
    public static String MODULE = "Activity_Login";
    public static String TAG = "";

    boolean isAllEnteryFilled = true;
    Font font= MyApplication.getInstance().getFontInstance();
    TextView tv_welcome;
    String username, password;
    String Url = ApiConstants.LOGIN_URL;
    Button button_login;
    EditText et_username, et_password;
    TextInputLayout til_username,til_password;
    Context mContext;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            setContentView(R.layout.activity_login);
            TAG = "onCreate";
            Log.d(MODULE, TAG);
            mContext = getApplicationContext();

            tv_welcome = (TextView) findViewById(R.id.tv_welcome);
            button_login = (Button) findViewById(R.id.btn_login);
            et_username = (EditText) findViewById(R.id.et_login_username);
            et_password = (EditText) findViewById(R.id.et_login_password);

            til_username= (TextInputLayout) findViewById(R.id.til_login_username);
            til_password= (TextInputLayout) findViewById(R.id.til_login_password);

            button_login.setOnClickListener(_OnClickListner);

            et_username.addTextChangedListener(new MyTextWatcher(et_username));
            et_password.addTextChangedListener(new MyTextWatcher(et_password));

            tv_welcome.setTypeface(font.getHelveticaRegular());
            et_username.setTypeface(font.getHelveticaRegular());
            et_password.setTypeface(font.getHelveticaRegular());
            button_login.setTypeface(font.getHelveticaRegular());

            mToolbar = (Toolbar) findViewById(R.id.toolbar);

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Login");

            et_username.setText("");
            et_password.setText("daemon");
        }
        catch (Exception ex)
        {

        }
    }

    View.OnClickListener _OnClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TAG = "onClick";
            Log.d(MODULE, TAG);

            switch (v.getId()) {
                case R.id.btn_login:
                    doLogin();
                    break;
                default:
                    break;
            }
        }
    };

    public void doLogin() {
        TAG = "doLogin";
        Log.d(MODULE, TAG);

        if (IsValid())
        {
            AppUtils.showProgressDialog(this);
            new UserLogin_Process(Url, Activity_Login.this, Payload()).GetLogin();
        }
    }

    public boolean IsValid() {
        TAG = "IsValid";
        Log.d(MODULE, TAG);

        boolean IsValid = true;

        if(validateUserName() && validatePassword()==true) IsValid=true;
                else IsValid=false;

        return IsValid;
    }

    @Override
    public void onLoginSuccess() {
        TAG="onLoginSuccess";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        et_username.setText("");
        et_password.setText("");
        et_username.requestFocus();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoginFailed(String msg) {
        TAG = "onLoginFailed";
        Log.d(MODULE,TAG + msg);
        AppUtils.hideProgressDialog();
        AppUtils.showDialog(this,msg);
    }

    public JSONObject Payload()
    {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Username", username);
            obj.put("Password", password);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        }
    }

    private boolean validateUserName() {
        if (et_username.getText().toString().trim().isEmpty()) {
            til_username.setError(getString(R.string.lbl_msg_blank_username));
            requestFocus(et_username);
            return false;
        }else if(et_username.getText().length()<4){
            til_username.setError(getString(R.string.lbl_msg_valid_username));
            requestFocus(et_username);
            return false;
        }else {
            username=et_username.getText().toString().trim();
            til_username.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (et_password.getText().toString().trim().isEmpty()) {
            til_password.setError(getString(R.string.lbl_msg_blank_password));
            requestFocus(et_password);
            return false;
        }else if(et_password.getText().length()<6){
            til_password.setError(getString(R.string.lbl_msg_valid_password));
            requestFocus(et_password);
            return false;
        }else {
            password=et_password.getText().toString().trim();
            til_password.setErrorEnabled(false);
        }

        return true;
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
                case R.id.et_login_username:
                    validateUserName();
                    break;
                case R.id.et_login_password:
                    validatePassword();
                    break;
                default:
                    break;

            }
        }
    }

}
