package com.sharekit.smartkit.core;

import android.content.Context;
import android.graphics.Bitmap;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @title KitCore
 * @description
 * @author Chad
 * @since 15/7/7 上午11:04
 * @modifier
 * @date
 **/

public class KitCore {
    //平台标记
    public static String WeChat = "WE_CHAT_PLATFORM";
    public static String WeChatMoment = "WE_CHAT_MOMENT_PLATFORM";
    public static String WeChatFavorite = "WE_CHAT_FAVOURITE_PLATFORM";
    public static String QQ = "QQ_PLATFORM";
    public static String QQZone = "QQ_ZONE_PLATFORM";
    public static String SinaWeibo = "SINA_WEIBO_PLATFORM";


    /**
     * 从URL中获取Bitmap
     *
     * @param url     网址
     * @param context
     * @param sizeX   输出Width
     * @param sizeY   输出Height
     * @param KByte   最大输出大小
     * @return
     * @throws IOException
     */

    public static Bitmap getBitmapFromURL (String url,Context context,int sizeX,int sizeY,int KByte) throws IOException {
        Bitmap tempBitmap = Picasso.with(context).load(url).resize(sizeX,sizeY).get();
        if (tempBitmap != null) {
            while (tempBitmap.getRowBytes() * tempBitmap.getHeight() > KByte * 1024) {
                sizeX = sizeX / 2;
                sizeY = sizeY / 2;
                tempBitmap = Picasso.with(context).load(url).resize(sizeX, sizeY).get();
            }
            return tempBitmap;
        } else return null;
    }

    /**
     * 从URL中获取Bitmap
     * @param url 网址
     * @param context
     * @param KByte 最大输出大小
     * @return
     * @throws IOException
     */
    public static Bitmap getBitmapFromURL (String url,Context context,int KByte) throws IOException {
        Bitmap tempBitmap = Picasso.with(context).load(url).get();
        if (tempBitmap != null) {
            while (tempBitmap.getRowBytes() * tempBitmap.getHeight() > KByte * 1024) {
                tempBitmap = Picasso.with(context).load(url).resize(tempBitmap.getWidth() / 2, tempBitmap.getHeight() / 2).get();
            }
            return tempBitmap;
        } else return null;
    }
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
