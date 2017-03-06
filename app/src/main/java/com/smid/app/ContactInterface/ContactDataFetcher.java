package com.smid.app.ContactInterface;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.smid.app.Helper;

/**
 * Created by marek on 09.06.16.
 */
public class ContactDataFetcher {

    public ContactDataFetcher() {

    }

    public String getContactId(Context ctx, Uri contactUri) {
        Cursor cur = null;
        String contactID = null;

        try {

            cur = ctx.getContentResolver().query(contactUri, null, null, null, null);

            if (cur.moveToFirst()) {
                contactID = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            }
        } catch (Exception ex) {
            Helper.logException(ctx, ex);
        } finally {
            Helper.safelyCloseCursor(cur);
        }

        return contactID;
    }

    private String getContactPart(Context ctx, Uri contactUri, Uri dataUri,  String dataToFetch, String contactCondition) {
        String contactID = getContactId(ctx, contactUri);

        String ret = null;

        Cursor cursorPhone = null;

        try {

            cursorPhone = ctx.getContentResolver()
                    .query(dataUri,
                            new String[]{dataToFetch},
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " + contactCondition,
                            new String[]{contactID},
                            null);


            if (cursorPhone.moveToFirst()) {
                ret = cursorPhone.getString(cursorPhone.getColumnIndex(dataToFetch));

                return ret;
            }

        } catch (Exception ex) {
            Helper.logException(ctx, ex);
        } finally {
            if (cursorPhone != null)
                cursorPhone.close();
        }

        return ret;
    }

    public String GetContactNumber(Context ctx, Uri contactUri) {
        return getContactPart(ctx,
                contactUri,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
    }

    public String GetContactName(Context ctx, Uri contactUri) {
        String contactID = getContactId(ctx, contactUri);
        String ret = null;
        Cursor cur = null;

        try {
            cur = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID, null, null);

            if(cur.moveToFirst()) {
                ret = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            }

        } catch (Exception ex) {
            Helper.logException(ctx, ex);
        } finally {
            Helper.safelyCloseCursor(cur);
        }

        return ret;
    }
}
