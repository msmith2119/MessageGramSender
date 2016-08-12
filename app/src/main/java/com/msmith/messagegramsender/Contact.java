package com.msmith.messagegramsender;

/**
 * Created by morgan on 8/8/16.
 */
public class Contact {
    private String alias;
    private String  address;

    public Contact() {alias="";address="";}
    public Contact(String alias,String address){
        this.alias=alias;
        this.address=address;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
