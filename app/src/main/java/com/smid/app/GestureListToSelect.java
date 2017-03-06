package com.smid.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.smid.app.model.businessLogic.Gesture;
import com.smid.app.taskerIntegration.TaskerQueryReceiver;

/**
 * Created by marek on 30.07.16.
 */
public class GestureListToSelect extends GestureList {

    public static String KEY_VALUE ="com.twofortyfouram.locale.example.condition.display.extra.BOOLEAN_STATE";

    public static String BUNDLE_EXTRA_INT_VERSION_CODE = "com.twofortyfouram.locale.example.condition.display.extra.INT_VERSION_CODE";


    @Override
    public void SelectionChanged(Gesture model) {
        setResultAndFinish(model.getId(), model.getName());
    }
public    static  Intent createRequestIntent()  {
    return new Intent(com.twofortyfouram.locale.api.Intent.ACTION_REQUEST_QUERY)
            .putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_ACTIVITY_CLASS_NAME, GestureListToSelect.class.getName());
}

    void setResultAndFinish(long gestureID, String gestureName) {
        String keyId = Long.toString(gestureID);

        Bundle resultBundle = new Bundle();

        resultBundle.putInt(BUNDLE_EXTRA_INT_VERSION_CODE, 1);
        resultBundle.putString(KEY_VALUE, keyId);

        Intent resultIntent = new Intent();

        resultIntent.putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_BUNDLE, resultBundle);
        resultIntent.putExtra(com.twofortyfouram.locale.api.Intent.EXTRA_STRING_BLURB, gestureName);

        TaskerQueryReceiver.setDetectedGestureId(gestureID);

        setResult(RESULT_OK, resultIntent);

        finish();
    }
}
