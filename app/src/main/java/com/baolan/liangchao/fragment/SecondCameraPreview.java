package com.baolan.liangchao.fragment;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.baolan.liangchao.R;
import com.baolan.liangchao.activity.MainActivity;
import com.baolan.liangchao.manager.VideoManager;

/*
   相机1预览场景
*/
public class SecondCameraPreview extends Fragment  {
	private String TAG= SecondCameraPreview.class.getSimpleName();
	private MainActivity mMainActivity;

	private SurfaceHolder surfaceHolder;

	private String cameraId="1";


	VideoManager mVideoManager=null;

	public Handler FirstSceneMenuHandler=new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			}
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mMainActivity=(MainActivity)activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.secondcamerapreview,container,false);

		SurfaceView surfaceView = mView.findViewById(R.id.secondsurfaceview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Log.i(TAG,"surfaceCreated");

			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				Log.i(TAG,"surfaceChanged   format  "+format+"  width  "+width+"  height  "+height);
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.i(TAG,"surfaceDestroyed");
			}
		});

		mVideoManager=new VideoManager(mMainActivity,mMainActivity.mCameraManager,mMainActivity.cameraHandler);
		mVideoManager.openCamera(cameraId);
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		closeCamera();
	}

	public void SendMessage(int args,String info){
		Message msg = new Message();
		msg.what = args;

		if(info != null) {
			if (!info.equals("")) {
				msg.obj = info;
			}
		}

		FirstSceneMenuHandler.sendMessage(msg);
	}


	public void previewCamera() {
		mVideoManager.previewCamera(surfaceHolder);
	}

	public void closeCamera(){
		mVideoManager.closeCamera();
	}

}  