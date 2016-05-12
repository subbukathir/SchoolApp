package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.adapter.ClassAdapter;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.UpdateClass;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.AddClassListener;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.Class_List_Item_Click_Listener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_Class_List extends Fragment implements ClassListListener,Class_List_Item_Click_Listener,AddClassListener
{

    public static String MODULE = "Fragment_Class_List ";
    public static String TAG = "";

    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    TextView text_view_empty;
    RelativeLayout layout_empty;

    SharedPreferences mPreferences;
    User mUser;
    Common_Class mClass;
    ArrayList<Common_Class> mListClasses =new ArrayList<Common_Class>();
    CommonList_Response response;
    Toolbar mToolbar;

    AppCompatActivity mActivity;
    Bundle mSavedInstanceState;
    FloatingActionButton fab_add_subject;
    String Str_Id="",Str_Class_Name="",Str_ClassId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_Add_Class_Url = ApiConstants.ADD_CLASS_URL;

    public Fragment_Class_List()
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
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS,Context.MODE_PRIVATE);
            getProfile();
            AppUtils.showProgressDialog(mActivity);
            new ClassList_Process(mActivity,this).GetClassList();
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
        View rootView = inflater.inflate(R.layout.fragment_class_list, container, false);
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
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_class);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            fab_add_subject = (FloatingActionButton) view.findViewById(R.id.fab);

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
                getClassList();
                showClassList();
                if (mActivity.getCurrentFocus() != null)
                {
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
                }
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
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
            text_view_empty.setTypeface(font.getHelveticaRegular());
            if(mUser.getUserType().equals(ApiConstants.ADMIN)) fab_add_subject.setVisibility(View.VISIBLE);
            fab_add_subject.setOnClickListener(_OnClickListener);
            SetActionBar();
        }
        catch (Exception ex)
        {

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
                mToolbar.setTitle(R.string.lbl_class_management);
                mToolbar.setSubtitle("");
                if(!MainActivity.mTwoPane)
                {
                    FragmentDrawer.mDrawerLayout.post(new Runnable() {
                        @Override
                        public void run() {
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

    @Override
    public void onClassListReceived()
    {
        TAG = "onClassListReceived";
        Log.d(MODULE, TAG);
        try
        {
            AppUtils.hideProgressDialog();
            getClassList();
            showClassList();
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onClassListReceivedError(String Str_Msg)
    {
        TAG = "onClassListReceivedError";
        Log.d(MODULE, TAG);
        try
        {
            AppUtils.hideProgressDialog();
            text_view_empty.setText(Str_Msg);
            showEmptyView();
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onClassListItemClicked(int position) {
        TAG = "onClassListItemClicked";
        Log.d(MODULE, TAG);
        try
        {
            goto_UpdateClassFragment(position);
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onClassListDeleteBtnClicked(int position) {
        TAG = "onClassListItemClicked";
        Log.d(MODULE, TAG);
        try
        {
            Str_Class_Name = mListClasses.get(position).getName();
            Str_ClassId = mListClasses.get(position).getID();
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(getString(R.string.lbl_msg_delete_class))
                    .setPositiveButton(getString(R.string.lbl_yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.lbl_no), dialogClickListener)
                    .show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClassUpdated(String Str_Msg)
    {
        TAG = "onClassUpdated";
        Log.d(MODULE, TAG);
        try
        {
            AppUtils.hideProgressDialog();
            AppUtils.DialogMessage(mActivity, Str_Msg);
            new ClassList_Process(mActivity,this).GetClassList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClassUpdatedError(String Str_Msg)
    {
        TAG = "onClassUpdatedError";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        AppUtils.DialogMessage(mActivity, Str_Msg);
    }


    public void getClassList()
    {
        TAG = "getClassList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_CLASS_LIST,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                response = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>(){}.getType());
                mListClasses = response.getCclass();
                Log.d(MODULE, TAG + " mListClasses : " + mListClasses.size());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showClassList()
    {
        TAG = "showClassList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListClasses.size()>0)
            {
                ClassAdapter adapter = new ClassAdapter(mListClasses,this);
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
            textView.setText(R.string.lbl_no_class_list);
            textView.setTypeface(font.getHelveticaRegular());
            ((ViewGroup)recycler_view.getParent().getParent()).addView(emptyView);
            recycler_view.setEmptyView(emptyView);
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
                case R.id.fab:
                    goto_AddClassFragment();
                    break;
            }
        }
    };

    public void goto_AddClassFragment()
    {
        TAG = "goto_AddClassFragment";
        Log.d(MODULE, TAG);
        try
        {
            mSavedInstanceState=getSavedState();
            Fragment _fragment = new Fragment_Add_Class();
            FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, _fragment,AppUtils.FRAGMENT_ADD_CLASS);
            fragmentTransaction.addToBackStack(AppUtils.FRAGMENT_ADD_CLASS + "");
            fragmentTransaction.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void goto_UpdateClassFragment(int position)
    {
        TAG = "goto_UpdateClassFragment";
        Log.d(MODULE, TAG);

        if(mListClasses.size()>0)
        {
            mSavedInstanceState=getSavedState();
            mClass = mListClasses.get(position);
            Log.d(MODULE, TAG + "values of list " + mClass.getName());
            Log.d(MODULE, TAG + "getSectionId of list " + mClass.getID());

            Bundle  mBundle = new Bundle();

            mBundle.putParcelable(AppUtils.B_SINGLE_CLASS, mClass);
            mBundle.putInt(AppUtils.B_MODE,AppUtils.MODE_UPDATE);

            Fragment mFragment = new Fragment_Add_Class();
            FragmentManager mManager = mActivity.getSupportFragmentManager();
            FragmentTransaction mTransaction = mManager.beginTransaction();
            mFragment.setArguments(mBundle);
            mTransaction.replace(R.id.container_body, mFragment,AppUtils.FRAGMENT_ADD_CLASS);
            mTransaction.addToBackStack(AppUtils.FRAGMENT_ADD_CLASS + "");
            mTransaction.commit();
        }
    }

    public JSONObject Payload_Delete_Class()
    {
        TAG = "Payload_Delete_Class";
        Log.d(MODULE, TAG);

        JSONObject obj=new JSONObject();
        try {

            obj.put("UserId", Str_Id);
            obj.put("ClassName", Str_Class_Name);
            obj.put("ClassId",Str_ClassId);
            obj.put("Mode","2");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        TAG = "onSaveInstanceState";
        Log.d(MODULE, TAG);
        mSavedInstanceState=getSavedState();
    }

    public Bundle getSavedState()
    {
        TAG = "getSavedState";
        Log.d(MODULE, TAG);

        Bundle outState = new Bundle();
        try
        {

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


        Log.d(MODULE, TAG);
        return outState;
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                 if(!MainActivity.mTwoPane)
                 {
                    if(FragmentDrawer.mDrawerLayout.isDrawerOpen(GravityCompat.START))
                        FragmentDrawer.mDrawerLayout.closeDrawer(GravityCompat.START);
                    else
                        FragmentDrawer.mDrawerLayout.openDrawer(GravityCompat.START);
                 }
                 return true;
            default:
                 break;

        }
        return super.onOptionsItemSelected(item);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                     new UpdateClass(Str_Add_Class_Url,Fragment_Class_List.this,Payload_Delete_Class()).updateClass();
                     break;
                case DialogInterface.BUTTON_NEGATIVE:
                     break;
            }
        }
    };
}
