package com.baolan.liangchao.codec

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import java.io.BufferedOutputStream

class SurfaceCodec {
    companion object {
        private const val TAG = "SurfaceCodec"
    }

    private lateinit var encoder:MediaCodec
    private lateinit var bufferedOutputStream: BufferedOutputStream
    private lateinit var mCallback:MyMediaCodecCallback
    lateinit var  surface:Surface
    constructor(surfaceHolder: SurfaceHolder,width:Int,height: Int){
        initCodec(surfaceHolder,width, height)
    }
    private fun initCodec(surfaceHolder: SurfaceHolder,width:Int,height:Int){
        val mimeType = MediaFormat.MIMETYPE_VIDEO_HEVC
        encoder = MediaCodec.createEncoderByType(mimeType)
        val format = MediaFormat.createVideoFormat(mimeType,width,height)
        format.setInteger(MediaFormat.KEY_BIT_RATE, 500000)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5)
        encoder.configure(format,null,null,
            MediaCodec.CONFIGURE_FLAG_ENCODE)
        mCallback = MyMediaCodecCallback()
//        encoder.setCallback(mCallback)
        this.surface = encoder.createInputSurface()
        encoder.start()

    }

    fun onFrame(){

        if (encoder == null) {
            Log.e(TAG, "Encode failed encoder is null !!!")
        }
//        var bufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
//        var index = encoder.dequeueOutputBuffer(bufferInfo, 40000)
////        encoder.dequeueInputBuffer(1000)
//        Log.i(TAG, "status $index")
//        if (index >= 0) {
//            encoder.getOutputBuffer(index)
//        }
//        encoder.releaseOutputBuffer(index, false)
    }



    private class MyMediaCodecCallback: MediaCodec.Callback() {
        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {

            Log.i(TAG,"onInputBufferAvailable")
            var bufferInfo:MediaCodec.BufferInfo = MediaCodec.BufferInfo()
            codec.dequeueOutputBuffer(bufferInfo,500)
            codec.getOutputBuffer(index)
            codec.releaseOutputBuffer(index,false)
        }

        override fun onOutputBufferAvailable(
            codec: MediaCodec,
            index: Int,
            info: MediaCodec.BufferInfo
        ) {

            Log.i(TAG,"onOutputBufferAvailable")
        }

        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {

            Log.i(TAG,"onError")
        }

        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {

            Log.i(TAG,"onOutputFormatChanged")
        }
    }

}