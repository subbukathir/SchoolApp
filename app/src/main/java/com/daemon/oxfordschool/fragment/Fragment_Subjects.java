package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.adapter.SubjectsAdapter;
import com.daemon.oxfordschool.asyncprocess.SubjectList_Process;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.components.RecycleEmptyErrorView;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.Subject_List_Item_Click_Listener;
import com.daemon.oxfordschool.listeners.SubjectListListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class Fragment_Subjects extends Fragment implements SubjectListListener,Subject_List_Item_Click_Listener
{

    public static String MODULE = "Fragment_Subjects ";
    public static String TAG = "";

    SwipeRefreshLayout swipeRefreshLayout;
    RecycleEmptyErrorView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;

    SharedPreferences mPreferences;
    User mUser;
    Common_Class mSubjects;
    ArrayList<Common_Class> mListSubjects =new ArrayList<Common_Class>();
    CommonList_Response response;
    Toolbar mToolbar;

    AppCompatActivity mActivity;
    Bundle mSavedInstanceState;
    FloatingActionButton fab_add_subject;
    String Str_Id="";
    private Font font= MyApplication.getInstance().getFontInstance();
    String Str_Url = ApiConstants.SUBJECTLIST_URL;

    public Fragment_Subjects()
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
            new SubjectList_Process(mActivity,this).GetSubjectList();
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
        View rootView = inflater.inflate(R.layout.fragment_subjects, container, false);
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
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
            recycler_view = (RecycleEmptyErrorView) view.findViewById(R.id.recycler_view_subjects);
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
                getSubjectList();
                showSubjectList();
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

    public void setProperties()
    {
        TAG = "setProperties";
        Log.d(MODULE, TAG);
        try
        {
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
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
                mToolbar.setTitle(R.string.lbl_subjects);
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
    public void onSubjectListReceived()
    {
        TAG = "onSubjectListReceived";
        Log.d(MODULE, TAG);
        getSubjectList();
        showSubjectList();
    }

    @Override
    public void onSubjectListReceivedError(String Str_Msg)
    {
        TAG = "onSubjectListReceivedError";
        Log.d(MODULE, TAG);
    }

    @Override
    public void onSubjectListItemClicked(int position)
    {
        TAG = "onSubjectListItemClicked";
        Log.d(MODULE, TAG + "position " + position);
        if(mUser.getUserType().equals(ApiConstants.ADMIN))gotoFragmentUpdate(position);
    }


    public void getSubjectList()
    {
        TAG = "getSubjectList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_SUBJECT_LIST,"");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if(Str_Json.length()>0)
            {
                response = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>(){}.getType());
                mListSubjects = response.getCclass();
                Log.d(MODULE, TAG + " mListSubjects : " + mListSubjects.size());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void showSubjectList()
    {
        TAG = "showSubjectList";
        Log.d(MODULE, TAG);
        try
        {
            if(mListSubjects.size()>0)
            {
                SubjectsAdapter adapter = new SubjectsAdapter(mListSubjects,this);
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
            textView.setText(R.string.lbl_no_subjects);
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
                    goto_AddSubjectFragment();
                    break;
            }
        }
    };

    public void goto_AddSubjectFragment()
    {
        mSavedInstanceState=getSavedState();
        Fragment _fragment = new Fragment_Add_Subject();
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, _fragment,AppUtils.FRAGMENT_ADD_SUBJECT);
        fragmentTransaction.addToBackStack(AppUtils.FRAGMENT_ADD_SUBJECT + "");
        fragmentTransaction.commit();
    }
    public void gotoFragmentUpdate(int position)
    {
        TAG = "gotoFragmentUpdate";
        Log.d(MODULE, TAG);

        if(mListSubjects.size()>0)
        {
            mSavedInstanceState=getSavedState();
            mSubjects = mListSubjects.get(position);
            Log.d(MODULE, TAG + "values of list " + mSubjects.getName());
            Log.d(MODULE, TAG + "getSectionId of list " + mSubjects.getID());

            Bundle  mBundle = new Bundle();

            mBundle.putParcelable(AppUtils.B_SUBJECTS, mSubjects);
            mBundle.putInt(AppUtils.B_MODE,AppUtils.MODE_UPDATE);

            Fragment mFragment = new Fragment_Add_Subject();
            FragmentManager mManager = mActivity.getSupportFragmentManager();
            FragmentTransaction mTransaction = mManager.beginTransaction();
            mFragment.setArguments(mBundle);
            mTransaction.replace(R.id.container_body, mFragment,AppUtils.FRAGMENT_ADD_SUBJECT);
            mTransaction.addToBackStack(AppUtils.FRAGMENT_ADD_SUBJECT + "");
            mTransaction.commit();
        }
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

}
