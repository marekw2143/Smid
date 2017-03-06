package com.smid.app.externalServices;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.smid.app.ContactInterface.ContactDataFetcher;
import com.smid.app.GD_CONSTS;

public abstract class GeneralSelectContactActivity extends AppCompatActivity {

    protected void doSelect() {
        Intent i = new Intent(Intent.ACTION_PICK);

        i.setType(ContactsContract.Contacts.CONTENT_TYPE);//, ContactsContract.Contacts.CONTENT_URI);

        startActivityForResult(i, 1);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doSelect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactUri = data.getData();

                if(!checkUriAfterSelected(contactUri)) {
                    return;
                }
                ContactDataFetcher contactDataFetcher = new ContactDataFetcher();

                String name= contactDataFetcher.GetContactName(this, contactUri);
                if(name == null) {
                    name= contactDataFetcher.GetContactNumber(this, contactUri);
                }
                if(name != null && name.equals("")) {
                    name= contactDataFetcher.GetContactNumber(this, contactUri);
                }

                Intent resultIntent = new Intent();

                resultIntent.putExtra(GD_CONSTS.EXTERNAL_SERVICE_EXTERNAL_OPTION_ID, contactUri.toString());
                resultIntent.putExtra(GD_CONSTS.EXTERNAL_SERVICE_EXTERNAL_OPTION_FRIENDLY_NAME, getFriendlyName() + " " +  name);

                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    protected abstract String getFriendlyName();

    protected boolean checkUriAfterSelected(Uri contactUri) {
        return true;
    }
}
