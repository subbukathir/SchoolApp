package com.daemon.oxfordschool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daemon.oxfordschool.MyApplication;
import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.Utils.Font;
import com.daemon.oxfordschool.listeners.ClassListListener;
import com.daemon.oxfordschool.listeners.SectionListListener;
import com.daemon.oxfordschool.listeners.SubjectListListener;
import com.daemon.oxfordschool.asyncprocess.ClassList_Process;
import com.daemon.oxfordschool.asyncprocess.SectionList_Process;
import com.daemon.oxfordschool.asyncprocess.SubjectList_Process;

/**
 * Created by daemonsoft on 1/2/16.
 */
public class SplashScreen extends AppCompatActivity implements ClassListListener,SectionListListener,SubjectListListener {
    public static String MODULE = "SplashScreen";
    public static String TAG = "";

    private ProgressBar progressBar;
    private TextView tv_loading,tv_welcome;
    int pStatus = 0;
    private Handler handler = new Handler();
    private Font font= MyApplication.getInstance().getFontInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = "onCreate";
        Log.d(MODULE, TAG);
        setContentView(R.layout.splash_screen);

        Init();
        new ClassList_Process(SplashScreen.this,this).GetClassList();
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (pStatus <= 100) {
                    //pStatus +=5;
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            progressBar.setProgress(pStatus);
                            progressBar.setSecondaryProgress(pStatus + 5);
                            tv_loading.setText(pStatus + "/" + progressBar.getMax());
                            if (pStatus == 100) {
                                ShowIntent();
                            }
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pStatus += 2;
                }
            }
        }).start();


    }

    public void Init() {
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        tv_welcome = (TextView) this.findViewById(R.id.tv_welcome);
        tv_loading = (TextView) this.findViewById(R.id.tv_loading);
        tv_welcome.setTypeface(font.getHelveticaBold());
        tv_loading.setTypeface(font.getHelveticaBold());
    }

    @Override
    public void onClassListReceived() {
        TAG = "onClassListReceived";
        Log.d(MODULE, TAG);

        //new SectionList_Process(this,this).GetSectionList();
    }

    @Override
    public void onClassListReceivedError(String Str_Msg) {
        TAG = "onClassListReceivedError";
        Log.d(MODULE, TAG);
    }

    @Override
    public void onSectionListReceived() {
        TAG = "onSectionListReceived";
        Log.d(MODULE, TAG);

        //new SubjectList_Process(this,this).GetSubjectList();
    }

    @Override
    public void onSectionListReceivedError(String Str_Msg) {
        TAG = "onSectionListReceivedError";
        Log.d(MODULE, TAG);
    }

    @Override
    public void onSubjectListReceived() {
        TAG = "onSubjectListReceived";
        Log.d(MODULE, TAG);

    }

    @Override
    public void onSubjectListReceivedError(String Str_Msg) {
        TAG = "onSubjectListReceived";
        Log.d(MODULE, TAG);
    }

    public void ShowIntent() {
        TAG = "ShowIntent";
        Log.d(MODULE, TAG);

        Intent intent = new Intent(this, Activity_Login.class);
        startActivity(intent);
        this.finish();
    }

}