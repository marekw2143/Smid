package com.smid.app.payments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.android.vending.billing.IInAppBillingService;
import com.smid.app.Helper;
import com.smid.app.smid.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marek on 06.07.16.
 */
public class PaymentHelper {
    public static final String SALTT = "{#>TPC$;V-@W.ZBGU\"K.%EKDB(!A`PT<RK[#B\"US@?J'E(L}]CNU&`J~f]ZJN\\N-\"dfsfads897_($&_(&(FASL";
    public static final String SALT2 = "{#>TPC$;V-@W.ZBGU\"K.%EKDB(!A`PT<RK[#B\"US@?J'E(L}]CNU&`Jf~]ZJN\\N-\"dfsfads897_($&_(&(FASL";
    public static final String SALT3 = "{#>TPC$;V-@W.ZBGU\"K.%EKDB(!A`PT<RK[#B\"US@?J'E(L}]CNU&`fJ~]ZJN\\N-\"dfsfads897_($&_(&(FASL";
    public static final String SALT_DEV_PAYLOAD = "'F??\"X@~T=<RNT')X%FDXT?:T):H@&].HD$=U}FZJ%^f;$,Q]NXLK,V$]=H\\%/XT#";
    public static final String SALT4 = "{#>TPC$;V-@W.ZBGU\"K.%EKDB(!A`PT<RK[#B\"US@?J'E(L}]CNUf&`J~]ZJN\\N-\"dfsfads897_($&_(&(FASL";
    public static final String SALT = "{#>TPC$;V-@W.ZBGU\"K.%EKDB(!A`PT<RK[#B\"US@?J'E(L}]CNUf&`J~]ZJN\\N-\"dfsfads897_($&_(&(FASL";
    public static final String SALT5 = "{#>TPC$;V-@W.ZBGU\"K.%EKDB(!A`PT<RK[#B\"US@?J'E(L}]CfNU&`J~]ZJN\\N-\"dfsfads897_($&_(&(FASL";
    public static final String SALT6 = "{#>TPC$;V-@W.ZBGU\"K.%EKDB(!A`PT<RK[#B\"US@?J'E(L}]fCNU&`J~]ZJN\\N-\"dfsfads897_($&_(&(FASL";

    PaymentHelper(){
        int z = (SALTT + SALT + SALT2 + SALT3 + SALT_DEV_PAYLOAD + SALT4 + SALT5 + SALT6).length();
    }

    public static final String PERF_KEY_hasBoughtAccess ="PREF_KEY_BOUGHT_MORE_THAN_TWO";

    public static final String packageName = "com.smid.app";

    public static final String PREF_KEY_hasBoughtAccess_HASH ="PREF_KEY_BOUGHT_MORE_THAN_TWO_HASH_KEY";
    public static final int BOUGHT_ACCESS_NOT_DEFINED = 0;
    private static final int BOUGHT_ACCESS_YES = 1;
    private static final int BOUGHT_ACCESS_NO = 2;
    public static final String API_RESPONSE_CODE = "RESPONSE_CODE";

    public static final String SKU_MORE_THAN_TWO =  "more_than_two_shapes";//"test_product_1";

    public static void showAlertNeedsBuying(final Context ctx) {
        try {
            AlertDialog.Builder bbuilder = new AlertDialog.Builder(ctx);
            bbuilder.setMessage(ctx.getString(R.string.Payment_ToSaveMoreThanTwo));
            bbuilder.setPositiveButton(ctx.getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PaymentHelper.startPaymentActivity(ctx);
                }
            });
            bbuilder.setNegativeButton(ctx.getString(R.string.no), null);
            bbuilder.create();
            bbuilder.show();
        } catch (Exception e) {
            Helper.logException(ctx, e);
        }
    }

    public static void startPaymentActivity(Context context) {
        Intent i = new Intent(context, PaymentActivity.class);

        Helper.setTopTextToBeDisplayed(i, context.getString(R.string.Payment_Activity));

        context.startActivity(i);
    }

    public static boolean checkPaidOptions(Context ctx, IInAppBillingService mService) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);

        SharedPreferences.Editor editor = sharedPref.edit();

        if(getPurchases(ctx, mService).contains(SKU_MORE_THAN_TWO)) {
            setAccessToMoreThanTwo(ctx);

            return true;
        } else {
            editor.putInt(PERF_KEY_hasBoughtAccess, BOUGHT_ACCESS_NO);
            editor.commit();

            return false;
        }
    }

    protected static  void setAccessToMoreThanTwo(final Context ctx) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putInt(PERF_KEY_hasBoughtAccess, BOUGHT_ACCESS_YES);

            editor.putString(PREF_KEY_hasBoughtAccess_HASH, getHashSecureValue(ctx));

            editor.commit();
        } catch (Exception e) {
            Helper.logException(ctx, e);
        }
    }


    public static List<String> getPurchases (Context ctx, IInAppBillingService mService) {
        try {
            Bundle ownedItems = mService.getPurchases(3, packageName, "inapp", null);

            int response = ownedItems.getInt(API_RESPONSE_CODE);

            if (response == 0) {
                ArrayList<String> items = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");

                return items;
            }
        } catch (RemoteException e) {
            Helper.logException(ctx, e);
        }

        return null;
    }


    private static IInAppBillingService mService;

    static ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);

