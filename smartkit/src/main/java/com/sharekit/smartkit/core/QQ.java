package com.sharekit.smartkit.core;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.sharekit.smartkit.R;
import com.sharekit.smartkit.params.QQParams;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

/**
 * @author Chad
 * @title com.sharekit.smartkit.core
 * @description
 * @modifier
 * @date
 * @since 15/1/19 下午12:48
 */
public class QQ {

    public static final int TYPE_IMAGE_TEXT = 1;
    public static final int TYPE_IMAGE_ONLY = 2;
    public static final int TYPE_MUSIC = 3;
    public static final int TYPE_APP_DATA = 4;

    Activity activity;
    String APP_KEY;
    Tencent mTencent;
    String APP_NAME;

    public QQ(Activity activity,String APP_KEY,String APP_NAME){
        this.activity = activity;
        this.APP_KEY = APP_KEY;
        this.APP_NAME = APP_NAME;
        mTencent =Tencent.createInstance(APP_KEY, activity);
    }

    public boolean shareQQ(QQParams qqParams,int type,BaseIUListener baseIUListener){
        Bundle params = new Bundle();
        if (!KitCore.isQQInstalled(activity)) {
            Toast.makeText(activity,
                    R.string.not_install_qq,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        switch (type)
        {
            case TYPE_IMAGE_TEXT:

                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                params.putString(QQShare.SHARE_TO_QQ_TITLE,qqParams.getTitle());
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY,qqParams.getSummary());
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,qqParams.getImage_url());
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,qqParams.getTarget_url());
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME,APP_NAME);
                mTencent.shareToQQ(activity,params,baseIUListener);
                break;
            case TYPE_MUSIC:
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                params.putString(QQShare.SHARE_TO_QQ_TITLE,qqParams.getTitle());
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY,qqParams.getSummary());
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,qqParams.getImage_url());
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,qqParams.getTarget_url());
                params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, qqParams.getAudio_url());
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME,APP_NAME);
                mTencent.shareToQQ(activity,params,baseIUListener);
                break;

        }
        return true;
    }

    public boolean shareQQZone(QQParams qqParams, int type, BaseIUListener baseIUListener) {
        Bundle params = new Bundle();
        if (!KitCore.isQQInstalled(activity)) {
            Toast.makeText(activity,
                    R.string.not_install_qq,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        switch (type) {
            case TYPE_IMAGE_TEXT:

                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, qqParams.getTitle());
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, qqParams.getSummary());
                ArrayList<String> imageArray = new ArrayList<String>();
                imageArray.add(qqParams.getImage_url());
                params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, imageArray);
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, qqParams.getTarget_url());
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, APP_NAME);
                mTencent.shareToQzone(activity, params, baseIUListener);
                break;
            case TYPE_MUSIC:
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, qqParams.getTitle());
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, qqParams.getSummary());
                ArrayList<String> imageArray1 = new ArrayList<String>();
                imageArray1.add(qqParams.getImage_url());
                params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, imageArray1);
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, qqParams.getTarget_url());
                params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, qqParams.getAudio_url());
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, APP_NAME);
                mTencent.shareToQzone(activity, params, baseIUListener);
                break;

        }
        return true;
    }

//    Bundle params = new Bundle();
//    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//    params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
//    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "要分享的摘要");
//    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://www.qq.com/news/1.html");
//    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
//    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "测试应用222222");
//    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
//    mTencent.shareToQQ(MainActivity.this, params, new BaseUiListener());
}
