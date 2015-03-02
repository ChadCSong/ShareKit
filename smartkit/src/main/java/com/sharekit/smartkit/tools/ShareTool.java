package com.sharekit.smartkit.tools;

import android.app.Activity;
import android.content.Context;

import com.sharekit.smartkit.core.QQ;
import com.sharekit.smartkit.core.SinaWeibo;
import com.sharekit.smartkit.core.WeChat;

/**
 * MofunSky.
 * 分享工具类，用来创建分享接口
 * Created by ChadSong on 15/1/16.
 * ^_^
 */
public class ShareTool {


    public static SinaWeibo getSinaWeiboShareApi (Activity activity,String APP_KEY){
        return new SinaWeibo(activity, APP_KEY);
    }
    public static WeChat getWeChatShareApi (Context context,String APP_KEY){
        return new WeChat(context,APP_KEY);
    }
    public static QQ getQQShareApi (Activity activity,String APP_KEY,String APP_NAME){
        return new QQ(activity,APP_KEY,APP_NAME);
    }



}
