package com.msmith.messagegramsender;

/**
 * Created by morgan on 8/9/16.
 */
public class Packet {

    private  int id;
    private  String name;
    private int alias_id;
    private int message_id;

    public Packet() { this.name = "";id=-1; alias_id = -1; message_id =-1;}

    public Packet(int id,String name, int  alias_id, int message_id){
        this.id = id;
        this.name = name;
        this.alias_id=alias_id;
        this.message_id=message_id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAlias_id() {
        return alias_id;
    }

    public void setAlias_id(int alias_id) {
        this.alias_id = alias_id;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
