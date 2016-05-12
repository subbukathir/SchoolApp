package com.daemon.oxfordschool.fragment;

/**
 * Created by Vikram on 29/07/15.
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
import com.daemon.oxfordschool.adapter.AssignedSectionAdapter;
import com.daemon.oxfordschool.adapter.SectionAdapter;
import com.daemon.oxfordschool.asyncprocess.AllSectionList_Process;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.asyncprocess.UpdateSection;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.AddSectionListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.Section_List_Item_Click_Listener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_Section_List extends Fragment implements Section_List_Item_Click_Listener,
        SectionListListener,AddSectionListener
{

    public static String MODULE = "Fragment_Master_Section_List";
    public static String TAG = "";

    TextView text_view_empty;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    RelativeLayout layout_empty;

    SharedPreferences mPreferences;
    User mUser;
    Common_Class mSection;
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();
    CommonList_Response responseCommon;
    Toolbar mToolbar;

    AppCompatActivity mActivity;
    Bundle mSavedInstanceState;
    FloatingActionButton fab_add_section;
    String Str_Id="",Str_SectionId="",Str_SectionName;
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_Add_Section_Url = ApiConstants.ADD_SECTION_URL;
    final static String ARG_SECTION_LIST = "Section_List";

    public Fragment_Section_List()
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
            new AllSectionList_Process(this).GetSectionList();
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
        View rootView = inflater.inflate(R.layout.fragment_section_list, container, false);
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
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            fab_add_section = (FloatingActionButton) view.findViewById(R.id.fab_add_section);
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
                mListSection = mSavedInstanceState.getParcelableArrayList(ARG_SECTION_LIST);
                showSectionList();
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
            if(mUser.getUserType().equals(ApiConstants.ADMIN)) fab_add_section.setVisibility(View.VISIBLE);
            fab_add_section.setOnClickListener(_OnClickListener);
            SetActionBar();
            showEmptyView();
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
                mToolbar.setTitle(R.string.lbl_section_managment);
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

    }

    @Override
    public void onSectionListItemClicked(int position)
    {
        TAG = "onSectionListItemClicked";
        Log.d(MODULE, TAG);
        try
        {
            goto_UpdateSectionFragment(position);
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
            Str_SectionName=mListSection.get(position).getName();
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
    public void onSectionUpdated(String Str_Msg)
    {
        TAG = "onSectionUpdated";
        Log.d(MODULE, TAG);
        try
        {
            AppUtils.hideProgressDialog();
            AppUtils.DialogMessage(mActivity, Str_Msg);
            new AllSectionList_Process(this).GetSectionList();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onSectionUpdatedError(String Str_Msg)
    {
        TAG = "onSectionUpdatedError";
        Log.d(MODULE, TAG);
        AppUtils.hideProgressDialog();
        AppUtils.DialogMessage(mActivity, Str_Msg);
    }

    public void getSectionListFromService()
    {
        TAG = "getSectionListFromService";
        Log.d(MODULE, TAG);
        try
        {
            new AllSectionList_Process(this).GetSectionList();
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

    public void showSectionList()
    {
        TAG = "showSectionList";
        Log.d(MODULE, TAG);
        try
        {
            SectionAdapter adapter = new SectionAdapter(mListSection,this);
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

    View.OnClickListener _OnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.fab_add_section:
                     goto_AddSectionFragment();
                     break;
            }
        }
    };

    public void goto_AddSectionFragment()
    {
        TAG = "goto_AddSectionFragment";
        Log.d(MODULE, TAG);
        try
        {
            mSavedInstanceState=getSavedState();
            Fragment _fragment = new Fragment_Add_Section();
            FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, _fragment,AppUtils.FRAGMENT_ADD_SECTION);
            fragmentTransaction.addToBackStack(AppUtils.FRAGMENT_ADD_SECTION + "");
            fragmentTransaction.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void goto_UpdateSectionFragment(int position)
    {
        TAG = "goto_UpdateSectionFragment";
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

            Fragment mFragment = new Fragment_Add_Section();
            FragmentManager mManager = mActivity.getSupportFragmentManager();
            FragmentTransaction mTransaction = mManager.beginTransaction();
            mFragment.setArguments(mBundle);
            mTransaction.replace(R.id.container_body, mFragment,AppUtils.FRAGMENT_ADD_SECTION);
            mTransaction.addToBackStack(AppUtils.FRAGMENT_ADD_SECTION + "");
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
            obj.put("SectionName",Str_SectionName);
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
            outState.putParcelableArrayList(ARG_SECTION_LIST,mListSection);
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

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                     new UpdateSection(Str_Add_Section_Url,Fragment_Section_List.this,Payload_Delete_Section()).updateSection();
                     break;
                case DialogInterface.BUTTON_NEGATIVE:
                     break;
            }
        }
    };
}
