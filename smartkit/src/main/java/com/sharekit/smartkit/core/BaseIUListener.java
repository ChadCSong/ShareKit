package com.sharekit.smartkit.core;

import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class BaseIUListener implements IUiListener {

    @Override
    public void onComplete(Object o) {

    }

    @Override
    public void onError(UiError uiError) {
        Log.e("QQIUiListen", uiError.errorMessage);
    }

    @Override
    public void onCancel() {

    }
}