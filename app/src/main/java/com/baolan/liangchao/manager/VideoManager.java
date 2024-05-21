package com.baolan.liangchao.manager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.baolan.liangchao.util.ImageUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class VideoManager {
    private static final String TAG = VideoManager.class.getSimpleName();

    private Context mContext=null;

    String cameraId;

    private CameraDevice mCameraDevice=null;

    private ImageReader imageReader;

    private String[] cameraIdList;

    CodeManager codeManager=null;

    private SurfaceHolder surfaceHolder;


    private CameraManager  cameraManager=null;

    private CameraManager.AvailabilityCallback availabilityCallback;

    public Handler cameraHandler;

    public VideoManager(Context mContext,CameraManager  cameraManager, Handler cameraHandler){
        this.mContext=mContext;
        this.cameraManager=cameraManager;
        this.cameraHandler=cameraHandler;
    }


    @SuppressLint("MissingPermission")
    public void openCamera(String cameraId) {
        this.cameraId=cameraId;

        try {
            cameraIdList = cameraManager.getCameraIdList();
            //05-16 19:15:34.298  4367  4367 I bl_camera: cameraIdList==>[0, 1, 2, 3]
            Log.i(TAG,"cameraIdList==>" + Arrays.toString(cameraIdList));



            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);

            for (Size size:sizes) {
                Log.i(TAG, "width  " + size.getWidth());
                Log.i(TAG, "height  " + size.getHeight());
            }

            cameraManager.registerAvailabilityCallback(availabilityCallback = new CameraManager.AvailabilityCallback() {
                @Override
                public void onCameraAvailable(@NonNull String cameraId) {
                    super.onCameraAvailable(cameraId);
                    Log.i(TAG,"onCameraAvailable>>cameraId>>"+cameraId);
                }

                @Override
                public void onCameraUnavailable(@NonNull String cameraId) {
                    super.onCameraUnavailable(cameraId);
                    Log.i(TAG,"onCameraUnavailable>>cameraId>>"+cameraId);
                }

                /*
                @Override
                public void onCameraAccessPrioritiesChanged() {
                    super.onCameraAccessPrioritiesChanged();
                    LogUtils.i(TAG,"onCameraAccessPrioritiesChanged>>");
                }

                @Override
                public void onPhysicalCameraAvailable(@NonNull String cameraId, @NonNull String physicalCameraId) {
                    super.onPhysicalCameraAvailable(cameraId, physicalCameraId);
                    LogUtils.i(TAG,"onPhysicalCameraAvailable>>cameraId>>"+cameraId);
                }

                @Override
                public void onPhysicalCameraUnavailable(@NonNull String cameraId, @NonNull String physicalCameraId) {
                    super.onPhysicalCameraUnavailable(cameraId, physicalCameraId);
                    LogUtils.i(TAG,"onPhysicalCameraUnavailable>>cameraId>>"+cameraId);
                }
                */
            },cameraHandler);



            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraDevice = camera;
                    startCamera();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                }
            }, new Handler(Looper.getMainLooper()));

        } catch (Exception e) {
        }
    }


    public void startCamera() {
        imageReader  = ImageReader.newInstance(1920, 1080, ImageFormat.YUV_420_888, 3);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireLatestImage();

                if(image != null){
                    dealImageReader(image);
                    image.close();
                }
            }
        }, cameraHandler);

    }


    public void previewCamera(SurfaceHolder surfaceHolder) {
        try {
            CaptureRequest.Builder  captureRequest = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequest.addTarget(imageReader.getSurface());
            //captureRequest.addTarget(surfaceHolder.getSurface());

            this.surfaceHolder = surfaceHolder;
            List<Surface> surfaceList = new ArrayList<>();
            surfaceList.add(imageReader.getSurface());
            surfaceList.add(surfaceHolder.getSurface());

            mCameraDevice.createCaptureSession(surfaceList, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        session.setRepeatingRequest(captureRequest.build(), null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.i(TAG,"onConfigured==>相机预览会话配置失败");
                }
            }, cameraHandler);
        } catch (Exception e) {
            Log.i(TAG,"Exception  "+e.toString());
        }
    }


    private void dealImageReader(Image image) {
        Log.i(TAG,"image width:"+ image.getWidth()+"  height:"+image.getHeight() + "image cap ");

        if(codeManager==null) {
            codeManager = new CodeManager(mContext, Integer.parseInt(cameraId));
        }
        if(surfaceHolder != null){
            surfaceHolder.getSurface().lockHardwareCanvas();

        }
        Log.i(TAG,"Camera "+cameraId+" start convert "+ SystemClock.currentThreadTimeMillis());
        byte[] data21 = ImageUtils.YUV_420_888toNV12(image);
        Log.i(TAG,"Camera "+cameraId+" stop convert "+ SystemClock.currentThreadTimeMillis());
        codeManager.setData(data21);
    }

    public static String format(Date date, String formatStr){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(formatStr);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String format = simpleDateFormat.format(date);
        return format;
    }

    public static String format(Date date){
        return format(date,"yyyy-MM-dd HH:mm:ss");
    }

    public void closeCamera() {
        codeManager.close();
        codeManager=null;

        cameraManager.unregisterAvailabilityCallback(availabilityCallback);
        mCameraDevice.close();
    }

}