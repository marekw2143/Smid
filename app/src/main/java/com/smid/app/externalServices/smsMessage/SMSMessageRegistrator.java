package com.smid.app.externalServices.smsMessage;

import android.content.Context;

import com.smid.app.smid.R;
import com.smid.app.externalServices.ExternalServiceRegistration;

/**
 * Created by marek on 30.06.16.
 */
public class SMSMessageRegistrator {
    private static final String fetcher = "com.gdapplication.app.gesturedetector.externalServices.smsMessage.SmsMessage_SelectContact";
    private static final String executor = "com.gdapplication.app.gesturedetector.externalServices.smsMessage.SmsMessage_SendMessage";

    public static void Register(Context ctx) throws Exception {

        ExternalServiceRegistration.RegisterSrevice(ctx, ctx.getString(R.string.PhoneCallService_ServiceName),
                fetcher,
                executor);
    }
}
