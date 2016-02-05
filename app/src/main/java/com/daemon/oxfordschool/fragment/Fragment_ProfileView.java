package com.daemon.oxfordschool.fragment;

/**
 * Created by Ravi on 29/07/15.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.activity.DialogGallery;
import com.daemon.oxfordschool.asyncprocess.ImageSaving;
import com.daemon.oxfordschool.classes.Action;
import com.daemon.oxfordschool.classes.User;
import com.daemon.oxfordschool.listeners.ImagePickListener;
import com.daemon.oxfordschool.listeners.ImageSavingListener;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;

public class Fragment_ProfileView extends Fragment implements ImagePickListener,ImageSavingListener
{

    public static String MODULE = "Fragment_ProfileView ";
    public static String TAG = "";

    TextView tv_profile_mobile_number,tv_profile_email,tv_lbl_profile_address,tv_profile_address,tv_header_name;
    public ImageView image_view_profile;
    RelativeLayout profile_header;
    SharedPreferences mPreferences;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    User mUser;
    private Uri mImageCaptureUri;
    FragmentManager mManager;
    AppCompatActivity mActivity;
    ImagePickListener mCallBack;
    String Str_Id="";
    private Font font= MyApplication.getInstance().getFontInstance();

    public Fragment_ProfileView()
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
            mManager = mActivity.getSupportFragmentManager();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_profileview, container, false);
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
            tv_header_name = (TextView) view.findViewById(R.id.tv_header_name);
            tv_profile_mobile_number=(TextView) view.findViewById(R.id.tv_profile_mobile_number);
            tv_profile_email=(TextView) view.findViewById(R.id.tv_profile_email);
            tv_lbl_profile_address=(TextView) view.findViewById(R.id.tv_lbl_profile_address);
            tv_profile_address=(TextView) view.findViewById(R.id.tv_profile_address);
            image_view_profile = (ImageView) view.findViewById(R.id.iv_profile);

            profile_header = (RelativeLayout) view.findViewById(R.id.view_profile_header);

            profile_header.setOnClickListener(_onClickListener);
            initImageLoader();
            SetProfileImage();
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
            getProfile();
            setProfile();
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
            tv_header_name.setTypeface(font.getHelveticaRegular());
            tv_profile_mobile_number.setTypeface(font.getHelveticaRegular());
            tv_profile_email.setTypeface(font.getHelveticaRegular());
            tv_lbl_profile_address.setTypeface(font.getHelveticaRegular());
            tv_profile_address.setTypeface(font.getHelveticaRegular());
        }
        catch (Exception ex)
        {

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

    public void setProfile()
    {
        TAG = "setProfile";
        Log.d(MODULE, TAG);

        StringBuilder Str_Name = new StringBuilder();
        Str_Name.append(mUser.getFirstName()).append(" ");
        Str_Name.append(mUser.getLastName()).append(" ");
        tv_header_name.setText(Str_Name.toString());
        tv_profile_mobile_number.setText(mUser.getMobile_Number());
        tv_profile_email.setText(mUser.getEmail());
        StringBuilder Str_Address = new StringBuilder();
        Str_Address.append(mUser.getAddress1()).append(" ");
        Str_Address.append(mUser.getAddress2()).append(" ");
        Str_Address.append(mUser.getAddress3()).append(" ");
        tv_profile_address.setText(Str_Address);
    }

    View.OnClickListener _onClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.view_profile_header:
                    ShowSelectPhotoOption();
                    break;
            }
        }
    };

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

    private void dispatchTakePictureIntent()
    {
        TAG = "dispatchTakePictureIntent";
        Log.d(MODULE, TAG);

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp.jpg"));
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,mImageCaptureUri);
        captureIntent.putExtra("return-data", true);
        startActivityForResult(captureIntent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        TAG = "onActivityResult";
        Log.d(MODULE, TAG);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2)
        {
            if (data != null)
            {
                Bundle extras2 = data.getExtras();
                if (extras2 != null)
                {
                    Bitmap photo = extras2.getParcelable("data");
                    AppUtils.saveImage(photo, mActivity);
                    SetProfileImage();
                }
            }
        }
        if (requestCode == 101)
        {
            cropImage();
        }
        if (requestCode == 102)
        {
            try
            {
                if(data!=null)
                {
                    if(data.getExtras()!=null)
                    {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        AppUtils.saveImage(photo, mActivity);
                        image_view_profile.setImageBitmap(photo);
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private void cropImage()
    {
        try
        {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(mImageCaptureUri, "image/*");
            intent.putExtra("outputX", 600);
            intent.putExtra("outputY", 600);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, 102);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void StartPicking()
    {
        TAG = "StartPicking";
        Log.d(MODULE, TAG);

        try
        {
            mManager = mActivity.getSupportFragmentManager();

            Bundle Args = new Bundle();
            Args.putString("B_ACTION", Action.ACTION_PICK);

            DialogGallery fragment = new DialogGallery();
            fragment.setArguments(Args);
            fragment.setTargetFragment(fragment, AppUtils.SHARED_INT_DIALOG_PICKER);
            fragment.SetImagePickListener(Fragment_ProfileView.this);
            FragmentTransaction ObjTransaction = mManager.beginTransaction();
            ObjTransaction.add(android.R.id.content,fragment,AppUtils.SHARED_DIALOG_PICKER+"");
            ObjTransaction.addToBackStack(null);
            ObjTransaction.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    private void initImageLoader()
    {
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

    public void SetProfileImage()
    {
        try
        {
            String Str_ImagePath ="file://" + AppUtils.getProfilePicturePath(mActivity) + "/profile.png";
            MemoryCacheUtils.removeFromCache(Str_ImagePath, ImageLoader.getInstance().getMemoryCache());
            DiskCacheUtils.removeFromCache(Str_ImagePath, ImageLoader.getInstance().getDiskCache());
            imageLoader.displayImage(Str_ImagePath, image_view_profile, options);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void onSingleImagePicked(String Str_Path)
    {
        TAG = "onSingleImagePicked";
        Log.d(MODULE, TAG);

        Log.d(MODULE,TAG + " Single Path : " + Str_Path);
        try
        {
            String Str_ImagePath = "file://" + Str_Path;
            new ImageSaving(mActivity,this,Str_ImagePath).execute();
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

    @Override
    public void onImageSaved() {
        SetProfileImage();
    }

    @Override
    public void onImageSavedError() {
        AppUtils.DialogMessage(mActivity,"Cannot Save");
    }

}
