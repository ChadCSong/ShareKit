package com.sharekit.smartkit.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.sharekit.smartkit.R;
import com.sharekit.smartkit.params.SinaWeiboParams;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.VoiceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.tauth.UiError;

import java.io.IOException;

/**
 * @title SinaWeibo
 * @description
 * @author Chad
 * @since 15/1/16 下午9:08
 * @modifier
 * @date
 **/

public class SinaWeibo extends FragmentActivity implements IWeiboHandler.Response {

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

    //微博Api
    IWeiboShareAPI mWeiboShareAPI;
    Activity activity;
    String APP_KEY;
    BaseIUListener baseIUListener;
    /**
     * 构造函数
     * @param activity
     * @param APP_KEY 与包名不匹配的话会造成微博NPE错误
     */
    public SinaWeibo(Activity activity,String APP_KEY){
        this.activity = activity;
        this.APP_KEY = APP_KEY;
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, APP_KEY);
        mWeiboShareAPI.registerApp();
    }


    public boolean shareSinaWeibo(final SinaWeiboParams sinaWeiboParams, final int type,BaseIUListener baseIUListener) {
        this.baseIUListener = baseIUListener;
        // 创建微博 SDK 接口实例
        // 获取微博客户端相关信息，如是否安装、支持 SDK 的版本
        boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
        // 如果未安装微博客户端，设置下载微博对应的回调
        if (!isInstalledWeibo) {
            Toast.makeText(activity,
                    R.string.not_install_weibo,
                    Toast.LENGTH_SHORT).show();

        }

        //是否转换图片

        if(checkType(sinaWeiboParams,type)){
            if(sinaWeiboParams.getThumb()==null&&sinaWeiboParams.getThumbUrl()!=null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sinaWeiboParams.setThumb(KitCore.getBitmapFromURL(sinaWeiboParams.getThumbUrl(),activity,200));
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
            } else if (sinaWeiboParams.getThumb() != null) {
                Message message = new Message();
                message.what = 1;
                message.obj = sinaWeiboParams;
                message.arg1 = type;
                handler.sendMessage(message);
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



    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1) {
                sendWeiboMessage((SinaWeiboParams)msg.obj, msg.arg1);
            }

        }
    };

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
            Toast.makeText(activity, R.string.not_support_api_hint, Toast.LENGTH_SHORT).show();
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
        mWeiboShareAPI.sendRequest(activity, request);
        if (sinaWeiboParams.getThumb() != null) {
            sinaWeiboParams.getThumb().recycle();
        }
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
        mWeiboShareAPI.sendRequest(activity, request);
        if (sinaWeiboParams.getThumb() != null) {
            sinaWeiboParams.getThumb().recycle();
        }
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
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }


    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                baseIUListener.onComplete(baseResponse);
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                baseIUListener.onCancel();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                baseIUListener.onError(new UiError(1,"",""));
                break;
        }
    }
    public IWeiboShareAPI getmWeiboShareAPI(){
        if(mWeiboShareAPI !=null)
            return mWeiboShareAPI;
        else return WeiboShareSDK.createWeiboAPI(activity, APP_KEY);
    }

}
