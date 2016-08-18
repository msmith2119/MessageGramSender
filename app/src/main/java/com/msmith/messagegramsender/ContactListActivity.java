package com.msmith.messagegramsender;

import android.Manifest;
import android.app.Activity;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by morgan on 8/6/16.
 */
public class ContactListActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private Cursor cursor;
    private LayoutInflater  inflater;
    private CursorAdapter adapter;
    private ContactCallbackHandler searchHandler;
    private ListView contactsView;
    AdapterView.OnItemClickListener itemClickListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactsView = (ListView) findViewById(R.id.list_contacts);
        try {
            ContactDatabaseHelper helper = new ContactDatabaseHelper(this);
            db = helper.getReadableDatabase();
            cursor = helper.getDBContactsCursor(db);

            //CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, new String[]{"name"}, new int[]{android.R.id.text1}, 0);
            adapter = new SimpleCursorAdapter(this, R.layout.contacts_list_item, cursor, new String[]{"alias", "name"}, new int[]{android.R.id.text1, android.R.id.text2}, 0);
            contactsView.setAdapter(adapter);
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }


        if(!canReadContacts())
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},1223);


        itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                doClick(contactsView,view,position,id);
            }
        };

        contactsView.setOnItemClickListener(itemClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contacts_menu, menu);
        getSupportActionBar().setTitle("Contacts");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ToolBarActivityLauncher.handleToolbarSelection(this,item.getItemId());
        return true;



    }

    protected void doClick(ListView parent,View view, int position,final long id){

        View content = getLayoutInflater().inflate(R.layout.edit_contact,null);
        searchHandler = new ContactCallbackHandler(content,getLoaderManager(),getContentResolver());
        searchHandler.init();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(content);
        final ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        final Contact contact = dbHelper.getContact(db,(int)id);
        final EditText aliasText = (EditText)content.findViewById(R.id.alias);
        final TextView contactNameText = (TextView)content.findViewById(R.id.contact_name);
        final TextView phoneText = (TextView)content.findViewById(R.id.number);


        if(id >0 ){
            Log.v("contact","contact_id="+contact.getContactId());
            Map<String,String> contactInfo = ContactUtils.getContactDetail( this,contact.getContactId());
            String name = contactInfo.get("contactName");
            String number = contactInfo.get("contactNumber");
            aliasText.setText(contact.getAlias());
            contactNameText.setText(name);
            phoneText.setText(number);
            Log.v("doClick","name="+name);


        }
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(aliasText.getText().toString().length() == 0){
                    Toast toast = Toast.makeText(ContactListActivity.this, "Error:empty alias", Toast.LENGTH_SHORT);
                    toast.show();
                    return;

                }
                if(id>0){

             contact.setAlias(aliasText.getText().toString());
             if(searchHandler.getContactKey() != null){
                 contact.setName(searchHandler.getContactName());
                 Log.v("contact","contactName="+searchHandler.getContactName());
                 Log.v("contact","contactId="+searchHandler.getContactName());
                 contact.setContactId(searchHandler.getContactKey());
             }
                    dbHelper.updateContact(db,contact,(int)id);
                }

                else {
                    if(aliasText.getText().toString().length() == 0 || searchHandler.getContactKey() == null){
                        Toast toast = Toast.makeText(ContactListActivity.this, "Error:empty contact, ", Toast.LENGTH_SHORT);
                        toast.show();
                        return;

                    }
                    Log.v("contact","contactName="+searchHandler.getContactName());
                    Log.v("contact","contactId="+searchHandler.getContactName());
                    contact.setAlias(aliasText.getText().toString());
                    contact.setName(searchHandler.getContactName());
                    contact.setContactId(searchHandler.getContactKey());
                    Log.v("contact","setting contact="+contact.getContactId());
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
       // super.onListItemClick(l, v, position, id);
    }


      public void onClickAdd(View view){
        itemClickListener.onItemClick(null,null,-1,-1);
      }
    public void onClickDelete(View view){


        AlertDialog.Builder builder = new AlertDialog.Builder(ContactListActivity.this);
        builder.setMessage("Delete checked contacts?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 doDelete();
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

         private void doDelete() {
        ListView listView =   contactsView;

        int n = listView.getChildCount();

        ContactDatabaseHelper helper = new ContactDatabaseHelper(this);
        db  = helper.getWritableDatabase();
        boolean edited = false;
             List<Integer> ids = new ArrayList<Integer>();
        for(int i = 0; i <n;i++) {
            View listItemView = listView.getChildAt(i);
            CheckBox cbView = (CheckBox) listItemView.findViewById(R.id.cbdel);
            Cursor item = (Cursor) listView.getAdapter().getItem(i);
            int id = item.getInt(0);
            if (cbView.isChecked()) {
                ids.add(id);
            }
           }

            for ( Integer idx : ids){
                edited=true;
                helper.deleteContact(db,idx);
            }


            if(edited)
                updateAdapter();
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
