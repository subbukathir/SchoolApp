package com.daemon.oxfordschool.fragment;

/**
 * Created by Vikram on 29/07/15.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.adapter.AssignedSectionAdapter;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.GetStudentList;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.asyncprocess.AssignSection;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.AssignSectionListener;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.Section_List_Item_Click_Listener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_Assigned_Section_List extends Fragment implements ClassListListener,Section_List_Item_Click_Listener,
        SectionListListener,AssignSectionListener
{

    public static String MODULE = "Fragment_Assigned_Section_List ";
    public static String TAG = "";

    CoordinatorLayout cl_main;
    TextView tv_lbl_class,text_view_empty;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    RelativeLayout layout_empty;
    LinearLayout layout_section;
    Spinner spinner_class;

    SharedPreferences mPreferences;
    User mUser;
    Common_Class mSection;
    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    CommonList_Response responseCommon;
    Toolbar mToolbar;

    AppCompatActivity mActivity;
    Bundle mSavedInstanceState;
    FloatingActionButton fab_add_section;
    String Str_Id="",Str_ClassId="",Str_SectionId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_Add_Section_Url = ApiConstants.ASSIGN_SECTION_URL;
    int mClassListPosition=0;
    final static String ARG_CLASS_LIST_POSITION = "Class_List_Position";

    public Fragment_Assigned_Section_List()
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
        View rootView = inflater.inflate(R.layout.fragment_assigned_section_list, container, false);
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
            tv_lbl_class = (TextView) view.findViewById(R.id.tv_lbl_class);
            layout_section = (LinearLayout) view.findViewById(R.id.layout_section);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            fab_add_section = (FloatingActionButton) view.findViewById(R.id.fab);
            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_section);
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
                mClassListPosition = mSavedInstanceState.getInt(ARG_CLASS_LIST_POSITION);
                Log.d(MODULE, TAG + " mClassListPosition:" + mClassListPosition);
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
            tv_lbl_class.setTypeface(font.getHelveticaRegular());
            text_view_empty.setTypeface(font.getHelveticaRegular());
            layout_section.setVisibility(View.GONE);
            if(mUser.getUserType().equals(ApiConstants.ADMIN)) fab_add_section.setVisibility(View.VISIBLE);
            fab_add_section.setOnClickListener(_OnClickListener);
            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            SetActionBar();
            showClassList();
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
                mToolbar.setTitle(R.string.lbl_assign_section);
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
            showSnackBar(Str_Msg,0);

        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onSectionListReceived()
    {
        TAG = "onSectionListReceived";
        Log.d(MODULE, TAG);
        try
        {
            getSectionList();
            showSectionList();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onSectionListReceivedError(String Str_Msg)
    {
        TAG = "onSectionListReceivedError";
        Log.d(MODULE, TAG);
        try
        {
            getSectionList();
            showSectionList();
            showSnackBar(Str_Msg, 1);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void onSectionListItemClicked(int position)
    {
        TAG = "onSectionListItemClicked";
        Log.d(MODULE, TAG);
        try
        {
            got_UpdateAssignedSectionFragment(position);
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onSectionListDeleteBtnClicked(int position)
    {
        TAG = "onSectionListDeleteBtnClicked";
        Log.d(MODULE, TAG);
        try
        {
            Str_SectionId = mListSection.get(position).getID();
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(getString(R.string.lbl_msg_delete_section))
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
    public void onSectionAssigned(String Str_Msg)
    {
        TAG = "onSectionUpdated";
        Log.d(MODULE, TAG);
        try
        {
            AppUtils.hideProgressDialog();
            AppUtils.DialogMessage(mActivity, Str_Msg);
            new SectionList_Process(this,PayloadSection()).GetSectionList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onSectionAssignedError(String Str_Msg)
    {
        TAG = "onSectionUpdatedError";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        AppUtils.DialogMessage(mActivity, Str_Msg);
        showSnackBar(Str_Msg, 2);
    }

    AdapterView.OnItemSelectedListener _OnClassItemSelectedListener =  new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
        {
            TAG = "onItemSelected";
            Log.d(MODULE, TAG);
            try
            {
                if (position > 0)
                {
                    Str_ClassId = mListClass.get(position - 1).getID();
                    getSectionListFromService();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView)
        {

        }
    };


    public void getSectionListFromService()
    {
        TAG = "getSectionListFromService";
        Log.d(MODULE, TAG);
        try
        {
            new SectionList_Process(this, PayloadSection()).GetSectionList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
                responseCommon = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>(){}.getType());
                mListClass = responseCommon.getCclass();
                Log.d(MODULE, TAG + " mListClass : " + mListClass.size());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void getSectionList()
    {
        TAG = "getSectionList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_SECTION_LIST, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if (Str_Json.length() > 0)
            {
                responseCommon = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>() {}.getType());
                mListSection = responseCommon.getCclass();
                Log.d(MODULE, TAG + " mListSection : " + mListSection.size());
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
            String[] items;
            if(mListClass.size()>0)
            {
                items = AppUtils.getArray(mListClass,getString(R.string.lbl_select_class));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_class.setAdapter(adapter);
                spinner_class.setSelection(mClassListPosition);
            }
            else
            {
                items = new String[1];
                items[0] = getString(R.string.lbl_select_class);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_class.setAdapter(adapter);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showSectionList()
    {
        TAG = "showSectionList";
        Log.d(MODULE, TAG);
        try
        {
            AssignedSectionAdapter adapter = new AssignedSectionAdapter(mListSection,this);
            recycler_view.setAdapter(adapter);
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
            View emptyView  = mActivity.getLayoutInflater().inflate(R.layout.view_list_empty, null);
            LinearLayout.LayoutParams params = AppUtils.getMatchParentParams();
            emptyView.setLayoutParams(params);
            TextView textView = (TextView) emptyView.findViewById(R.id.text_view_empty);
            textView.setText(R.string.lbl_no_section_list);
            textView.setTypeface(font.getHelveticaRegular());
            ((ViewGroup)recycler_view.getParent().getParent()).addView(emptyView);
            recycler_view.setEmptyView(emptyView);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

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

    View.OnClickListener _OnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.fab:
                     goto_AssignSectionFragment();
                     break;
            }
        }
    };

    public void goto_AssignSectionFragment()
    {
        TAG = "goto_AssignSectionFragment";
        Log.d(MODULE, TAG);
        try
        {
            mSavedInstanceState=getSavedState();
            Fragment _fragment = new Fragment_Assign_Section();
            FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, _fragment,AppUtils.FRAGMENT_ASSIGN_SECTION);
            fragmentTransaction.addToBackStack(AppUtils.FRAGMENT_ASSIGN_SECTION + "");
            fragmentTransaction.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void got_UpdateAssignedSectionFragment(int position)
    {
        TAG = "got_UpdateAssignedSectionFragment";
        Log.d(MODULE, TAG);

        if(mListSection.size()>0)
        {
            mSavedInstanceState=getSavedState();
            mSection = mListSection.get(position);
            Log.d(MODULE, TAG + "values of list " + mSection.getName());
            Log.d(MODULE, TAG + "getSectionId of list " + mSection.getID());

            Bundle  mBundle = new Bundle();

            mBundle.putParcelable(AppUtils.B_SINGLE_SECTION, mSection);
            mBundle.putInt(AppUtils.B_MODE,AppUtils.MODE_UPDATE);

            Fragment mFragment = new Fragment_Assign_Section();
            FragmentManager mManager = mActivity.getSupportFragmentManager();
            FragmentTransaction mTransaction = mManager.beginTransaction();
            mFragment.setArguments(mBundle);
            mTransaction.replace(R.id.container_body, mFragment,AppUtils.FRAGMENT_ASSIGN_SECTION);
            mTransaction.addToBackStack(AppUtils.FRAGMENT_ASSIGN_SECTION + "");
            mTransaction.commit();
        }
    }

    public JSONObject Payload_Delete_Section()
    {
        TAG = "Payload_Delete_Section";
        Log.d(MODULE, TAG);

        JSONObject obj=new JSONObject();
        try {

            obj.put("UserId", Str_Id);
            obj.put("ClassId",Str_ClassId);
            obj.put("SectionId",Str_SectionId);
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
            outState.putInt(ARG_CLASS_LIST_POSITION,spinner_class.getSelectedItemPosition());
            Log.d(MODULE, TAG + " ARG_CLASS_LIST_POSITION:" + spinner_class.getSelectedItemPosition());
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
                     new AssignSection(Str_Add_Section_Url,Fragment_Assigned_Section_List.this,Payload_Delete_Section()).assignSection();
                     break;
                case DialogInterface.BUTTON_NEGATIVE:
                     break;
            }
        }
    };

    public void showSnackBar(String Str_Msg, final int mService)
    {
        Snackbar snackbar = Snackbar.make(cl_main, Str_Msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.lbl_retry), new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mService==0)
                    new ClassList_Process(mActivity,this).GetClassList();
                else if (mService==1) getSectionListFromService();
                else if (mService==2)
                {
                    new AssignSection(Str_Add_Section_Url,Fragment_Assigned_Section_List.this,Payload_Delete_Section()).assignSection();
                }
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
