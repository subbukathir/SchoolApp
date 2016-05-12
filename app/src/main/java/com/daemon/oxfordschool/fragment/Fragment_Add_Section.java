package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 18/03/16.
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

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.asyncprocess.UpdateClass;
import com.daemon.oxfordschool.asyncprocess.UpdateSection;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.AddClassListener;
import com.daemon.oxfordschool.listeners.AddSectionListener;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

public class Fragment_Add_Section extends Fragment implements AddSectionListener
{

    public static String MODULE = "Fragment_Add_Section ";
    public static String TAG = "";

    SharedPreferences mPreferences;
    Toolbar mToolbar;
    User mUser;
    Common_Class mSection;
    Button btn_add_section;

    AppCompatActivity mActivity;
    String Str_Id="",Str_Section_Name="",Str_SectionId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    Integer mMode=0;

    EditText et_add_section_name;
    TextInputLayout til_add_section_name;
    String Str_Add_Section_Url = ApiConstants.ADD_SECTION_URL;
    Bundle mBundle;
    FragmentManager mManager;

    public Fragment_Add_Section()
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
            mBundle=savedInstanceState;
            mManager = mActivity.getSupportFragmentManager();
            mBundle = this.getArguments();
            getProfile();
            if(mBundle!=null)
            {
                getBundle();
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
        View rootView = inflater.inflate(R.layout.fragment_add_section, container, false);
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
            et_add_section_name = (EditText) view.findViewById(R.id.et_add_section_name);
            til_add_section_name= (TextInputLayout) view.findViewById(R.id.til_add_section_name);
            btn_add_section = (Button) view.findViewById(R.id.btn_add_section);
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
                if(mMode==AppUtils.MODE_ADD) mToolbar.setTitle(R.string.lbl_add_new_section);
                else mToolbar.setTitle(R.string.lbl_update_section);
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

    public void setProperties()
    {
        TAG = "setProperties";
        Log.d(MODULE, TAG);
        try
        {
            et_add_section_name.setTypeface(font.getHelveticaRegular());
            btn_add_section.setOnClickListener(_OnClickListener);

            if(!Str_SectionId.equals(""))
            {
                Log.d(MODULE, TAG + "bundle available");
                btn_add_section.setText(getString(R.string.lbl_update));
                et_add_section_name.setText(Str_Section_Name);
            }
            else
            {
                btn_add_section.setText(getString(R.string.lbl_add));
            }
            et_add_section_name.addTextChangedListener(new MyTextWatcher(et_add_section_name));
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
    public void onSectionUpdated(String Str_Msg)
    {
        TAG = "onSectionUpdated";
        Log.d(MODULE, TAG);
        try
        {
            AppUtils.hideProgressDialog();
            AppUtils.DialogMessage(mActivity, Str_Msg);
            goto_SectionListFragment();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onSectionUpdatedError(String Str_Msg)
    {
        TAG = "onClassUpdatedError";
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
                case R.id.btn_add_section:
                     if(IsValid()) addSection();
                     break;

                default:
                     break;
            }
        }
    };

    public void addSection()
    {
        TAG = "addSection";
        Log.d(MODULE, TAG);
        AppUtils.showProgressDialog(mActivity);
        et_add_section_name.setText("");
        new UpdateSection(Str_Add_Section_Url,this,Payload_Add_Section()).updateSection();
    }

    public JSONObject Payload_Add_Section()
    {
        TAG = "Payload_Add_Section";
        Log.d(MODULE, TAG);

        JSONObject obj=new JSONObject();
        try {

            obj.put("UserId", Str_Id);
            obj.put("SectionName", Str_Section_Name);
            obj.put("SectionId",Str_SectionId);
            obj.put("Mode",mMode.toString());
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
                case R.id.et_add_subject_name:
                     validateClassName();
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

    private boolean validateClassName() {
        if (et_add_section_name.getText().toString().trim().isEmpty()) {
            til_add_section_name.setError(getString(R.string.lbl_msg_blank_class_name));
            requestFocus(et_add_section_name);
            return false;
        }else if(et_add_section_name.getText().length()<1){
            til_add_section_name.setError(getString(R.string.lbl_msg_min_class_name));
            requestFocus(et_add_section_name);
            return false;
        }else {
            Str_Section_Name=et_add_section_name.getText().toString().trim();
            til_add_section_name.setErrorEnabled(false);
        }
        return true;
    }

    public boolean IsValid() {
        TAG = "IsValid";
        Log.d(MODULE, TAG);

        boolean IsValid = true;

        if(validateClassName()==true) IsValid=true;
        else IsValid=false;

        return IsValid;
    }

    public void goto_SectionListFragment()
    {
        TAG = "goto_SectionListFragment";
        Log.d(MODULE, TAG);

        try
        {
            Fragment _fragment = new Fragment_Section_List();
            FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, _fragment);
            fragmentTransaction.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getBundle()
    {
        TAG = "getBundle";
        Log.d(MODULE, TAG);

        mMode = mBundle.getInt(AppUtils.B_MODE);
        mSection= mBundle.getParcelable(AppUtils.B_SINGLE_SECTION);

        Str_Section_Name = mSection.getName();
        Str_SectionId = mSection.getID();
        Str_Id = mUser.getID();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
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
                 if(!MainActivity.mTwoPane) FragmentDrawer.mDrawerLayout.closeDrawer(GravityCompat.START);
                 mManager.popBackStack();
                 return true;
            default:
                 break;

        }
        return super.onOptionsItemSelected(item);
    }


}
