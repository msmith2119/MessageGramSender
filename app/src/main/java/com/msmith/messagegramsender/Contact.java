package com.msmith.messagegramsender;

/**
 * Created by morgan on 8/8/16.
 */
public class Contact {
    private String alias;
    private String name;
    private String contactId;

    public Contact() {alias="";name="";contactId="";}


    public Contact(String alias, String name,String contactId){
        this.alias=alias;
       this.name = name;
        this.contactId=contactId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
}
