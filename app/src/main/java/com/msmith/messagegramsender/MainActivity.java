package com.msmith.messagegramsender;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

 private SQLiteDatabase db ;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {
                if(position == 0){
                    Intent intent =  new Intent(MainActivity.this,ContactListActivity.class);
                    startActivity(intent);
                }
                else if (position == 1){
                    Intent intent= new Intent(MainActivity.this,MessageListActivity.class);
                    startActivity(intent);
                }
                else if (position == 2){
                    Intent intent= new Intent(MainActivity.this,SendMessageActivity.class);
                    startActivity(intent);
                }
                else if (position == 3){
                    Intent intent= new Intent(MainActivity.this,QuickSendActivity.class);
                    startActivity(intent);
                }
            }
        };
        ListView listView = (ListView)findViewById(R.id.list_options);
        listView.setOnItemClickListener(itemClickListener);


       // setUpFavorites();



    }


   private void setUpFavorites() {

       ListView favorites = (ListView)findViewById(R.id.list_favorites);
       ContactDatabaseHelper helper = new ContactDatabaseHelper(this);
       db = helper.getReadableDatabase();
       cursor = helper.getDBPacketCursor(db);
       CursorAdapter favoritesAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,
               cursor,new String[]{"name"},new int[]{android.R.id.text1},0);

       favorites.setAdapter(favoritesAdapter);

       AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
           @Override
           public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {

             ContactDatabaseHelper helper = new ContactDatabaseHelper(MainActivity.this);
              db = helper.getReadableDatabase();
              Packet packet  = helper.getPacket(db,(int)id);
              Contact contact = helper.getContact(db,packet.getAlias_id());
               final Message message = helper.getMessage(db,packet.getMessage_id());
               HashMap<String,String>  contactValues  = ContactUtils.getContactDetail(MainActivity.this,contact.getContactId());
               final String contactNumber = contactValues.get("contactNumber");
               AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
               builder.setMessage("Send "+packet.getName());

               builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       sendSMSMessage(contactNumber,message.getMsg());
                   }
               });
               builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });
               AlertDialog dialog = builder.create();
               dialog.show();

           }
       };
         favorites.setOnItemClickListener(itemClickListener);
       if(!canSendSMS())
           requestPermissions(new String[]{Manifest.permission.SEND_SMS},1223);

   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ToolBarActivityLauncher.handleToolbarSelection(this,item.getItemId());
        return true;



    }
    protected void sendSMSMessage(String phoneNo, String message) {
        if(!canSendSMS()){
            Toast.makeText(getApplicationContext(), "permission denied sending SMS", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Log.v("sendSMSMessage","smsManager.sendTextMessage("+phoneNo+", null, "+message+", null, null)");
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
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
