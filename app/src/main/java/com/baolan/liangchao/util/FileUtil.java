package com.baolan.liangchao.util;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {
    public static final String TAG = FileUtil.class.getSimpleName();

    public static File saveBitmapToFile(Bitmap bitmap, String dir, String fileName) {
        File appDir = new File(dir);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
            } finally {
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    /**
     *  YUV_420_888 转 bitmap
     * @param yuvByteArray
     * @param context
     * @return
     */
    public static Bitmap yuv420ByteToBitmap(byte[] yuvByteArray,int width,int height, Context context) {
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB script = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        // Refer the logic in a section below on how to convert a YUV_420_888 image
        // to single channel flat 1D array. For sake of this example I'll abstract it
        // as a method.
        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(yuvByteArray.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs))
                .setX(width)
                .setY(height);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        // The allocations above "should" be cached if you are going to perform
        // repeated conversion of YUV_420_888 to Bitmap.
        in.copyFrom(yuvByteArray);
        script.setInput(in);
        script.forEach(out);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bitmap);
        return bitmap;
    }

    public static Bitmap addTextToBitmap(Bitmap bitmap, String text, int textSize, int textColor) {
        // 创建一个新的和原始位图一样大小的位图
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        // 设置默认位图格式
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // 根据新的配置重新创建位图
        bitmap = bitmap.copy(bitmapConfig, true);

        // 初始化画布
        Canvas canvas = new Canvas(bitmap);


        // 初始化画笔
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        // 获取文字的宽度
        float textWidth = paint.measureText(text);
        // 获取文字的高度
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;
        // 将文字绘制在位图的右下角
        float x = 100;
        float y = 100;
        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    // 获得旋转角度之后的位图对象
    public static Bitmap getRotateBitmap(Bitmap bitmap, float rotateDegree) {
        Matrix matrix = new Matrix(); // 创建操作图片用的矩阵对象
        matrix.postRotate(rotateDegree); // 执行图片的旋转动作
        // 创建并返回旋转后的位图对象
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
    }
}