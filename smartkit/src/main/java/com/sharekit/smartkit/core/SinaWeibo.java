package com.sharekit.smartkit.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.sharekit.smartkit.params.SinaWeiboParams;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.Utility;

import java.io.IOException;

/**
 * @title SinaWeibo
 * @description
 * @author Chad
 * @since 15/1/16 下午9:08
 * @modifier
 * @date
 **/

public class SinaWeibo {

    //静态标记
    /**
     * 所有分享类型，不同的分享类型需要的SinaWeiboParams元素不一样。
     */

    /**
     * TYPE_TEXT 文本分享
     * SinaWeiboParams 需要
     * content
     */
    public final static int TYPE_TEXT = 1;

    /**
     * TYPE_VIDEO 视频分享
     * SinaWeiboParams 需要
     *
     */
    public final static int TYPE_VIDEO = 2;
    public final static int TYPE_MUSIC = 3;
    public final static int TYPE_IMAGE = 4;
    public final static int TYPE_WEBPAGE = 5;
    public final static int TYPE_VOICE = 6;


    public static final String WEIBO_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * <p/>
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * <p/>
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * <p/>
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String WEIBO_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    //微博Api
    IWeiboShareAPI mWeiboShareAPI;
    Activity activity;
    Context context;
    String APP_KEY;
    Oauth2AccessToken mWeiboAccessToken;
    SsoHandler mSsoHandler;
    UsersAPI mUsersAPI;
    WeiboLoginListener weiboLoginListener;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1) {
                sendWeiboMessage((SinaWeiboParams)msg.obj, msg.arg1);
            }

        }
    };
    private AuthInfo mAuthInfo;


    /**
     * 构造函数
     * @param activity
     * @param APP_KEY 与包名不匹配的话会造成微博NPE错误
     */
    public SinaWeibo(Activity activity,String APP_KEY){
        this.context = activity;
        this.activity = activity;
        this.APP_KEY = APP_KEY;
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, APP_KEY);
        mWeiboShareAPI.registerApp();
    }

    public boolean shareSinaWeibo(final SinaWeiboParams sinaWeiboParams, final int type) {
        // 创建微博 SDK 接口实例
        // 获取微博客户端相关信息，如是否安装、支持 SDK 的版本
        boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
        // 如果未安装微博客户端，设置下载微博对应的回调

        //是否转换图片

        if(checkType(sinaWeiboParams,type)){
            if(sinaWeiboParams.getThumb()==null&&sinaWeiboParams.getThumbUrl()!=null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sinaWeiboParams.setThumb(KitCore.getBitmapFromURL(sinaWeiboParams.getThumbUrl(),context,200));
                            Message message = new Message();
                            message.what = 1;
                            message.obj = sinaWeiboParams;
                            message.arg1 = type;
                            handler.sendMessage(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            return true;
        }else {
            return false;
        }
    }

    private boolean checkType(SinaWeiboParams sinaWeiboParams,int type) {
        switch (type)
        {
            case TYPE_TEXT:
                return sinaWeiboParams.getContent()!=null;
            case TYPE_IMAGE:
                return (sinaWeiboParams.getThumb()!=null
                        || sinaWeiboParams.getThumbUrl()!=null)
                        && sinaWeiboParams.getContent()!=null;
            case TYPE_WEBPAGE:
                return (sinaWeiboParams.getThumb()!=null
                        || sinaWeiboParams.getThumbUrl()!=null)
                        && sinaWeiboParams.getContent()!=null
                        && sinaWeiboParams.getActionUrl()!=null
                        && sinaWeiboParams.getTitle()!=null;
            case TYPE_MUSIC:
                return (sinaWeiboParams.getThumb()!=null
                        || sinaWeiboParams.getThumbUrl()!=null)
                        && sinaWeiboParams.getContent()!=null
                        && sinaWeiboParams.getActionUrl()!=null
                        && (sinaWeiboParams.getDataUrl()!=null||sinaWeiboParams.getDataHdUrl()!=null)
                        && sinaWeiboParams.getTitle()!=null;
            case TYPE_VIDEO:
                return (sinaWeiboParams.getThumb()!=null
                        || sinaWeiboParams.getThumbUrl()!=null)
                        && sinaWeiboParams.getContent()!=null
                        && sinaWeiboParams.getActionUrl()!=null
                        && (sinaWeiboParams.getDataUrl()!=null||sinaWeiboParams.getDataHdUrl()!=null)
                        && sinaWeiboParams.getTitle()!=null;
            case TYPE_VOICE:
                return (sinaWeiboParams.getThumb()!=null
                        || sinaWeiboParams.getThumbUrl()!=null)
                        && sinaWeiboParams.getContent()!=null
                        && sinaWeiboParams.getActionUrl()!=null
                        && (sinaWeiboParams.getDataUrl()!=null||sinaWeiboParams.getDataHdUrl()!=null)
                        && sinaWeiboParams.getTitle()!=null;
            default:
                return false;
        }
    }

    public boolean registAppToSinaWeibo(){
        return mWeiboShareAPI.registerApp();
    }
    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
     */
    private void sendWeiboMessage(SinaWeiboParams sinaWeiboParams, int type) {
        boolean hasText = false;
        boolean hasImage = false;
        boolean hasWebpage = false;
        boolean hasMusic = false;
        boolean hasVideo = false;
        boolean hasVoice = false;

        switch (type)
        {
            case TYPE_TEXT:
                hasText = true;
                break;
            case TYPE_IMAGE:
                hasText = true;
                hasImage = true;
                break;
            case TYPE_WEBPAGE:
                hasText = true;
                hasImage = true;
                hasWebpage = true;
                break;
            case TYPE_MUSIC:
                hasText = true;
                hasImage = true;
                hasWebpage = true;
                hasMusic = true;
                break;
            case TYPE_VIDEO:
                hasText = true;
                hasImage = true;
                hasWebpage = true;
                hasVideo = true;
                break;
            case TYPE_VOICE:
                hasText = true;
                hasImage = true;
                hasWebpage = true;
                hasVoice = true;
                break;
        }

        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
            int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
            if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
                sendMultiMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo,hasVoice,sinaWeiboParams);
            } else {
                sendSingleMessage(hasText, hasImage, hasWebpage, hasMusic, hasVideo,sinaWeiboParams);
            }
        } else {
            Toast.makeText(context, "版本过低", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link com.sina.weibo.sdk.api.share.IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     *
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     * @param hasVoice   分享的内容是否有声音
     */
    private void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
                                  boolean hasMusic, boolean hasVideo, boolean hasVoice,SinaWeiboParams sinaWeiboParams) {

        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj(sinaWeiboParams);
        }

        if (hasImage) {
            weiboMessage.imageObject = getImageObj(sinaWeiboParams);
        }

        // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj(sinaWeiboParams);
        }
        if (hasMusic) {
            weiboMessage.mediaObject = getMusicObj(sinaWeiboParams);
        }
        if (hasVideo) {
            weiboMessage.mediaObject = getVideoObj(sinaWeiboParams);
        }
        if (hasVoice) {
            weiboMessage.mediaObject = getVoiceObj(sinaWeiboParams);
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(activity,request);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link com.sina.weibo.sdk.api.share.IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     *
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     */
    private void sendSingleMessage(boolean hasText, boolean hasImage, boolean hasWebpage,
                                   boolean hasMusic, boolean hasVideo,SinaWeiboParams sinaWeiboParams) {

        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        if (hasText) {
            weiboMessage.mediaObject = getTextObj(sinaWeiboParams);
        }
        if (hasImage) {
            weiboMessage.mediaObject = getImageObj(sinaWeiboParams);
        }
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj(sinaWeiboParams);
        }
        if (hasMusic) {
            weiboMessage.mediaObject = getMusicObj(sinaWeiboParams);
        }
        if (hasVideo) {
            weiboMessage.mediaObject = getVideoObj(sinaWeiboParams);
        }
        /*if (hasVoice) {
            weiboMessage.mediaObject = getVoiceObj();
        }*/

        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(activity,request);
    }

    /**
     * 获取分享的文本模板。
     *
     * @return 分享的文本模板
     */
    private String getSharedText() {
//        int formatId = R.string.weibosdk_demo_share_text_template;
//        String format = getString(formatId);
//        String text = format;
//        String demoUrl = getString(R.string.weibosdk_demo_app_url);
//        if (mTextCheckbox.isChecked() || mImageCheckbox.isChecked()) {
//            format = getString(R.string.weibosdk_demo_share_text_template);
//        }
//        if (mShareWebPageView.isChecked()) {
//            format = getString(R.string.weibosdk_demo_share_webpage_template);
//            text = String.format(format, getString(R.string.weibosdk_demo_share_webpage_demo), demoUrl);
//        }
//        if (mShareMusicView.isChecked()) {
//            format = getString(R.string.weibosdk_demo_share_music_template);
//            text = String.format(format, getString(R.string.weibosdk_demo_share_music_demo), demoUrl);
//        }
//        if (mShareVideoView.isChecked()) {
//            format = getString(R.string.weibosdk_demo_share_video_template);
//            text = String.format(format, getString(R.string.weibosdk_demo_share_video_demo), demoUrl);
//        }
//        if (mShareVoiceView.isChecked()) {
//            format = getString(R.string.weibosdk_demo_share_voice_template);
//            text = String.format(format, getString(R.string.weibosdk_demo_share_voice_demo), demoUrl);
//        }

        return "测试分享";
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj(SinaWeiboParams sinaWeiboParams) {
        TextObject textObject = new TextObject();
        textObject.text = sinaWeiboParams.getContent();
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(SinaWeiboParams sinaWeiboParams) {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(sinaWeiboParams.getThumb());
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj(SinaWeiboParams sinaWeiboParams) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = sinaWeiboParams.getTitle();
        mediaObject.description = sinaWeiboParams.getDescription();

        // 设置 Bitmap 类型的图片到视频对象里
        mediaObject.setThumbImage(sinaWeiboParams.getThumb());
        mediaObject.actionUrl = sinaWeiboParams.getActionUrl();
        mediaObject.defaultText = sinaWeiboParams.getContent();
        return mediaObject;
    }

    /**
     * 创建多媒体（音乐）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private MusicObject getMusicObj(SinaWeiboParams sinaWeiboParams) {
        // 创建媒体消息
        MusicObject musicObject = new MusicObject();
        musicObject.identify = Utility.generateGUID();
        musicObject.title = sinaWeiboParams.getTitle();
        musicObject.description = sinaWeiboParams.getDescription();

        // 设置 Bitmap 类型的图片到视频对象里
        musicObject.setThumbImage(sinaWeiboParams.getThumb());
        musicObject.actionUrl = sinaWeiboParams.getActionUrl();
        musicObject.dataUrl = sinaWeiboParams.getDataUrl();
        musicObject.dataHdUrl = sinaWeiboParams.getDataHdUrl();
        musicObject.duration = sinaWeiboParams.getDuration();
        musicObject.defaultText = sinaWeiboParams.getContent();
        return musicObject;
    }

    /**
     * 创建多媒体（视频）消息对象。
     *
     * @return 多媒体（视频）消息对象。
     */
    private VideoObject getVideoObj(SinaWeiboParams sinaWeiboParams) {
        // 创建媒体消息
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = sinaWeiboParams.getTitle();
        videoObject.description = sinaWeiboParams.getDescription();

        // 设置 Bitmap 类型的图片到视频对象里
        videoObject.setThumbImage(sinaWeiboParams.getThumb());
        videoObject.actionUrl = sinaWeiboParams.getActionUrl();
        videoObject.dataUrl = sinaWeiboParams.getDataUrl();
        videoObject.dataHdUrl = sinaWeiboParams.getDataHdUrl();
        videoObject.duration = sinaWeiboParams.getDuration();
        videoObject.defaultText = sinaWeiboParams.getContent();
        return videoObject;
    }

    /**
     * 创建多媒体（音频）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private VoiceObject getVoiceObj(SinaWeiboParams sinaWeiboParams) {
        // 创建媒体消息
        VoiceObject voiceObject = new VoiceObject();
        voiceObject.identify = Utility.generateGUID();
        voiceObject.title = sinaWeiboParams.getTitle();
        voiceObject.description = sinaWeiboParams.getDescription();

        // 设置 Bitmap 类型的图片到视频对象里
        voiceObject.setThumbImage(sinaWeiboParams.getThumb());
        voiceObject.actionUrl = sinaWeiboParams.getActionUrl();
        voiceObject.dataUrl = sinaWeiboParams.getDataUrl();
        voiceObject.dataHdUrl = sinaWeiboParams.getDataHdUrl();
        voiceObject.duration = sinaWeiboParams.getDuration();
        voiceObject.defaultText = sinaWeiboParams.getContent();
        return voiceObject;
    }




    public void loginWeibo(WeiboLoginListener weiboLoginListener){
        this.weiboLoginListener = weiboLoginListener;
        mAuthInfo = new AuthInfo(activity, APP_KEY, WEIBO_REDIRECT_URL, WEIBO_SCOPE);
        mSsoHandler = new SsoHandler(activity, mAuthInfo);
        mSsoHandler.authorize(new AuthDialogListener());
    }

    public SsoHandler getmSsoHandler(){
        return mSsoHandler;
    }

    public void authorizeCallBack(int requestCode, int resultCode,Intent data){
        mSsoHandler.authorizeCallBack(requestCode,resultCode,data);
    }

    public interface WeiboLoginListener{
        public void onComplete(String name,String token,String photo,String id);
        public void onError();
        public void onCancel();
    }

    public class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mWeiboAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mWeiboAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                mUsersAPI = new UsersAPI(activity,APP_KEY,mWeiboAccessToken);
                long uid = Long.parseLong(mWeiboAccessToken.getUid());
                mUsersAPI.show(uid, new RequestListener() {
                    @Override
                    public void onComplete(String response) {
                        if (!TextUtils.isEmpty(response)) {

                            User user = User.parse(response);
                            weiboLoginListener.onComplete(user.name,mWeiboAccessToken.getToken(),user.profile_image_url,user.id);
//                            remoteLogin(user.name,mWeiboAccessToken.getToken(),user.profile_image_url,user.id,3);
                        }
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        weiboLoginListener.onError();
                    }
                });
            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code", "");
            }
        }

        @Override
        public void onCancel() {
            weiboLoginListener.onCancel();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            weiboLoginListener.onError();
        }
    }
}
