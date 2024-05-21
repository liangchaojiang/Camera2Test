package com.baolan.liangchao.manager;
import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CodeManager {
    private static final String TAG = CodeManager.class.getSimpleName();

    public static int THRESHOLD_FRAME_1 = 10; //map中数据存储的阈值 第一档
    public static int THRESHOLD_FRAME_2 = 20; //map中数据存储的阈值 第二档
    public static final String VCODEC = MediaFormat.MIMETYPE_VIDEO_HEVC; // h265
    private MediaCodec mediaCodec;
    public int vBitrate = 900 * 1024;  // 1000 kbps

    private FileOutputStream outputStream;
    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private MediaCodec.BufferInfo bufferInfo;
    private static final int FRAME_RATE = 15;
    private static final int TIMEOUT_US = 10000;
    ConcurrentMap<Integer,byte[]> byteMap = new ConcurrentHashMap<>(); //用来保存处理视频帧数据的map
    private int videoWidth;
    private int videoHeight;
    private ScheduledExecutorService dealDataPool;

    private boolean isDealing = false;
    private Context context;

    long presentationTimeUs = 0;

    int unUseFrameNum = 0;
    int frame = 0;

    public CodeManager(Context context, int chanelId) {
        init(context,chanelId);
    }

    public void init(Context context, int chanelId) {
        this.context = context;
        videoWidth = 1920;
        videoHeight = 1080;
        isDealing = true;
        dealData(); //开始处理数据线程

        try {
            mediaCodec = MediaCodec.createEncoderByType(VCODEC);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(VCODEC, videoWidth, videoHeight);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, vBitrate);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
            mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, videoWidth*videoHeight*2);
            mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();

            File dir = new File(getVideoBaseDir().getPath()+File.separator+chanelId);
            if(!dir.exists()){
                dir.mkdir();
            }

            SimpleDateFormat ymdhmssSimpleDateFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

            File file= new File(dir.getPath(),ymdhmssSimpleDateFormat.format(new Date())+".h265");

            boolean newFile = file.createNewFile();
            Log.i(TAG,"newFile>>"+newFile);
            outputStream = new FileOutputStream(file);
            inputBuffers = mediaCodec.getInputBuffers();
            outputBuffers = mediaCodec.getOutputBuffers();
            bufferInfo = new MediaCodec.BufferInfo();
        } catch (IOException e) {

        }
    }

    /**
     * 设置数据 并处理 存储阈值时 丢帧的管理
     * @param data
     */
    public void setData(byte[] data){
        unUseFrameNum++;
        Log.i(TAG+"_deal","byteMap.size()>>"+byteMap.size());
        if(byteMap.size() > THRESHOLD_FRAME_2 && unUseFrameNum < 3){  //大于此值时 3帧取一帧
            Log.i(TAG+"_deal","丢弃帧2>unUseFrameNum>>"+unUseFrameNum);
            return;
        }else if(byteMap.size() > THRESHOLD_FRAME_1 && unUseFrameNum < 2){ //大于此值时 2帧取一帧
            Log.i(TAG+"_deal","丢弃帧1>unUseFrameNum>>"+unUseFrameNum);
            return;
        }
        unUseFrameNum = 0; //重新计数
        frame++;
        byteMap.put(frame,data);
    }

    private void dealData(){
        if(dealDataPool != null){
            dealDataPool.shutdown();
        }

        dealDataPool = Executors.newSingleThreadScheduledExecutor();
        dealDataPool.execute(new Runnable() {
            @Override
            public void run() {
                while (isDealing){
//                    LogUtils.i(TAG,"dealDataPool>byteMap.size()>"+byteMap.size());
                    if(byteMap.size()>0){
                        Set<Map.Entry<Integer, byte[]>> entries = byteMap.entrySet();
                        for (Map.Entry<Integer, byte[]> entry:entries){
                            Integer key = entry.getKey();
                            byte[] data = entry.getValue();

                            yuvToH265(data);
                            byteMap.remove(key);
                            data = null;
                        }
                    }
                }
            }
        });
    }


    public void yuvToH265(byte[] yuvData) {
        if(inputBuffers == null){
            return;
        }
        Log.i(TAG,"yuvData>>"+yuvData.length);
        Log.i(TAG,"inputBuffers>>"+inputBuffers);
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
        Log.i(TAG,"inputBufferIndex>>"+inputBufferIndex);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];

            int capacity = inputBuffer.capacity();
            Log.i(TAG,"capacity>>"+capacity);
            if(capacity < yuvData.length){
                return;
            }

            inputBuffer.clear();
            inputBuffer.put(yuvData);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, yuvData.length, presentationTimeUs, 0);
            presentationTimeUs += 1000000 / FRAME_RATE;
        }

        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US);
        if (outputBufferIndex >= 0) {
            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];

            // 获取编码后的视频数据
            byte[] outData = new byte[bufferInfo.size];
            outputBuffer.get(outData);

            // 将编码后的视频数据写入文件
            try {
                outputStream.write(outData);
            } catch (IOException e) {

            }
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);

            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                Log.i(TAG,"ERROR");
            }
        }

    }


    public void close(){
        if(dealDataPool != null){
            dealDataPool.shutdown();
        }

        if(mediaCodec != null){
            mediaCodec.release();
        }

        isDealing = false;
    }

    /**
     * 录像保存地址
     * @return
     */
    public static File getVideoBaseDir(){
        String dir = Environment.getExternalStorageDirectory().getPath()+File.separator+"video";
        File file=new File(dir);
        if(!file.exists()){
            file.mkdirs();
        }
        return file;
    }
}
