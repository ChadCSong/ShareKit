package com.sharekit.smartkit.core;

import android.app.Activity;

import com.sharekit.smartkit.params.CommonShareParams;

/**
 * @author Chad
 * @title com.sharekit.smartkit.core
 * @description
 * @modifier
 * @date
 * @since 15/1/21 上午11:51
 */
public class CommonShare {
    Activity activity;
    String APP_NAME;
    public CommonShare(Activity activity,String APP_NAME){
        this.activity = activity;
        this.APP_NAME = APP_NAME;
    }

    public void onShare(CommonShareParams commonShareParams){

    }


}
