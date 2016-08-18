package com.msmith.messagegramsender;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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

/**
 * Created by morgan on 8/9/16.
 */
public class MessageListActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private Cursor cursor;
    private CursorAdapter adapter;
    private AdapterView.OnItemClickListener itemClickListener;
    private ListView messagesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        try {
            ContactDatabaseHelper helper = new ContactDatabaseHelper(this);
            db = helper.getReadableDatabase();
            cursor = helper.getDBMessageCursor(db);
            messagesList = (ListView) findViewById(R.id.messages_list);

            adapter = new SimpleCursorAdapter(this, R.layout.message_list_item, cursor, new String[]{"name", "msg"}, new int[]{R.id.message_name}, 0);
            messagesList.setAdapter(adapter);
        } catch (SQLiteException e) {

            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }


        itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                doClick(messagesList, view, position, id);
            }
        };
        messagesList.setOnItemClickListener(itemClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.messages_menu, menu);
        getSupportActionBar().setTitle("Messages");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ToolBarActivityLauncher.handleToolbarSelection(this, item.getItemId());
        return true;


    }

    protected void doClick(ListView l, View v, int position, final long id) {
        ;
        View content = getLayoutInflater().inflate(R.layout.edit_message, null);

        final ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        final Message message = dbHelper.getMessage(db, (int) id);
        final EditText messageNameText = (EditText) content.findViewById(R.id.message_name);
        final EditText messageText = (EditText) content.findViewById(R.id.message_text);

        if (id >= 0) {
            messageNameText.setText(message.getName());
            messageText.setText(message.getMsg());

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(content);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String messageName = messageNameText.getText().toString();
                String msg = messageText.getText().toString();

                if (messageName == null || messageName.length() == 0 || msg == null || msg.length() == 0) {

                    Toast toast = Toast.makeText(MessageListActivity.this, "Error : empty field(s)", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                message.setName(messageNameText.getText().toString());
                message.setMsg(messageText.getText().toString());
                dbHelper.updateMessage(db, message, (int) id);
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

    }

    public void onClickAdd(View view) {
        doClick(null, null, -1, -1);
    }

    public void onClickDelete(View view) {

        final List<Integer> ids = getChecked();
        if(ids.size() == 0){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MessageListActivity.this);
        builder.setMessage("Delete checked messages?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doDelete(ids);
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

    private List<Integer> getChecked() {
        ListView listView = messagesList;
        int n = listView.getChildCount();

        List<Integer> ids = new ArrayList<Integer>();

        for (int i = 0; i < n; i++) {
            View listItemView = listView.getChildAt(i);
            CheckBox cbView = (CheckBox) listItemView.findViewById(R.id.cbdel);
            Cursor item = (Cursor) listView.getAdapter().getItem(i);
            int id = item.getInt(0);
            if (cbView.isChecked()) {
                ids.add(id);
            }
        }
        return ids;

    }
    private void doDelete(List<Integer> ids) {


        ContactDatabaseHelper helper = new ContactDatabaseHelper(this);
        db = helper.getWritableDatabase();


        for (Integer idx : ids) {
            helper.deleteMessage(db, idx);
        }

            updateAdapter();

    }

    private void updateAdapter() {

        ContactDatabaseHelper helper = new ContactDatabaseHelper(this);
        db = helper.getReadableDatabase();
        cursor = helper.getDBMessageCursor(db);
        adapter.changeCursor(cursor);
    }

}

