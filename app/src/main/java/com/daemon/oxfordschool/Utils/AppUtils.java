package com.daemon.oxfordschool.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.classes.Common_Class;
import com.daemon.oxfordschool.classes.User;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by daemonsoft on 7/12/15.
 */
public class AppUtils extends Dialog
{

    public static final String MODULE = "AppUtils";
    public static String TAG = "";

    public static String SHARED_LOGIN_PROFILE = "Shared_Login";
    public static String SHARED_STUDENT_LIST = "Shared_Student_List";
    public static String LOGIN_SHARED = "Shared_Login";
    public static String SHARED_SUBJECT_LIST = "Shared_Subject_List";
    public static String SHARED_SECTION_LIST = "Shared_Section_List";
    public static String SHARED_CLASS_LIST = "Shared_Class_List";
    public static String SHARED_EXAM_TYPE_LIST = "Shared_Exam_List";
    public static String SHARED_EXAM_LIST = "Shared_Exam_List";
    public static String SHARED_EXAM_RESULT = "Shared_Exam_Result";
    public static String SHARED_EVENTS_LIST = "Shared_Events_List";
    public static String SHARED_HOMEWORK_LIST = "Shared_HomeWork_List";
    public static String SHARED_ATTENTANCE = "Shared_Attendance";
    public static String SHARED_TIMETABLE = "Shared_TimeTable";
    public static final String SHARED_PREFS = "MY_PREFERENCES";

    public static int MODE_UPDATE=1;
    public static int MODE_ADD=0;

    public static String B_MODE="Bundle_Mode";
    public static String B_USER_ID="Bundle_UserId";
    public static String B_CLASS_ID="Bundle_ClassId";
    public static String B_SECTION_ID="Bundle_SectionId";
    public static String B_ATTENDANCE_LIST="Bundle_List";
    public static String B_DATE="Bundle_Date";


    public static int SHARED_INT_DIALOG_PICKER = 1400;
    public static String SHARED_DIALOG_PICKER = "Shared_Dialog_Picker";

    public static final String FRAGMENT_ADD_ATTENDANCE = "500";

    public static File root = android.os.Environment.getExternalStorageDirectory();
    public static String RootPath = "/Android/data/com.daemon.oxfordschool";

    private static ProgressDialog mPrograssDialog;

    // SD card image directory
    public static final String PHOTO_ALBUM = AppUtils.root.getAbsolutePath() + AppUtils.RootPath;

    public static void DialogMessage(AppCompatActivity mActivity,String Str_Msg)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
        builder1.setMessage(Str_Msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton(R.string.lbl_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static Object fromJson(String jsonString, Type type)
    {
        return new Gson().fromJson(jsonString, type);
    }

    public static DisplayImageOptions getOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(0)).cacheInMemory(true)
                .cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.ic_profile)
                .showImageForEmptyUri(R.drawable.ic_profile)
                .showImageOnFail(R.drawable.ic_profile)
                .resetViewBeforeLoading(true).considerExifParams(true).build();
        return options;

    }

    public static boolean IsFileExist(File dir,File file)
    {
        TAG = "IsFileExist";
        Log.d(MODULE, TAG);

        boolean RetValue=false;
        File[] Files_Array = dir.listFiles();
        for(int i=0;i<Files_Array.length;i++)
        {
            if(file.equals(Files_Array[i]))
            {
                RetValue=true;
            }
        }
        return RetValue;
    }

    public static void saveImage(Bitmap photo, AppCompatActivity mActivity,String Str_Name)
    {
        File sdIconStorageDir = new File(getProfilePicturePath(mActivity));
        sdIconStorageDir.mkdirs();
        try
        {
            String filePath = sdIconStorageDir.toString() + "/" + Str_Name + ".png";
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Log.d("TAG", "Error saving image file: " + e.getMessage());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.d("TAG", "Error saving image file: " + e.getMessage());
        }
    }

    public static String getProfilePicturePath(Context context) {
        String profilePicturePath = AppUtils.PHOTO_ALBUM;
        return profilePicturePath;
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int newWidth, int newHeight)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public AppUtils(Context context)
    {
        super(context);
    }

    public static void showProgressDialog(Context context)
    {
        mPrograssDialog = new ProgressDialog(context);
        mPrograssDialog.setProgressStyle(mPrograssDialog.THEME_DEVICE_DEFAULT_LIGHT);
        mPrograssDialog.setMessage(context.getString(R.string.lbl_loading));
        mPrograssDialog.setCancelable(true);
        if(!mPrograssDialog.isShowing())
        {
            mPrograssDialog.show();
        }
    }

    public static void hideProgressDialog()
    {
        mPrograssDialog.hide();
    }

    public static LinearLayout.LayoutParams getMatchParentParams()
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        return params;
    }

    public static LinearLayout.LayoutParams getMatchWrapParams()
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        return params;
    }

    public static String[] getArray(ArrayList<Common_Class> list,String Str)
    {
        String[] array = new String[list.size()+1];
        array[0] = Str;
        try
        {
            for(int i=0;i<list.size();i++)
            {
                array[i+1] = list.get(i).getName();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return array;
    }

    public static String[] getArray(ArrayList<Common_Class> list)
    {
        String[] array = new String[list.size()];
        try
        {
            for(int i=0;i<list.size();i++)
            {
                array[i] = list.get(i).getName();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return array;
    }

    public static String[] getArray(String[] items,String Str)
    {
        String[] array = new String[items.length+1];
        array[0] = Str;
        try
        {
            for(int i=0;i<items.length;i++)
            {
                array[i+1] = items[i];
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return array;
    }

    public static String[] getStudentArray(ArrayList<User> list,String Str)
    {
        String[] array = new String[list.size()+1];
        array[0] = Str;
        try
        {
            for(int i=0;i<list.size();i++)
            {
                User mUSer = list.get(i);
                StringBuilder Str_Name = new StringBuilder();
                Str_Name.append(mUSer.getFirstName()).append(" ").append(mUSer.getLastName());
                array[i+1] = Str_Name.toString();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return array;
    }

    public static void showDialog(AppCompatActivity mActivity,String Str_Msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(Str_Msg)
                .setCancelable(false)
                .setPositiveButton(mActivity.getString(R.string.lbl_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
