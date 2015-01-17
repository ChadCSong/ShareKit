package com.sharekit.smartkit.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageUtil {
    /**
     * @author : 桥下一粒砂
     * @email : chenyoca@gmail.com
     * @date : 2012-11-8
     * @desc :
     */
    public static final int ALL = 347120;
    public static final int TOP = 547120;
    public static final int LEFT = 647120;
    public static final int RIGHT = 747120;
    public static final int BOTTOM = 847120;

    /**
     * 灰度图转化
     *
     * @param bitmap
     * @return
     */
    public static Bitmap convert2Gray(Bitmap bitmap) {
        /**
         *  [ a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t ] When applied to a color [r, g, b, a],
         *  the resulting color is computed as (after clamping)
         *  R' = a*R + b*G + c*B + d*A + e; G' = f*R + g*G + h*B + i*A + j;
         *  B' = k*R + l*G + m*B + n*A + o; A' = p*R + q*G + r*B + s*A + t;
         */
        ColorMatrix colorMatrix = new ColorMatrix();
        /**
         * Set the matrix to affect the saturation(饱和度) of colors.
         * A value of 0 maps the color to gray-scale（灰阶）. 1 is identity
         */
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

        Paint paint = new Paint();
        paint.setColorFilter(filter);

        Bitmap result = bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return result;
    }

    /**
     * 适配图片到指定高度，同时等比缩放图片宽度
     *
     * @param bitmap
     * @param contanerH
     * @return
     */
    public static Bitmap scaleFitHeight(Bitmap bitmap, float contanerH) {
        if (bitmap == null) {
            return null;
        }
//        float sx = (float) contanerW / bitmap.getWidth();//要强制转换，不转换我的在这总是死掉。
        float sy = (float) contanerH / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(sy, sy); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        bitmap.recycle();
        return resizeBmp;
    }

    public static Bitmap scaleTo(Bitmap bitmap, float contanerW, float contanerH) {
        if (bitmap == null) {
            return null;
        }
        float sx = (float) contanerW / bitmap.getWidth();//要强制转换，不转换我的在这总是死掉。
        float sy = (float) contanerH / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        bitmap.recycle();
        return resizeBmp;
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int contanerW, int contanerH, int roundPx) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(contanerW, contanerH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, contanerW, contanerH);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }

    /**
     * 将图片切成圆角，并在上面覆盖一层指定的图层
     *
     * @param bitmap
     * @param wrapperPx 容器长宽
     * @return
     */
    public static Bitmap cutToCircle(Bitmap bitmap, int wrapperPx) {
        if (bitmap == null) {
            return null;
        }
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
//        int offset = wrapperPx - circlePx;
        Bitmap output = Bitmap.createBitmap(wrapperPx, wrapperPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xff424242);
//        //先将图片缩放
        float sx = (float) wrapperPx / bitmap.getWidth();//要强制转换，不转换我的在这总是死掉。
        float sy = (float) wrapperPx / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(Math.max(sx, sy), Math.max(sx, sy)); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        //在缩放图上剪出圆角效果
//        int left = (resizeBmp.getWidth()-wrapperPx)*-1;
//        int top = (resizeBmp.getHeight()-wrapperPx)*-1;
//        final Rect rect = new Rect(left,top, left+resizeBmp.getWidth(), top+resizeBmp.getHeight());
        int left = -1 * (resizeBmp.getWidth() - wrapperPx) / 2;
        int top = -1 * (resizeBmp.getHeight() - wrapperPx) / 2;
        final Rect rect = new Rect(left, top, left + resizeBmp.getWidth(), top + resizeBmp.getHeight());
        //指定圆点及半径画圆
        canvas.drawCircle(wrapperPx / 2, wrapperPx / 2, wrapperPx / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resizeBmp, null, rect, paint);
//        bitmap.recycle();
        resizeBmp.recycle();
        return output;
    }

    public static Bitmap overlay(Bitmap bitmap, Bitmap overlayBmp, PorterDuff.Mode pmode) {
        if (bitmap == null) {
            return null;
        }
        final Paint paint = new Paint();
        final Rect rect = new Rect(0 / 2, 0 / 2, bitmap.getWidth(), bitmap.getWidth());
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
//        int offset = wrapperPx - circlePx;
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawBitmap(bitmap, rect, rect, paint);

        //先将图片缩放
        float sx = (float) bitmap.getWidth() / overlayBmp.getWidth();//要强制转换，不转换我的在这总是死掉。
        float sy = (float) bitmap.getHeight() / overlayBmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(overlayBmp, 0, 0, overlayBmp.getWidth(), overlayBmp.getHeight(), matrix, true);

        paint.setXfermode(new PorterDuffXfermode(pmode));
        canvas.drawBitmap(resizeBmp, rect, rect, paint);
        resizeBmp.recycle();
//        bitmap.recycle();
        overlayBmp.recycle();
        return output;
    }

    /**
     * 指定图片的切边，对图片进行圆角处理
     *
     * @param type    具体参见：{@link .ALL} , {@link .TOP} ,
     *                {@link .LEFT} , {@link .RIGHT} , {@link .BOTTOM}
     * @param bitmap  需要被切圆角的图片
     * @param roundPx 要切的像素大小
     * @return
     */
    public static Bitmap fillet(int type, Bitmap bitmap, int width, int height, int roundPx) {
        try {
            // 其原理就是：先建立一个与图片大小相同的透明的Bitmap画板
            // 然后在画板上画出一个想要的形状的区域。
            // 最后把源图片帖上。

            Bitmap paintingBoard = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(paintingBoard);
            canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);

            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);

            if (TOP == type) {
                clipTop(canvas, paint, roundPx, width, height);
            } else if (LEFT == type) {
                clipLeft(canvas, paint, roundPx, width, height);
            } else if (RIGHT == type) {
                clipRight(canvas, paint, roundPx, width, height);
            } else if (BOTTOM == type) {
                clipBottom(canvas, paint, roundPx, width, height);
            } else {
                clipAll(canvas, paint, roundPx, width, height);
            }

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            //帖子图
            final Rect src = new Rect(0, 0, width, height);
            final Rect dst = src;
            canvas.drawBitmap(bitmap, src, dst, paint);
            return paintingBoard;
        } catch (Exception exp) {
            exp.printStackTrace();
            return bitmap;
        }
    }

    /**
     * 指定图片的切边，对图片进行圆角处理
     *
     * @param type    具体参见：{@link .ALL} , {@link .TOP} ,
     *                {@link .LEFT} , {@link .RIGHT} , {@link .BOTTOM}
     * @param bitmap  需要被切圆角的图片
     * @param roundPx 要切的像素大小
     * @return
     */
    public static Bitmap fillet(int type, Bitmap bitmap, int roundPx) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        return fillet(type, bitmap, width, height, roundPx);
    }

    private static void clipTop(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(0, offset, width, height);
        canvas.drawRect(block, paint);
        final RectF rectF = new RectF(0, 0, width, offset * 2);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    private static void clipLeft(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(offset, 0, width, height);
        canvas.drawRect(block, paint);
        final RectF rectF = new RectF(0, 0, offset * 2, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    private static void clipRight(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(0, 0, width - offset, height);
        canvas.drawRect(block, paint);
        final RectF rectF = new RectF(width - offset * 2, 0, width, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    private static void clipBottom(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(0, 0, width, height - offset);
        canvas.drawRect(block, paint);
        final RectF rectF = new RectF(0, height - offset * 2, width, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }

    private static void clipAll(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, offset, offset, paint);
    }
}