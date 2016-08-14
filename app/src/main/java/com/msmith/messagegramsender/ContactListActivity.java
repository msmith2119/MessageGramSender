package com.msmith.messagegramsender;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


/**
 * Created by morgan on 8/6/16.
 */
public class ContactListActivity extends ListActivity  {

    private SQLiteDatabase db;
    private Cursor cursor;
    private LayoutInflater  inflater;
    private CursorAdapter adapter;
    private ContactCallbackHandler searchHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        try {
            ContactDatabaseHelper helper = new ContactDatabaseHelper(this);
            db = helper.getReadableDatabase();
            cursor =  helper.getDBContactsCursor(db);
            ListView listContacts = getListView();
            //CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, new String[]{"name"}, new int[]{android.R.id.text1}, 0);
            adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, new String[]{"alias","name"}, new int[]{android.R.id.text1,android.R.id.text2}, 0);
            listContacts.setAdapter(adapter);
        } catch(SQLiteException e){
            Log.v("Contact",e.toString());
            Toast toast = Toast.makeText(this,"Database unavailable",Toast.LENGTH_SHORT);
            toast.show();
        }

        if(!canReadContacts())
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},1223);

        searchHandler = new ContactCallbackHandler(getListView(),getLoaderManager());

    }

    @Override
    protected void onRestart() {
        super.onRestart();

//        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(this);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        cursor = dbHelper.getDBCursor(db);
//        adapter = (CursorAdapter)getListView().getAdapter();
//        adapter.changeCursor(cursor);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, final long id) {
        inflater = getLayoutInflater();
        View content = inflater.inflate(R.layout.edit_contact,null);
         searchHandler =  new ContactCallbackHandler(content,getLoaderManager());
         searchHandler.init();
        final ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        final Contact contact = dbHelper.getContact(db,(int)id);

        final EditText aliasText = (EditText)content.findViewById(R.id.alias);
        final TextView contactNameText = (TextView)content.findViewById(R.id.name);
        final TextView  contactNumberText = (TextView)content.findViewById(R.id.number);
        Log.v("contactNumberText","contactNumberText="+contactNumberText);
        Log.v("onclick","id="+id);
        if(id >=0) {
            HashMap<String,String>  contactValues = ContactUtils.getContactDetail(this,contact.getContactId());
            Log.v("getContact","contactValues="+contactValues);
            aliasText.setText(contact.getAlias());
            contactNameText.setText(contactValues.get("contactName"));
            contactNumberText.setText(contactValues.get("contactNumber"));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(content);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(id>0){
             contact.setAlias(aliasText.getText().toString());
             if(searchHandler.getContactKey() != null){
                 contact.setName(searchHandler.getContactName());
                 contact.setContactId(searchHandler.getContactKey());
             }
                    dbHelper.updateContact(db,contact,(int)id);
                }

                else {
                    contact.setAlias(aliasText.getText().toString());
                    contact.setName(searchHandler.getContactName());
                    contact.setContactId(searchHandler.getContactKey());
                    dbHelper.updateContact(db,contact,(int)id);
                }


                //contact.setAlias(aliasText.getText().toString());
                //contact.setName(contactText.getText().toString());
                //dbHelper.updateContact(db,contact,(int)id);
                updateAdapter();

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
        super.onListItemClick(l, v, position, id);
    }


      public void onClickAdd(View view){
        onListItemClick(null,null,-1,-1);
      }
      private void updateAdapter() {

          ContactDatabaseHelper helper = new ContactDatabaseHelper(this);
          db = helper.getReadableDatabase();
          cursor =  helper.getDBContactsCursor(db);
          adapter.changeCursor(cursor);
      }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(canReadContacts()){
            Toast toast = Toast.makeText(this, "Can read contacts", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(this, "read contacts denied", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean canReadContacts() {

        return(hasPermission(Manifest.permission.READ_CONTACTS));

    }

    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }

}