//            init();
        }
    };

    public static boolean HasAccess (Context ctx) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);

        int bought_access = sharedPref.getInt(PERF_KEY_hasBoughtAccess, BOUGHT_ACCESS_NOT_DEFINED);

        if(bought_access == BOUGHT_ACCESS_YES) { // jest podejrzenie ze dostep jest
            String savedHash = sharedPref.getString(PREF_KEY_hasBoughtAccess_HASH, ""); // porownaj hashe
            if(savedHash.equals(getHashSecureValue(ctx))) { // jesli ten sam -ok
                setAccessToMoreThanTwo(ctx);

                return true;
            } else {
                return false; // jesli inne to sorry
            }
        } else { // nie ma podejrzenia o dostep

            try {
                if (getPurchases(ctx, mService).contains(SKU_MORE_THAN_TWO)) {
                    setAccessToMoreThanTwo(ctx);
                    return true;
                };
            }catch (Exception e) {
                Helper.logException(ctx, e);
            }

            Intent serviceIntent =
                    new Intent("com.android.vending.billing.InAppBillingService.BIND");
            serviceIntent.setPackage("com.android.vending");
            ctx.bindService(serviceIntent, PaymentHelper.mServiceConn, Context.BIND_AUTO_CREATE);

            return false;
        }
    }

    public static String HashSha256(String textTohash) {
        MessageDigest hasher = null;

        try {
            hasher = MessageDigest.getInstance("SHA-256");
            hasher.update(textTohash.getBytes(Charset.forName("UTF-8")));
            byte[] msgDigest = hasher.digest();
            StringBuilder hexString = new StringBuilder();
            for(int i=0;i<msgDigest.length;i++) {
                hexString.append(Integer.toHexString(0xFF & msgDigest[i]));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Helper.logException(null, e);
        }

        return null;
    }

    public static SkuDetails getSkuDetails(Context ctx, IInAppBillingService mService, String skuId) {
        ArrayList<String> skuList = new ArrayList<String> ();
        skuList.add(skuId);
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        try {
            Bundle skuDetails = mService.getSkuDetails(3, packageName, "inapp", querySkus);

            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> responseList
                        = skuDetails.getStringArrayList("DETAILS_LIST");

                for (String thisResponse : responseList) {
                    JSONObject object = new JSONObject(thisResponse);
                    String sku = object.getString("productId");
                    String price = object.getString("price");
                    String price_currency_code = object.getString("price_currency_code");
                    String title = object.getString("title");
                    String description = object.getString("description");
                    if (sku.equals(skuId))
                        return new SkuDetails(skuId, price, price_currency_code, title, description);
                }
            }
        } catch (RemoteException e) {
            Helper.showOkMessage(ctx, ctx.getString(R.string.Payment_ErrorDuringGettingPrices), null);
            Helper.logException(ctx, e);
        } catch (JSONException e) {
            Helper.showOkMessage(ctx,ctx.getString(R.string.Payment_ErrorDuringGettingPrices), null);
            Helper.logException(ctx, e);
        }

        return null;
    }
    public static String getAccountName(Context ctx) {
        String accName = null;
        AccountManager manager = (AccountManager) ctx.getSystemService(ctx.ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        for (Account accounts : list) {
            if (accounts.type.equals("com.google")) {
                String accUserName = accounts.name;

                return accUserName;
            }
        }

        return null;
    }

    protected static String getToHashData (Context ctx) {
        String accName = null;// getAccountName(ctx);
        String androidId = Settings.Secure.ANDROID_ID;
        String skuName = SKU_MORE_THAN_TWO;
        String salt = PaymentHelper.SALT;

        String textToHash = accName + androidId + skuName + salt;

        return textToHash;
    }

    protected static String getHashSecureValue(Context ctx) {
        return PaymentHelper.HashSha256(getToHashData(ctx));
    }

}