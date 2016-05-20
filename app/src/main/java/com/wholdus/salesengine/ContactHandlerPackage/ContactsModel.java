package com.wholdus.salesengine.ContactHandlerPackage;

/**
 * Created by kaustubh on 19/5/16.
 */
public class ContactsModel {

    public int id;
    public String contactName;
    public String contactNumber;

    public ContactsModel(int id, String contactName, String contactNumber){
        this.id = id;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getContactName(){
        return this.contactName;
    }

    public void setContactName(String contactName){
        this.contactName = contactName;
    }

    public String getContactNumber(){
        return this.contactNumber;
    }

    public void setContactNumber(String contactNumber){
        this.contactNumber = contactNumber;
    }
}
