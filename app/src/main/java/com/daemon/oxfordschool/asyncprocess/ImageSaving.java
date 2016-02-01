package com.daemon.oxfordschool.asyncprocess;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.AppUtils;
import com.daemon.oxfordschool.listeners.ImageSavingListener;

/**
 * Created by daemonsoft on 23/12/15.
 */
public class ImageSaving extends AsyncTask<Void, Void, Integer>
{

    ProgressDialog dialog = null;
    AppCompatActivity mActivity;
    Fragment mFragment;
    ImageSavingListener mCallBack;
    String Str_ImagePath;

    public ImageSaving(AppCompatActivity mActivity,Fragment mFragment,String Str_ImagePath)
    {
        this.mActivity = mActivity;
        this.mFragment = mFragment;
        this.Str_ImagePath = Str_ImagePath;
        mCallBack = (ImageSavingListener)mFragment;
    }

    @Override
    protected void onPreExecute()
    {
        String Str_Msg = mActivity.getResources().getString(R.string.lbl_progress_msg);
        dialog = ProgressDialog.show(mActivity,"",Str_Msg ,true);
    }

    @Override
    protected Integer doInBackground(Void... arg0)
    {
        int ReturnValue=0;
        try
        {
            Uri imageUri = Uri.parse(Str_ImagePath);
            Bitmap bmp = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), imageUri);
            Bitmap bmpProfile = AppUtils.getScaledBitmap(bmp, 640, 480);
            AppUtils.saveImage(bmpProfile,mActivity);
        }
        catch (Exception ex)
        {
            ReturnValue=1;
            ex.printStackTrace();
        }

        return ReturnValue;
    }

    @Override
    protected void onPostExecute(Integer integer)
    {
        super.onPostExecute(integer);
        if(dialog!=null) dialog.dismiss();
        if(integer==0) mCallBack.onImageSaved();
        else mCallBack.onImageSavedError();
    }

}
