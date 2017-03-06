package com.smid.app.payments;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.smid.app.BaseActivity;
import com.smid.app.Helper;
import com.smid.app.smid.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by marek on 09.07.16.
 */
public class PaymentActivityDebug extends BaseActivity {
    private static final int PURCHASE_ITEM_RQ = 1001;

    IInAppBillingService mService;

    public static final String PERF_KEY_hasBoughtAccess = PaymentHelper.PERF_KEY_hasBoughtAccess;

    public static final String PREF_KEY_hasBoughtAccess_HASH = PaymentHelper.PREF_KEY_hasBoughtAccess_HASH;

    public static final String SKU_MORE_THAN_TWO = PaymentHelper.SKU_MORE_THAN_TWO;

    public static final String API_RESPONSE_CODE = PaymentHelper.API_RESPONSE_CODE;

    String devPayload;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);

            init();
        }
    };

    protected void consume() {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(PERF_KEY_hasBoughtAccess, PaymentHelper.BOUGHT_ACCESS_NOT_DEFINED);
            editor.putString(PREF_KEY_hasBoughtAccess_HASH, "");
            editor.commit();

            String purchaseToken = "inapp:" + PaymentHelper.packageName + ":" + SKU_MORE_THAN_TWO;

            mService.consumePurchase(3, PaymentHelper.packageName, purchaseToken);

        } catch (Exception e) {
            Helper.logException(this, e);
        }
    }

    private void init() {
        getPaymentInfo().setVisibility(View.GONE);

        getContentLayout().setVisibility(View.VISIBLE);

        try {
            String price = getPriceString();

            Button btn = getButtonBuy();

            btn.setText(getString(R.string.Payment_ButtonBuy) + " " + price + ".");
        } catch (Exception e) {
            Helper.showOkMessage(this, "Error during fetching price", null);
        }
    }

    String getPriceString() {
        SkuDetails skuDetails = PaymentHelper.getSkuDetails(this, mService, SKU_MORE_THAN_TWO);

        return skuDetails.getPrice();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        setContentView(R.layout.payment_activity_debug);

        Helper.setTopText(this);
    }

    public void onClick(View v) {
        if(v.getId() == R.id.Payment_BtnBuy) {
            buyAccessToMoreThanTwo();
        } else if (v.getId() == R.id.Payment_getPurchases) {
            List<String> purchases = PaymentHelper.getPurchases(this, mService);

            String text = "";
            for(int i=0;i<purchases.size();i++) {
                text += purchases.get(i);
            }

            getTextPurhcases().setText(text);
        } else if(v.getId() == R.id.Payment_Consume) {
            consume();
        } else if(v.getId() == R.id.Payment_PrintHasAccess) {
            getTextHasAccess().setText(Boolean.toString(PaymentHelper.HasAccess(this)));
        }
    }

    public void buyAccessToMoreThanTwo () {
        try {
            devPayload = PaymentHelper.HashSha256(PaymentHelper.getAccountName(this) + SKU_MORE_THAN_TWO + PaymentHelper.SALT_DEV_PAYLOAD);

            Bundle buyIntentBundle = mService.getBuyIntent(3, PaymentHelper.packageName, SKU_MORE_THAN_TWO,
                    "inapp", devPayload);

            int response = buyIntentBundle.getInt(API_RESPONSE_CODE);

            if (response == 0) {
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

                startIntentSenderForResult(pendingIntent.getIntentSender(),
                        PURCHASE_ITEM_RQ, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                        Integer.valueOf(0));
            }
        } catch (RemoteException e) {
            Helper.logException(this, e);
        } catch (IntentSender.SendIntentException e) {
            Helper.logException(this, e);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PURCHASE_ITEM_RQ) {
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    String developerPayload = jo.getString("developerPayload");

                    if(!developerPayload.equals(devPayload)) {
                        throw new Exception("Received developer payload is different than generated one");
                    }
                    if(sku.equals(SKU_MORE_THAN_TWO)) {
                        PaymentHelper.setAccessToMoreThanTwo(this);
                    }
                }
                catch (Exception e) {
                    Helper.showErrorMessage(this ,getString(R.string.ErrorDuringBuyingItem));
                    Helper.logException(this, e);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mServiceConn!=null) {
            unbindService(mServiceConn);
        }
    }

    protected TextView getWhatCanBuyDescription() {
        return (TextView) getContentLayout().findViewById(R.id.Payment_WhatCanBuyDescription);
    }

    protected TextView getPaymentInfo() {
        return (TextView) findViewById(R.id.MainActivity_MainLayout).findViewById(R.id.Payment_Info);
    }
    protected TextView getTextHasAccess() {
        return (TextView)getContentLayout().findViewById(R.id.Payment_TextHasAccess);
    }

    protected ViewGroup getMainLayout () {
        return (ViewGroup)findViewById(R.id.MainActivity_MainLayout);
    }

    protected ViewGroup getContentLayout(){
        return (ViewGroup) findViewById(R.id.MainActivity_MainLayout).findViewById(R.id.MainActivity_ContentLayout);
    }

    protected TextView getTextPurhcases(){
        return (TextView) getContentLayout().findViewById(R.id.Payment_TextPurhcases);
    }


    Button getButtonBuy () {
        return (Button) getContentLayout().findViewById(R.id.Payment_BtnBuy);
    }
}
