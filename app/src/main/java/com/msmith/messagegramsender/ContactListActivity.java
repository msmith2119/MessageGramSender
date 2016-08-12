package com.msmith.messagegramsender;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
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
import android.widget.Toast;




/**
 * Created by morgan on 8/6/16.
 */
public class ContactListActivity extends ListActivity  {

    private SQLiteDatabase db;
    private Cursor cursor;
    private LayoutInflater  inflater;
    private CursorAdapter adapter;



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
        View content = inflater.inflate(R.layout.mydialog,null);

        final ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        final Contact contact = dbHelper.getContact(db,(int)id);
        final EditText aliasText = (EditText)content.findViewById(R.id.alias);
        final EditText contactText = (EditText)content.findViewById(R.id.contact);
        Log.v("onclick","id="+id);
        if(id >=0) {
            aliasText.setText(contact.getAlias());
            contactText.setText(contact.getName());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(content);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                contact.setAlias(aliasText.getText().toString());
                contact.setName(contactText.getText().toString());
                dbHelper.updateContact(db,contact,(int)id);
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


}
