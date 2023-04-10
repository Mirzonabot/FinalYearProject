package com.example.mobileapp;

public class TokenUserID {
    private String token;
    private String userID;

    public TokenUserID(String token, String userID) {
        this.token = token;
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "TokenUserID{" +
                "token='" + token + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}
