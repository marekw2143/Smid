package com.smid.app.externalServices.phoneCall;

import android.content.DialogInterface;
import android.net.Uri;

import com.smid.app.ContactInterface.ContactDataFetcher;
import com.smid.app.Helper;
import com.smid.app.smid.R;
import com.smid.app.externalServices.GeneralSelectContactActivity;

public class PhoneCall_SelectContact extends GeneralSelectContactActivity {

    @Override
    protected String getFriendlyName() {
        return getString(R.string.PhoneCall_FriendlyNamePrefix);
    }

    @Override
    protected boolean checkUriAfterSelected(Uri contactUri) {
        ContactDataFetcher fetcher = new ContactDataFetcher();

        String number = fetcher.GetContactNumber(this, contactUri);

        if(number == null || "".equals(number)){
            Helper.showOkMessage(this, getString(R.string.PhoneCall_SelectContact_ContactHasNoNUmber), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PhoneCall_SelectContact.this.doSelect();
                }
            });

            return false;
        }

        return true;
    }
}