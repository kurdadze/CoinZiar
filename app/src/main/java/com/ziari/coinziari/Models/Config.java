package com.ziari.coinziari.Models;

/**
 * Created by soso on 1/11/2017.
 */

public class Config {

    public String LngName;
    public String UserName;
    public Boolean Autorised;
    public String Token;
    public int UserID ;
    public String FirstLastName;
    public int Version;

    public String getUserName() {
        return UserName;
    }
    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getLngName() {
        return LngName;
    }
    public void setLngName(String lngName) {
        LngName = lngName;
    }

    public Boolean getAutorised() {
        return Autorised;
    }
    public void setAutorised(Boolean autorised) {
        Autorised = autorised;
    }

    public String getToken() {
        return Token;
    }
    public void setToken(String token) {
        this.Token = token;
    }

    public int getUserID() {
        return UserID;
    }
    public void setUserID(int userID) {
        this.UserID = userID;
    }

    public String getFirstLastName() {
        return FirstLastName;
    }
    public void setFirstLastName(String firstLastName) {
        FirstLastName = firstLastName;
    }

    public int getVersion() {return Version;}
    public void setVersion(int version) {Version = version;}
}
