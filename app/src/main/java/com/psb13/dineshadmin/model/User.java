package com.psb13.dineshadmin.model;

/**
 * Created by prabhudeepsingh on 20/03/18.
 */

public class User {
    public String name;
    public String email;
    public String password;
    public String contact;
    public String gender;

    public User() {
    }

    public User(String name, String email, String password, String contact, String gender) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.gender = gender;
    }
}
