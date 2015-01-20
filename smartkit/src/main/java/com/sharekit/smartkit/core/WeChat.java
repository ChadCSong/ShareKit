package com.sharekit.smartkit.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.sharekit.smartkit.params.WeChatParams;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;


/**
 * @title WeChat
 * @description 
 * @author Chad
 * @since 15/1/17 下午6:36
 * @modifier 
 * @date 
 **/

public class WeChat {

    /**
     * 图片大小
     */
    private static final int THUMB_SIZE = 150;
    /**
     * 定义微信分享类型
     */
    public static String TYPE_TEXT = "text";
    public static String TYPE_IMG = "img";
    public static String TYPE_MUSIC = "music";
    public static String TYPE_VIDEO = "video";
    public static String TYPE_WEB_PAGE = "webpage";
    public static String TYPE_APP_DATA = "appdata";
    public static String TYPE_EMOJI = "emoji";

    /**
     * 定义分享目标
     */
    public static final int WXSceneSession = 0;
    public static final int WXSceneTimeline = 1;
    public static final int WXSceneFavorite = 2;

    /**
     * 定义微信API
     */

    private static IWXAPI mWeChatApi;
    Context context;
    String APP_KEY;

    /**
     * 构造函数
     * @param context
     * @param APP_KEY 与包名不匹配的话会造成微信唤起失败
     */
    public WeChat(Context context,String APP_KEY){
        this.context = context;
        this.APP_KEY = APP_KEY;
        mWeChatApi = WXAPIFactory.createWXAPI(context , APP_KEY, false);
        mWeChatApi.registerApp(APP_KEY);
    }

    public IWXAPI getmWeChatApi(){
        return mWeChatApi;
    }
    public boolean shareWeChatSession(WeChatParams weChatParams,String type){
        return shareWeChat(weChatParams,type,WXSceneSession);
    }
    public boolean shareWeChatTimeline(WeChatParams weChatParams,String type){
        return shareWeChat(weChatParams,type,WXSceneTimeline);
    }
    public boolean shareWeChatFavourite(WeChatParams weChatParams,String type){
        return shareWeChat(weChatParams,type,WXSceneFavorite);
    }

    private boolean shareWeChat(final WeChatParams weChatParams, final String type,final int shareType){

        if(checkParams(weChatParams,type)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (weChatParams.getThumb() == null) {
                            weChatParams.setThumb(KitCore.getBitmapFromURL(weChatParams.getThumbUrl(), context, 90,90,32));
                        }
                        Message message = new Message();
                        message.obj = weChatParams;
                        message.arg1 = shareType;
                        if (type.equals(TYPE_TEXT)) {
                            message.what = 1;
                        } else if (type.equals(TYPE_IMG)) {
                            message.what = 2;
                        } else if (type.equals(TYPE_WEB_PAGE)) {
                            message.what = 3;
                        } else if (type.equals(TYPE_MUSIC)) {
                            message.what = 4;
                        } else if (type.equals(TYPE_VIDEO)) {
                            message.what = 5;
                        } else if (type.equals(TYPE_APP_DATA)) {
                            message.what = 6;
                        } else if (type.equals(TYPE_EMOJI)) {
                            message.what = 7;
                        } else {
                            message.what = 1;
                        }
                        sessionHandler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            return false;
        }
        return true;
    }

    private boolean checkParams(WeChatParams weChatParams, String type) {
        return true;
    }

    Handler sessionHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            WeChatParams weChatParams = (WeChatParams)msg.obj;
            switch (msg.what)
            {
                case 1:
                    sendTxtToWeChat(weChatParams.getDescription(),msg.arg1);
                    break;
                case 2:
                    sendImgToWeChat(weChatParams.getThumb(),msg.arg1);
                    break;
                case 3:
                    sendWebPageToWeChatFromURL(weChatParams,msg.arg1);
                    break;
                case 4:
                    sendMusicToWeChatFromURL(weChatParams,msg.arg1);
                    break;
                case 5:
                    sendVideoToWeChatFromURL(weChatParams,msg.arg1);
                    break;
                case 6:
                    sendAppDataToWeChat(weChatParams,msg.arg1);
                    break;
                case 7:
                    sendEmojiToWeChat(weChatParams,msg.arg1);
                    break;

            }
        }
    };

    private void sendTxtToWeChat(String text , int shareType){
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(TYPE_TEXT);
        req.message = msg;
        /**
         * WXSceneTimeline朋友圈
         * WXSceneSession会话
         * WXSceneFavourite收藏
         */
        req.scene = shareType;
        mWeChatApi.sendReq(req);
    }
    private void sendImgToWeChat(Bitmap bmp,int shareType){
        WXImageObject imgObj = new WXImageObject();
        sendImg(imgObj,bmp,shareType);
    }

    private void sendImg(WXImageObject imgObj,Bitmap bmp,int shareType){
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = KitCore.bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(TYPE_IMG);
        req.message = msg;
        req.scene =shareType;
        mWeChatApi.sendReq(req);
    }

    private void sendMusicToWeChatFromURL(WeChatParams weChatMusic,int shareType){
        WXMusicObject music = new WXMusicObject();

        music.musicUrl = weChatMusic.getShareUrl();
        if(weChatMusic.getShareLowBandUrl()!=null&&!weChatMusic.getShareLowBandUrl().equals("")) {
            music.musicLowBandUrl = weChatMusic.getShareLowBandUrl();
        }else {
            music.musicLowBandUrl = weChatMusic.getShareUrl();
        }
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = music;
        msg.title = weChatMusic.getTitle();
        msg.description = weChatMusic.getDescription();
        msg.thumbData = KitCore.bmpToByteArray(weChatMusic.getThumb(), true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(TYPE_MUSIC);
        req.message = msg;
        req.scene = shareType;
        mWeChatApi.sendReq(req);
    }

    private void sendVideoToWeChatFromURL(WeChatParams weChatVideo, int shareType){
        WXVideoObject video = new WXVideoObject();
        video.videoUrl = weChatVideo.getShareUrl();
        if(weChatVideo.getShareLowBandUrl()!=null&&weChatVideo.getShareLowBandUrl().equals("")){
            video.videoLowBandUrl = weChatVideo.getShareLowBandUrl();
        }else {
            video.videoLowBandUrl = weChatVideo.getShareUrl();
        }
        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title = weChatVideo.getTitle();
        msg.description = weChatVideo.getDescription();
        msg.thumbData = KitCore.bmpToByteArray(weChatVideo.getThumb(), true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(TYPE_VIDEO);
        req.message = msg;
        req.scene = shareType;
        mWeChatApi.sendReq(req);
    }

    private void sendWebPageToWeChatFromURL(WeChatParams weChatParams, int shareType){

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = weChatParams.getShareUrl();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = weChatParams.getTitle();
        msg.description = weChatParams.getDescription();
        msg.thumbData = KitCore.bmpToByteArray(weChatParams.getThumb(), true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(TYPE_WEB_PAGE);
        req.message = msg;
        req.scene = shareType;
        mWeChatApi.sendReq(req);
    }
    private void sendAppDataToWeChat(WeChatParams weChatParams,int shareType){

    }
    private void sendEmojiToWeChat(WeChatParams weChatParams,int shareType){

    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}