package com.smid.app.service;

import android.content.Context;

import com.smid.app.model.businessLogic.Gesture;

/**
 * Created by marek on 25.06.16.
 */
public interface ISimpleGestureDetectorReceiver {
    void gestureReceived(Gesture gesture, Context ctx);
}
