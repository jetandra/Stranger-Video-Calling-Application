package com.example.strangers.models;

import android.webkit.JavascriptInterface;

import com.example.strangers.CallActivity;

public class InterfaceJava {

    CallActivity callActivity;

    public InterfaceJava(CallActivity callActivity){

        this.callActivity = callActivity;
    }
    @JavascriptInterface
    public void onPeerConnected(){
        callActivity.onPeerConnected();

    }

}

