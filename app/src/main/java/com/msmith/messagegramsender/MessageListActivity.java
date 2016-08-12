package com.msmith.messagegramsender;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
 * Created by morgan on 8/9/16.
 */
public class MessageListActivity extends ListActivity {

    private SQLiteDatabase db;
    private Cursor cursor;
    private LayoutInflater inflater;
    private CursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        try {
            ContactDatabaseHelper helper = new ContactDatabaseHelper(this);
            db = helper.getReadableDatabase();
            cursor = helper.getDBMessageCursor(db);
            ListView listMessages = getListView();

            adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, new String[]{"name", "msg"}, new int[]{android.R.id.text1, android.R.id.text2}, 0);
            listMessages.setAdapter(adapter);
        } catch (SQLiteException e) {

            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
        @Override
        protected void onListItemClick(ListView l, View v, int position, final long id) {
            inflater = getLayoutInflater();
            View content = inflater.inflate(R.layout.mydialog,null);

            final ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(this);
            db = dbHelper.getWritableDatabase();
            final Message message = dbHelper.getMessage(db,(int)id);
            final EditText aliasText = (EditText)content.findViewById(R.id.alias);
            final EditText contactText = (EditText)content.findViewById(R.id.contact);

            if(id >=0) {
                aliasText.setText(message.getName());
                contactText.setText(message.getMsg());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(content);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    message.setName(aliasText.getText().toString());
                    message.setMsg(contactText.getText().toString());
                    dbHelper.updateMessage(db,message,(int)id);
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
        cursor =  helper.getDBMessageCursor(db);
        adapter.changeCursor(cursor);
    }

    }

