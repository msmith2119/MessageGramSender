package com.msmith.messagegramsender;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;


import java.util.HashMap;

/**
 * Created by morgan on 8/11/16.
 */
public class ContactUtils {

    public static HashMap<String, String> getContactDetail(Context context, String lookupKey) {
        HashMap<String, String> values = new HashMap<String, String>();

        Cursor cursorPhone = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY},
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{lookupKey},
                null
        );



        if (cursorPhone.moveToFirst()) {
            values.put("contactNumber", cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            values.put("contactName", cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME)));
            values.put("contactKey", cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.Data.LOOKUP_KEY)));

        }
        return values;
    }


}
