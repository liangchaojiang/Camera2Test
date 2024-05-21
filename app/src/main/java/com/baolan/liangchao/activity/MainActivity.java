package com.baolan.liangchao.activity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.baolan.liangchao.R;
import com.baolan.liangchao.fragment.FirstCameraPreview;
import com.baolan.liangchao.fragment.FourthCameraPreview;
import com.baolan.liangchao.fragment.SecondCameraPreview;
import com.baolan.liangchao.fragment.ThirdCameraPreview;

public class MainActivity extends AppCompatActivity {
    public String TAG=MainActivity.class.getSimpleName();

    public FragmentManager mFragmentManager;
    public FragmentTransaction mFragmentTransaction;

    public FirstCameraPreview mFirstCameraPreview=null;

    public SecondCameraPreview mSecondCameraPreview=null;

    public ThirdCameraPreview mThirdCameraPreview=null;

    public FourthCameraPreview mFourthCameraPreview=null;

    private TextView tv_test;

    private TextView close;

    private TextView previewtextview;

    public static final int FIRST=1;

    public static final int SECOND=2;

    public static final int THIRD=3;

    public static final int FOURTH=4;

    public CameraManager mCameraManager=null;


    private HandlerThread handlerThread;


    public Handler cameraHandler;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FIRST:
                    mFirstCameraPreview=new FirstCameraPreview();

                    mFragmentTransaction=mFragmentManager.beginTransaction();

                    mFragmentTransaction.replace(R.id.firstcameralayout,mFirstCameraPreview);

                    mFragmentTransaction.commitAllowingStateLoss();
                    break;
                case SECOND:
                    mSecondCameraPreview=new SecondCameraPreview();

                    mFragmentTransaction=mFragmentManager.beginTransaction();

                    mFragmentTransaction.replace(R.id.secondcameralayout,mSecondCameraPreview);

                    mFragmentTransaction.commitAllowingStateLoss();
                    break;
                case THIRD:
                    mThirdCameraPreview=new ThirdCameraPreview();

                    mFragmentTransaction=mFragmentManager.beginTransaction();

                    mFragmentTransaction.replace(R.id.thirdcameralayout,mThirdCameraPreview);

                    mFragmentTransaction.commitAllowingStateLoss();
                    break;
                case FOURTH:
                    mFourthCameraPreview=new FourthCameraPreview();

                    mFragmentTransaction=mFragmentManager.beginTransaction();

                    mFragmentTransaction.replace(R.id.fourthcameralayout,mFourthCameraPreview);

                    mFragmentTransaction.commitAllowingStateLoss();
                    break;
            }
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window mWindow = MainActivity.this.getWindow();
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mFragmentManager=MainActivity.this.getSupportFragmentManager();

        mCameraManager = (CameraManager)MainActivity.this.getSystemService(Context.CAMERA_SERVICE);

        int checkSelfPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(checkSelfPermission  == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},1);
        }

        tv_test = findViewById(R.id.tv_test);

        tv_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessageDelayed(FIRST,0);

                handler.sendEmptyMessageDelayed(SECOND,1000);

                handler.sendEmptyMessageDelayed(THIRD,2000);

                handler.sendEmptyMessageDelayed(FOURTH,3000);
            }
        });


        close= findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirstCameraPreview.closeCamera();
                mSecondCameraPreview.closeCamera();
                mThirdCameraPreview.closeCamera();
                mFourthCameraPreview.closeCamera();
            }
        });

        previewtextview= findViewById(R.id.previewtextview);

        previewtextview.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   mFirstCameraPreview.previewCamera();
                                                   mSecondCameraPreview.previewCamera();
                                                   mThirdCameraPreview.previewCamera();
                                                   mFourthCameraPreview.previewCamera();
                                               }
                                           });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}