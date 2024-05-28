package com.baolan.liangchao.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;

import com.baolan.liangchao.R;
import com.baolan.liangchao.camera.Camera2Helper;
import com.baolan.liangchao.camera.Camera2Listener;
import com.baolan.liangchao.camera.MyGLSurfaceView;
import com.baolan.liangchao.manager.CodeManager;

public class CameraViewGl extends AppCompatActivity {
    private static final String TAG = "CameraViewGl";
    Button btStart,btStop;
    MyGLSurfaceView mPreview2,mPreview21,mPreview22,mPreview23;
    private Camera2Listener listener2,listener21,listener22,listener23;
    private Camera2Helper camera2,camera21,camera22,camera23;

    private CodeManager codeManager = null;
    private CodeManager codeManager1 = null;
    private CodeManager codeManager2= null;
    private CodeManager codeManager3= null;
    private Context mContext;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view_gl);
        btStart = (Button) findViewById(R.id.btStart);
        btStop = (Button) findViewById(R.id.btStop);
        mPreview2 = (MyGLSurfaceView) findViewById(R.id.glPanel);
        mPreview21 = (MyGLSurfaceView) findViewById(R.id.glPanel1);
        mPreview22 = (MyGLSurfaceView) findViewById(R.id.glPanel2);
        mPreview23 = (MyGLSurfaceView) findViewById(R.id.glPanel3);
        mPreview2.setYuvDataSize(1920,1080);
        mPreview21.setYuvDataSize(1920,1080);
        mPreview22.setYuvDataSize(1920,1080);
        mPreview23.setYuvDataSize(1920,1080);
        mContext = this;
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
            public void onPreview(Image rawData, int width, int height, boolean isRgb32, CameraDevice camera) {
                Log.i(TAG,"onPreview");
                endTimeNs = System.nanoTime();
                pCBCount++;
                if ((endTimeNs - startTimeNs) > 1000000000) {
                    startTimeNs = endTimeNs;
                    Log.d(TAG, "cameraID =  " + camera.getId() + " onImageAvailable fps = " + pCBCount);
                    pCBCount = 0;
                }
                mPreview2.feedNv12DataDirect(rawData.getPlanes()[0].getBuffer(),rawData.getPlanes()[1].getBuffer());
//                if(codeManager == null){
//                    codeManager = new CodeManager(mContext, Integer.parseInt(camera.getId()));
//                }
//                codeManager.setData(rawData);
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
            public void onPreview(Image rawData, int width, int height, boolean isRgb32, CameraDevice camera) {
                Log.i(TAG,"onPreview21");
                endTimeNs = System.nanoTime();
                pCBCount++;
                if ((endTimeNs - startTimeNs) > 1000000000) {
                    startTimeNs = endTimeNs;
                    Log.d(TAG, "cameraID =  " + camera.getId() + " onImageAvailable fps = " + pCBCount);
                    pCBCount = 0;
                }

                mPreview21.feedNv12DataDirect(rawData.getPlanes()[0].getBuffer(),rawData.getPlanes()[1].getBuffer());

//                if(codeManager1 == null){
//                    codeManager1 = new CodeManager(mContext, Integer.parseInt(camera.getId()));
//                }
//                codeManager1.setData(rawData);
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
            public void onPreview(Image rawData, int width, int height, boolean isRgb32, CameraDevice camera) {
                Log.i(TAG,"onPreview22");
                endTimeNs = System.nanoTime();
                pCBCount++;
                if ((endTimeNs - startTimeNs) > 1000000000) {
                    startTimeNs = endTimeNs;
                    Log.d(TAG, "cameraID =  " + camera.getId() + " onImageAvailable fps = " + pCBCount);
                    pCBCount = 0;
                }
                mPreview22.feedNv12DataDirect(rawData.getPlanes()[0].getBuffer(),rawData.getPlanes()[1].getBuffer());

//                if(codeManager2 == null){
//                    codeManager2 = new CodeManager(mContext, Integer.parseInt(camera.getId()));
//                }
//                codeManager2.setData(rawData);
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


        listener23 = new Camera2Listener() {
            @Override
            public void onCameraOpened(CameraDevice camera, String cameraId, int width, int height) {
                Log.i(TAG,"onCameraOpened23");
            }
            private int pCBCount = 0;
            private long startTimeNs, endTimeNs = 0;
            @Override
            public void onPreview(Image rawData, int width, int height, boolean isRgb32, CameraDevice camera) {
                Log.i(TAG,"onPreview23");
                endTimeNs = System.nanoTime();
                pCBCount++;
                if ((endTimeNs - startTimeNs) > 1000000000) {
                    startTimeNs = endTimeNs;
                    Log.d(TAG, "cameraID =  " + camera.getId() + " onImageAvailable fps = " + pCBCount);
                    pCBCount = 0;
                }
                Log.d(TAG,"liangchao rawData start"+camera.getId());
                mPreview23.feedNv12DataDirect(rawData.getPlanes()[0].getBuffer(),rawData.getPlanes()[1].getBuffer());
                Log.d(TAG,"liangchao rawData stop"+camera.getId());
//                if(codeManager3 == null){
//                    codeManager3 = new CodeManager(mContext, Integer.parseInt(camera.getId()));
//                }
//                codeManager3.setData(rawData);
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG,"onCameraClosed23");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG,"onCameraError23");
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

                camera23 = new Camera2Helper.Builder()
                        .rgb32Format(false)
                        .specificCameraId(String.valueOf(3))
                        .context(CameraViewGl.this.getBaseContext())
                        .maxPreviewSize(new Size(1920, 1080))
                        .previewSize(new Size(1920, 1080))
                        .cameraListener(listener23)
                        .build();
                camera23.start();
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera2.stop();
                camera21.stop();
                camera22.stop();
                camera23.stop();
            }
        });


    }
}