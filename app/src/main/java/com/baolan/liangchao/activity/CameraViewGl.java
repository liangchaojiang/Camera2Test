package com.baolan.liangchao.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;

import com.baolan.liangchao.R;
import com.baolan.liangchao.camera.Camera2Helper;
import com.baolan.liangchao.camera.Camera2Listener;
import com.baolan.liangchao.camera.FormatConvert;
import com.baolan.liangchao.camera.GLPanel;

import java.nio.ByteBuffer;

public class CameraViewGl extends AppCompatActivity {
    private static final String TAG = "CameraViewGl";
    Button btStart,btStop;
    GLPanel mPreview2;
    private Camera2Listener listener2;
    private Camera2Helper camera2;
    private FormatConvert mConvert2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view_gl);
        btStart = (Button) findViewById(R.id.btStart);
        btStop = (Button) findViewById(R.id.btStop);
        mPreview2 = (GLPanel)findViewById(R.id.glPanel);
        mConvert2 = new FormatConvert(this);
        listener2 = new Camera2Listener() {
            @Override
            public void onCameraOpened(CameraDevice camera, String cameraId, int width, int height) {
                Log.i(TAG,"onCameraOpened");
            }
            private int pCBCount = 0;
            private long startTimeNs, endTimeNs = 0;
            @Override
            public void onPreview(byte[] rawData, int width, int height, boolean isRgb32, CameraDevice camera) {
                Log.i(TAG,"onPreview");
                endTimeNs = System.nanoTime();
                pCBCount++;
                if ((endTimeNs - startTimeNs) > 1000000000) {
                    startTimeNs = endTimeNs;
                    Log.d(TAG, "cameraID =  " + camera.getId() + " onImageAvailable fps = " + pCBCount);
                    pCBCount = 0;
                }

            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG,"onCameraClosed");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG,"onCameraError");
            }
        };

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera2 = new Camera2Helper.Builder()
                        .rgb32Format(false)
                        .specificCameraId(String.valueOf(0))
                        .context(CameraViewGl.this.getBaseContext())
                        .maxPreviewSize(new Size(1920, 1080))
                        .previewSize(new Size(1920, 1080))
                        .cameraListener(listener2)
                        .build();
                camera2.start();
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera2.stop();
            }
        });


    }
}