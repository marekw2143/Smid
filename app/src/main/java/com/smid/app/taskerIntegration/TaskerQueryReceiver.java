package com.smid.app.taskerIntegration;

/**
 * Created by marek on 30.07.16.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.smid.app.GestureListToSelect;
import com.twofortyfouram.locale.api.Intent;
import com.twofortyfouram.locale.sdk.client.receiver.AbstractPluginConditionReceiver;

/**
 * Created by marek on 29.07.16.
 */
public class TaskerQueryReceiver extends AbstractPluginConditionReceiver {
    private static long detectedId;


    public static synchronized void setDetectedGestureId(long id) {
        detectedId = id;
    }

    @Override
    protected boolean isBundleValid(@NonNull Bundle bundle) {
        // tutaj na podstawie ustawionego w MainActivity keya trzeba odrzucic wadliwe a zostawic dobre
        String value = bundle.getString(GestureListToSelect.KEY_VALUE);

        long actValue = Long.parseLong(value);

        return actValue == detectedId;
//        if(value.equals(Long.toString(detectedId))) {
//            return true;
//        } else {
//            return false;
//        }
    }

    @Override
    protected boolean isAsync() {
        return false;
    }

    @Override
    protected int getPluginConditionResult(@NonNull Context context, @NonNull Bundle bundle) {
        return Intent.RESULT_CONDITION_SATISFIED;
    }
}

