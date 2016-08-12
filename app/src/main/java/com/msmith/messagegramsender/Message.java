package com.msmith.messagegramsender;

/**
 * Created by morgan on 8/9/16.
 */
public class Message {
    private String name;
    private String msg;

     public Message() { name=""; msg="";}

 public Message(String name,String msg){
     this.name = name;
     this.msg = msg;
 }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
