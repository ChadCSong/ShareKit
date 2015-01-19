package com.sharekit.smartkit.toos;

import android.content.Context;

import com.sharekit.smartkit.core.SinaWeibo;
import com.sharekit.smartkit.core.WeChat;

/**
 * MofunSky.
 * Created by ChadSong on 15/1/16.
 * ^_^
 */
public class ShareTool {

    public static SinaWeibo getSinaWeiboShareApi (Context context,String APP_KEY){
        return new SinaWeibo(context, APP_KEY);
    }
    public static WeChat getWeChatShareApi (Context context,String APP_KEY){
        return new WeChat(context,APP_KEY);
    }
}
