package com.smid.app.externalServices.smsMessage;

import com.smid.app.smid.R;
import com.smid.app.externalServices.GeneralSelectContactActivity;

/**
 * Created by marek on 30.06.16.
 */
public class SmsMessage_SelectContact extends GeneralSelectContactActivity {
    @Override
    protected String getFriendlyName() {
        return getString(R.string.SMSMessage_FriendlyName);
    }
}
