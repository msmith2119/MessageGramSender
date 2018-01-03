package com.msmith.messagegramsender;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by morgan on 8/6/16.
 */


public class ContactDatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "custdialog";
    public static final int DB_VERION = 1;

    public static final String CREATE_CONTACTS_SQL = "create table contact(_id integer primary key autoincrement, alias text, name text, contact_id text);";
    public static final String CREATE_MESSAGE_SQL = "create table message(_id integer primary key autoincrement, name text, msg  text);";
    public static final String CREATE_PACKET_SQL = "create table packet(_id integer primary key autoincrement,name text, alias_id integer,message_id text);";

    public ContactDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERION);
    }

    public Cursor getDBContactsCursor(SQLiteDatabase db) {


        Cursor cursor = db.query("contact", new String[]{"_id", "alias", "name", "contact_id"}, null, null, null, null, null);
        return cursor;

    }

    public Cursor getDBPacketCursor(SQLiteDatabase db) {


        Cursor cursor = db.query("packet", new String[]{"_id", "name", "alias_id", "message_id"}, null, null, null, null, null);
        return cursor;

    }

    public Cursor getDBMessageCursor(SQLiteDatabase db) {


        Cursor cursor = db.query("message", new String[]{"_id", "name", "msg"}, null, null, null, null, null);
        return cursor;

    }

    public long insertPacket(SQLiteDatabase db, Packet packet) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", packet.getName());
        contentValues.put("alias_id", packet.getAlias_id());
        contentValues.put("message_id", packet.getMessage_id());

        return db.insert("packet", null, contentValues);
    }

    private long insertContact(SQLiteDatabase db, Contact contact) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("alias", contact.getAlias());
        contentValues.put("name", contact.getName());
        contentValues.put("contact_id", contact.getContactId());

        return db.insert("contact", null, contentValues);
    }

    private long insertMessage(SQLiteDatabase db, Message message) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", message.getName());
        contentValues.put("msg", message.getMsg());

        return db.insert("message", null, contentValues);
    }

    public Contact getContact(SQLiteDatabase db, int pos) {

        if (pos < 0) {
            return new Contact();
        }

        Contact contact = null;
        Cursor cursor = db.query("contact", new String[]{"alias", "name", "contact_id"}, "_id=?", new String[]{Integer.toString(pos)}, null, null, null);
        if (cursor.moveToFirst()) {
            String alias = cursor.getString(0);
            String name = cursor.getString(1);
            String contactId = cursor.getString(2);


            contact = new Contact(alias, name, contactId);
        }

        return contact;

    }

    public Message getMessage(SQLiteDatabase db, int pos) {


        if (pos < 0) {
            return new Message();
        }
        Message message = null;
        Cursor cursor = db.query("message", new String[]{"name", "msg"}, "_id=?", new String[]{Integer.toString(pos)}, null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            String msg = cursor.getString(1);
            message = new Message(name, msg);
        }

        return message;

    }

    public Packet getPacket(SQLiteDatabase db, int pos) {


        if (pos < 0) {
            return new Packet();
        }
        Packet packet = null;
        Cursor cursor = db.query("packet", new String[]{"_id", "name", "alias_id", "message_id"}, "_id=?", new String[]{Integer.toString(pos)}, null, null, null);
        if (cursor.moveToFirst()) {
            int p_id = cursor.getInt(0);
            String name = cursor.getString(1);
            int alias_id = cursor.getInt(2);
            int message_id = cursor.getInt(3);
            packet = new Packet(p_id, name, alias_id, message_id);
        }

        return packet;

    }

    public List<Packet> getPacketsByAlias(SQLiteDatabase db, int id) {
        ArrayList<Packet> packets = new ArrayList<Packet>();
        Cursor cursor = db.query("packet", new String[]{"_id", "name", "alias_id", "message_id"}, "alias_id=?", new String[]{Integer.toString(id)}, null, null, null);
        while (cursor.moveToNext()) {
            int p_id = cursor.getInt(0);
            String name = cursor.getString(1);
            int alias_id = cursor.getInt(2);
            int message_id = cursor.getInt(3);
            Packet packet = new Packet(p_id, name, alias_id, message_id);
            packets.add(packet);
        }
        cursor.close();
        return packets;
    }

    public List<Packet> getPacketsByMessage(SQLiteDatabase db, int id) {
        ArrayList<Packet> packets = new ArrayList<Packet>();
        Cursor cursor = db.query("packet", new String[]{"_id", "name", "alias_id", "message_id"}, "message_id=?", new String[]{Integer.toString(id)}, null, null, null);
        while (cursor.moveToNext()) {
            int p_id = cursor.getInt(0);
            String name = cursor.getString(1);
            int alias_id = cursor.getInt(2);
            int message_id = cursor.getInt(3);
            Packet packet = new Packet(p_id, name, alias_id, message_id);
            packets.add(packet);
        }
        cursor.close();
        return packets;
    }

    public boolean hasPacket(SQLiteDatabase db, int alias_id_in, int message_id_in) {

        Cursor cursor = db.query("packet", new String[]{"name", "alias_id", "message_id"}, "alias_id=? and message_id=?", new String[]{Integer.toString(alias_id_in), Integer.toString(message_id_in)}, null, null, null);
        return cursor.getCount() > 0;


    }

    public void deleteContact(SQLiteDatabase db, int id) {


        db.delete("contact", "_id = ?", new String[]{Integer.toString(id)});
        List<Packet> packets = getPacketsByAlias(db, id);
        for (Packet packet : packets) {
            deletePacket(db, packet.getId());
        }

    }

    public void deleteMessage(SQLiteDatabase db, int id) {


        db.delete("message", "_id = ?", new String[]{Integer.toString(id)});
        List<Packet> packets = getPacketsByMessage(db, id);
        for (Packet packet : packets) {
            deletePacket(db, packet.getId());
        }

    }

    public void deletePacket(SQLiteDatabase db, int id) {


        db.delete("packet", "_id = ?", new String[]{Integer.toString(id)});


    }

    public void updateContact(SQLiteDatabase db, Contact contact, int id) {
        if (id < 0) {
            insertContact(db, contact);
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("alias", contact.getAlias());
        contentValues.put("name", contact.getName());
        contentValues.put("contact_id", contact.getContactId());

        db.update("contact", contentValues, "_id = ?", new String[]{Integer.toString(id)});
    }

    public void updateMessage(SQLiteDatabase db, Message message, int id) {

        if (id < 0) {
            insertMessage(db, message);
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", message.getName());
        contentValues.put("msg", message.getMsg());


        db.update("message", contentValues, "_id = ?", new String[]{Integer.toString(id)});
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL(CREATE_CONTACTS_SQL);
            db.execSQL(CREATE_MESSAGE_SQL);
            db.execSQL(CREATE_PACKET_SQL);


            Message message = new Message("late", "Running late. Will be there soon");
            insertMessage(db, message);
            message.setName("onway");
            message.setMsg("On My Way");
            insertMessage(db,message);
            message.setName("think");
            message.setMsg("Thinking of you");
            insertMessage(db,message);


        }

        if (oldVersion < 2) {
            db.execSQL("alter table contact add column vip numeric");
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }
}
