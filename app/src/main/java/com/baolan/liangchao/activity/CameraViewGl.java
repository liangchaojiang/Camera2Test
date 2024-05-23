package com.baolan.liangchao.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;

import com.baolan.liangchao.R;
import com.baolan.liangchao.camera.Camera2Helper;
import com.baolan.liangchao.camera.Camera2Listener;
import com.baolan.liangchao.camera.MyGLSurfaceView;

import java.nio.ByteBuffer;

public class CameraViewGl extends AppCompatActivity {
    private static final String TAG = "CameraViewGl";
    Button btStart,btStop;
    MyGLSurfaceView mPreview2,mPreview21;
    private Camera2Listener listener2,listener21,listener22;
    private Camera2Helper camera2,camera21,camera22;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view_gl);
        btStart = (Button) findViewById(R.id.btStart);
        btStop = (Button) findViewById(R.id.btStop);
        mPreview2 = (MyGLSurfaceView) findViewById(R.id.glPanel);
        mPreview21 = (MyGLSurfaceView) findViewById(R.id.glPanel1);
        mPreview2.setYuvDataSize(1920,1080);
        mPreview21.setYuvDataSize(1920,1080);
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String [] cameraList = cameraManager.getCameraIdList();
            for (String camera:cameraList
                 ) {
                Log.i(TAG,"camera "+camera);
            }
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
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
                mPreview2.feedData(rawData,1);

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

        listener21 = new Camera2Listener() {
            @Override
            public void onCameraOpened(CameraDevice camera, String cameraId, int width, int height) {
                Log.i(TAG,"onCameraOpened21");
            }
            private int pCBCount = 0;
            private long startTimeNs, endTimeNs = 0;
            @Override
            public void onPreview(byte[] rawData, int width, int height, boolean isRgb32, CameraDevice camera) {
                Log.i(TAG,"onPreview21");
                endTimeNs = System.nanoTime();
                pCBCount++;
                if ((endTimeNs - startTimeNs) > 1000000000) {
                    startTimeNs = endTimeNs;
                    Log.d(TAG, "cameraID =  " + camera.getId() + " onImageAvailable fps = " + pCBCount);
                    pCBCount = 0;
                }
                mPreview21.feedData(rawData,1);
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG,"onCameraClosed21");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG,"onCameraError21");
            }
        };

        listener22 = new Camera2Listener() {
            @Override
            public void onCameraOpened(CameraDevice camera, String cameraId, int width, int height) {
                Log.i(TAG,"onCameraOpened22");
            }
            private int pCBCount = 0;
            private long startTimeNs, endTimeNs = 0;
            @Override
            public void onPreview(byte[] rawData, int width, int height, boolean isRgb32, CameraDevice camera) {
                Log.i(TAG,"onPreview22");
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
                Log.i(TAG,"onCameraClosed22");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG,"onCameraError22");
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
                camera21 = new Camera2Helper.Builder()
                        .rgb32Format(false)
                        .specificCameraId(String.valueOf(1))
                        .context(CameraViewGl.this.getBaseContext())
                        .maxPreviewSize(new Size(1920, 1080))
                        .previewSize(new Size(1920, 1080))
                        .cameraListener(listener21)
                        .build();
                camera21.start();
                camera22 = new Camera2Helper.Builder()
                        .rgb32Format(false)
                        .specificCameraId(String.valueOf(2))
                        .context(CameraViewGl.this.getBaseContext())
                        .maxPreviewSize(new Size(1920, 1080))
                        .previewSize(new Size(1920, 1080))
                        .cameraListener(listener22)
                        .build();
                camera22.start();
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera2.stop();
                camera21.stop();
                camera22.stop();
            }
        });


    }
}