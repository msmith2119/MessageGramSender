package com.msmith.messagegramsender;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by morgan on 8/9/16.
 */
public class SendMessageActivity extends Activity {

    private Spinner cSpinner;
    private Spinner mSpinner;
    private SQLiteDatabase db;
    private Cursor cursor;
    private CursorAdapter cAdapter;
    private CursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        cSpinner = (Spinner)findViewById(R.id.aliases_spinner);
       mSpinner = (Spinner)findViewById(R.id.message_spinner);
        ContactDatabaseHelper helper = new ContactDatabaseHelper(this);
        db = helper.getReadableDatabase();
        cursor =  helper.getDBContactsCursor(db);
        cAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_dropdown_item, cursor, new String[]{"alias"}, new int[]{android.R.id.text1}, 0);
        cSpinner.setAdapter(cAdapter);
        cursor = helper.getDBMessageCursor(db);
        mAdapter =  new SimpleCursorAdapter(this, android.R.layout.simple_spinner_dropdown_item, cursor, new String[]{"name"}, new int[]{android.R.id.text1}, 0);
        mSpinner.setAdapter(mAdapter);
        if(!canSendSMS())
        requestPermissions(new String[]{Manifest.permission.SEND_SMS},1223);


    }

     public void onSendClicked(View view){
         int id1 = cSpinner.getId();
         int id2 = mSpinner.getId();

          ContactDatabaseHelper helper = new ContactDatabaseHelper(this);
          db = helper.getWritableDatabase();
          int alias_id = (int)cSpinner.getSelectedItemId();
          int message_id = (int)mSpinner.getSelectedItemId();
          Contact contact = helper.getContact(db,alias_id);
          Message message = helper.getMessage(db,message_id);
         sendSMSMessage(contact.getAddress(),message.getMsg());
         if(!helper.hasPacket(db,alias_id,message_id)){
             Packet  packet = new Packet(message.getName()+"->"+contact.getAlias(),alias_id,message_id);
             helper.insertPacket(db,packet);
         }
         Log.v("onSendClicked","itemId="+cSpinner.getSelectedItemId());


     }

    protected void sendSMSMessage(String phoneNo, String message) {
        Log.i("Send SMS", "");

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Log.v("sendSMSMessage","smsManager.sendTextMessage("+phoneNo+", null, "+message+", null, null)");
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();

        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(!canSendSMS()) {
            Toast.makeText(getApplicationContext(), "SMS Permission denied", Toast.LENGTH_LONG).show();
        }

    }
    private boolean canSendSMS() {

        return(hasPermission(Manifest.permission.SEND_SMS));

    }

    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }
}