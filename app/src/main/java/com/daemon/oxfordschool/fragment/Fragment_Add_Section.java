package com.daemon.oxfordschool.fragment;

/**
 * Created by Vikram on 20/04/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.asyncprocess.AllSectionList_Process;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.UpdateSection;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.AddSectionListener;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.response.CommonList_Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_Add_Section extends Fragment implements AddSectionListener, ClassListListener,SectionListListener
{

    public static String MODULE = "Fragment_Add_Section ";
    public static String TAG = "";

    TextView tv_lbl_class,tv_lbl_section;
    SharedPreferences mPreferences;
    Toolbar mToolbar;
    User mUser;
    Common_Class mSection;
    Button btn_add_section;
    RelativeLayout layout_empty;
    Spinner spinner_class,spinner_section;
    CommonList_Response responseCommon;

    ArrayList<Common_Class> mListClass =new ArrayList<Common_Class>();
    ArrayList<Common_Class> mListSection =new ArrayList<Common_Class>();

    AppCompatActivity mActivity;
    String Str_Id="",Str_ClassId="",Str_SectionId="";
    private Font font= MyApplication.getInstance().getFontInstance();
    Integer mMode=0;
    String Str_Add_Section_Url = ApiConstants.ADD_SECTION_URL;
    Bundle mBundle,mSavedInstanceState;
    FragmentManager mManager;
    int mClassListPosition=0,mSectionListPosition=0;

    final static String ARG_CLASS_LIST_POSITION = "Class_List_Position";
    final static String ARG_SECTION_LIST_POSITION = "Section_List_Position";

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
            mSavedInstanceState=savedInstanceState;
            mManager = mActivity.getSupportFragmentManager();
            mBundle = this.getArguments();
            getProfile();
            getClassList();
            getSectionListFromService();
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
            tv_lbl_class = (TextView) view.findViewById(R.id.tv_lbl_class);
            tv_lbl_section = (TextView) view.findViewById(R.id.tv_lbl_section);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            spinner_class = (Spinner) view.findViewById(R.id.spinner_class);
            spinner_section = (Spinner) view.findViewById(R.id.spinner_section);
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
            if(mSavedInstanceState!=null)
            {
                mClassListPosition=mSavedInstanceState.getInt(ARG_CLASS_LIST_POSITION);
                mSectionListPosition=mSavedInstanceState.getInt(ARG_SECTION_LIST_POSITION);
                Log.d(MODULE,TAG + "mClassListPosition" + mClassListPosition);
                Log.d(MODULE,TAG + "mSectionListPosition" + mSectionListPosition);
            }

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
            tv_lbl_class.setTypeface(font.getHelveticaRegular());
            tv_lbl_section.setTypeface(font.getHelveticaRegular());
            btn_add_section.setTypeface(font.getHelveticaRegular());

            btn_add_section.setOnClickListener(_OnClickListener);

            if(!Str_ClassId.equals(""))
            {
                Log.d(MODULE, TAG + "bundle available");
                btn_add_section.setText(getString(R.string.lbl_update));
            }
            else
            {
                btn_add_section.setText(getString(R.string.lbl_add));
            }
            spinner_class.setOnItemSelectedListener(_OnClassItemSelectedListener);
            spinner_section.setOnItemSelectedListener(_OnSectionItemSelectedListener);
            showClassList();
            showSectionList();
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
                mUser = (User) AppUtils.fromJson(Str_Json, new TypeToken<User>() {}.getType());
                Str_Id = mUser.getID();
                Log.d(MODULE, TAG + " Str_Id : " + Str_Id);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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

    AdapterView.OnItemSelectedListener _OnSectionItemSelectedListener =  new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
        {
            TAG = "_OnSectionItem";
            Log.d(MODULE, TAG);
            try
            {
                if(position>0)
                {
                    Log.d(MODULE, TAG + " Spinner Section : " + position);
                    Str_SectionId=mListSection.get(position-1).getID();
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

    View.OnClickListener _OnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.btn_add_section:
                     addSection();
                     break;
                default:
                     break;
            }
        }
    };


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

    @Override
    public void onClassListReceived()
    {
        TAG = "onClassListReceived";
        Log.d(MODULE, TAG);
        try
        {
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
        AppUtils.showDialog(mActivity, Str_Msg);
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
        AppUtils.showDialog(mActivity, Str_Msg);
    }

    @Override
    public void onSectionUpdated(String Str_Msg)
    {
        AppUtils.hideProgressDialog();
        AppUtils.showDialog(mActivity, Str_Msg);
        goto_SectionListFragment();
    }

    @Override
    public void onSectionUpdatedError(String Str_Msg)
    {
        AppUtils.hideProgressDialog();
        AppUtils.showDialog(mActivity, Str_Msg);
    }

    public void getClassList()
    {
        TAG = "getClassList";
        Log.d(MODULE, TAG);
        try
        {
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            String Str_Json = mPreferences.getString(AppUtils.SHARED_CLASS_LIST, "");
            Log.d(MODULE, TAG + " Str_Json : " + Str_Json);
            if (Str_Json.length() > 0)
            {
                responseCommon = (CommonList_Response) AppUtils.fromJson(Str_Json, new TypeToken<CommonList_Response>() {}.getType());
                mListClass = responseCommon.getCclass();
                Log.d(MODULE, TAG + " mListClass : " + mListClass.size());
            }
            else
            {
                new ClassList_Process(mActivity, this).GetClassList();
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
            String[] items=null;

            String Str = getString(R.string.lbl_select_class);
            Log.d(MODULE, TAG + " Str:" + Str);
            if(mListClass.size()>0)
            {
                items = AppUtils.getArray(mListClass,Str);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_class.setAdapter(adapter);
                spinner_class.setSelection(mClassListPosition);
            }
            else
            {
                items = new String[1];
                items[0] = Str;
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
            String[] items=null;

            if(mListSection.size()>0)
            {
                items = AppUtils.getArray(mListSection,getString(R.string.lbl_select_section));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_section.setAdapter(adapter);
                spinner_section.setSelection(mSectionListPosition);
            }
            else
            {
                items = new String[1];
                items[0] = getString(R.string.lbl_select_section);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_section.setAdapter(adapter);
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    public void addSection()
    {
        TAG = "addSection";
        Log.d(MODULE, TAG);
        AppUtils.showProgressDialog(mActivity);
        new UpdateSection(Str_Add_Section_Url,this,PayloadAddSection()).updateSection();
    }


    public JSONObject PayloadSection()
    {
        TAG = "Payload";
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

    public JSONObject PayloadAddSection()
    {
        TAG = "PayloadAddSection";
        Log.d(MODULE, TAG);

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("UserId", Str_Id);
            obj.put("ClassId", Str_ClassId);
            obj.put("SectionId", Str_SectionId);
            obj.put("Mode", mMode.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.d(MODULE, TAG + " obj : " + obj.toString());

        return obj;
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
        Str_SectionId = mSection.getID();
        Str_Id = mUser.getID();
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
            outState.putInt(ARG_SECTION_LIST_POSITION,spinner_section.getSelectedItemPosition());
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
                 FragmentDrawer.mDrawerLayout.closeDrawer(GravityCompat.START);
                 mManager.popBackStack();
                 return true;
            default:
                 break;

        }
        return super.onOptionsItemSelected(item);
    }


}
