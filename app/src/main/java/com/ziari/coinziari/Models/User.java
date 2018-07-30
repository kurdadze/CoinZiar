package com.ziari.coinziari.Models;

/**
 * Created by soso on 1/11/2017.
 */

public class User {
    public int ID ;
    public String Email;
    public String FirstName;
    public String LastName;
    public String Token;

    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return Email;
    }
    public void setEmail(String email) {
        Email = email;
    }

    public String getFirstName() {
        return FirstName;
    }
    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }
    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getToken() {
        return Token;
    }
    public void setToken(String token) {
        Token = token;
    }
}
