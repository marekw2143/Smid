package com.smid.app.externalServices.smsMessage;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.smid.app.ContactInterface.ContactDataFetcher;
import com.smid.app.model.businessLogic.IExternalService;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by marek on 30.06.16.
 */
public class SMSMessage_SendMessage extends Service implements IExternalService{
    public class SMSMessageBinder extends Binder {
        public IExternalService getService() {
            return SMSMessage_SendMessage.this;
        }
    }

    private final IBinder mBinder = new SMSMessageBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void call(String optionId) {

        ContactDataFetcher fetcher = new ContactDataFetcher();

        String phoneNumber = fetcher.GetContactNumber(this, Uri.parse(optionId));

        try {
            if (phoneNumber == null) { // TODO: == null !!!

            }
        } catch (Exception ex) {

        }

        Intent callIntent = new Intent(Intent.ACTION_SEND);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        callIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
        }
        startActivity(callIntent);
    }
}
