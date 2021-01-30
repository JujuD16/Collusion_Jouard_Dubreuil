package com.example.app1;

/**
 * This class permits to stock an id and a name related to a contact
 * This class is used in MainActivity to store the contact info recovered
 */

import java.util.List;

public class ContactInfo {

    private String id;
    private String name;

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
