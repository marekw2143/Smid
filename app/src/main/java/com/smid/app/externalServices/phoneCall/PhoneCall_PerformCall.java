package com.smid.app.externalServices.phoneCall;

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
public class PhoneCall_PerformCall extends Service implements IExternalService{
    public class PhoneCallBinder extends Binder {
        public IExternalService getService() {
            return PhoneCall_PerformCall.this;
        }
    }

    private final IBinder mBinder = new PhoneCallBinder();

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

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        callIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        startActivity(callIntent);
    }
}
