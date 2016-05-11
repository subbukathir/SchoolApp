package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.activity.DialogGallery;
import com.daemon.oxfordschool.activity.MainActivity;
import com.daemon.oxfordschool.adapter.NavigationDrawerAdapter;
import com.daemon.oxfordschool.asyncprocess.ImageSaving;
import com.daemon.oxfordschool.classes.Action;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.constants.ApiConstants;
import com.daemon.oxfordschool.listeners.ImagePickListener;
import com.daemon.oxfordschool.listeners.ImageSavingListener;
import com.daemon.oxfordschool.model.NavDrawerItem;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

public class FragmentDrawer extends Fragment implements ImagePickListener,ImageSavingListener
{

    boolean mTwoPane;
    private RecyclerView recyclerView;
    public ImageView image_view_profile;
    SharedPreferences mPreferences;
    public static ActionBarDrawerToggle mDrawerToggle;
    public static DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private static String[] left_menu_admin = null;
    private static String[] left_menu_staff = null;
    private static String[] left_menu_parent_student = null;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;
    private ImageLoader imageLoader;
    private AppCompatActivity mActivity;
    FragmentManager mManager;
    private DisplayImageOptions options;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static User mUser;
    String Str_Id="";

    public static String MODULE = "FragmentDrawer";
    public static String TAG="";

    public FragmentDrawer()
    {
        TAG="FragmentDrawer";
        Log.d(MODULE,TAG);

    }

    public void setDrawerListener(FragmentDrawerListener listener)
    {
        TAG="setDrawerListener";
        Log.d(MODULE,TAG);

        this.drawerListener = listener;
    }

    public static List<NavDrawerItem> getData()
    {
        TAG="getData";
        Log.d(MODULE,TAG);
        List<NavDrawerItem> data = new ArrayList<>();
        if(mUser.getUserType().equals(ApiConstants.STAFF))
        {
            titles=left_menu_staff;
        }
        else if(mUser.getUserType().equals(ApiConstants.ADMIN))
        {
            titles=left_menu_admin;
        }
        else
        {
            titles=left_menu_parent_student;
        }

        int itemCount = titles.length;
        // preparing navigation drawer items
        for (int i = 0; i <itemCount; i++)
        {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles[i]);
            data.add(navItem);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        TAG="onCreate";
        Log.d(MODULE,TAG);
        // drawer labels
        left_menu_staff= getActivity().getResources().getStringArray(R.array.left_menu_staff);
        left_menu_parent_student= getActivity().getResources().getStringArray(R.array.left_menu_parent_student);
        left_menu_admin= getActivity().getResources().getStringArray(R.array.left_menu_admin);
        mActivity = (AppCompatActivity) getActivity();
        getProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        TAG="onCreateView";
        Log.d(MODULE,TAG);
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        image_view_profile = (ImageView) layout.findViewById(R.id.image_view_profile);

        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                drawerListener.onDrawerItemSelected(view, titles[position]);
                if(!MainActivity.mTwoPane) mDrawerLayout.closeDrawer(containerView);
            }
            @Override
            public void onLongClick(View view, int position)
            {
            }
        }));
        image_view_profile.setOnClickListener(_OnClickListener);
        //initImageLoader();
        //SetProfileImage();
        return layout;
    }

    private void initImageLoader()
    {
        TAG="initImageLoader";
        Log.d(MODULE,TAG);

        try
        {
            imageLoader = ImageLoader.getInstance();
            options = AppUtils.getOptions();
            imageLoader.clearMemoryCache();
        }
        catch (Exception e)
        {

        }
    }

    View.OnClickListener _OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ShowSelectPhotoOption();
        }
    };

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar)
    {
        TAG="setUp";
        Log.d(MODULE,TAG);

        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                super.onDrawerSlide(drawerView, slideOffset);
                //toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static interface ClickListener
    {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
    {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener)
        {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
            {
                @Override
                public boolean onSingleTapUp(MotionEvent e)
                {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e)
                {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null)
                    {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
        {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e))
            {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e)
        {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
        {

        }
    }

    public interface FragmentDrawerListener
    {
        public void onDrawerItemSelected(View view, String Str_Item);
    }

    private void StartPicking()
    {
        TAG = "StartPicking";
        Log.d(MODULE, TAG);

        mManager = mActivity.getSupportFragmentManager();

        Bundle Args = new Bundle();
        Args.putString("B_ACTION", Action.ACTION_PICK);

        DialogGallery fragment = new DialogGallery();
        fragment.setArguments(Args);
        fragment.setTargetFragment(fragment, AppUtils.SHARED_INT_DIALOG_PICKER);
        fragment.SetImagePickListener(FragmentDrawer.this);
        /*Showing dialog within the screen */
        //FragmentTransaction ObjTransaction = mManager.beginTransaction();
        //fragment.show(ObjTransaction,AppConstants.C_DIALOG_BANK_FRAGMENT);
        /*Showing dialog in the separate screen */
        FragmentTransaction ObjTransaction = mManager.beginTransaction();
        ObjTransaction.add(android.R.id.content,fragment,AppUtils.SHARED_DIALOG_PICKER+"");
        ObjTransaction.addToBackStack(null);
        ObjTransaction.commit();
    }

    @Override
    public void onSingleImagePicked(String Str_Path)
    {
        Log.d(MODULE,TAG + " Single Path : " + Str_Path);
        try
        {
            String Str_ImagePath = "file://" + Str_Path;
            new ImageSaving(mActivity,this,Str_ImagePath,"profile").execute();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void onMultipleImagePicked(String[] Str_Path)
    {

    }

    public void SetProfileImage()
    {
        try
        {
            String Str_ImagePath ="file://" + AppUtils.getProfilePicturePath(mActivity) + "/profile.png";
            MemoryCacheUtils.removeFromCache(Str_ImagePath, ImageLoader.getInstance().getMemoryCache());
            DiskCacheUtils.removeFromCache(Str_ImagePath, ImageLoader.getInstance().getDiskCache());
            imageLoader.displayImage(Str_ImagePath, image_view_profile,options);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void onImageSaved()
    {
        SetProfileImage();
    }

    @Override
    public void onImageSavedError() {
        AppUtils.DialogMessage(mActivity,"Cannot Save");
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == mActivity.RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap bmpProfile = AppUtils.getScaledBitmap(imageBitmap, 240, 320);
            AppUtils.saveImage(bmpProfile,mActivity,"profile");
        }
    }

    public void ShowSelectPhotoOption()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.lbl_select_photo)
               .setItems(R.array.array_select_photo, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       switch (which) {
                           case 0:
                               dispatchTakePictureIntent();
                               break;
                           case 1:
                               StartPicking();
                               break;
                           default:
                               break;
                       }
                       dialog.dismiss();
                   }
               });
        builder.show();

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

}
