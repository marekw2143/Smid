package com.smid.app.externalServices.phoneCall;

import android.content.Context;

import com.smid.app.smid.R;
import com.smid.app.externalServices.ExternalServiceRegistration;

/**
 * Created by marek on 30.06.16.
 */
public class PhoneCallRegistrator {
    public static final String executor = "com.gdapplication.app.gesturedetector.externalServices.phoneCal.PhoneCall_PerformCall";
    public static final String fetcher = "com.gdapplication.app.gesturedetector.externalServices.phoneCal.PhoneCall_SelectContact";

    public static void register (Context ctx) throws Exception {

        ExternalServiceRegistration.RegisterSrevice(ctx, ctx.getString(R.string.PhoneCallService_ServiceName),
                fetcher,
                executor);
    }
}
