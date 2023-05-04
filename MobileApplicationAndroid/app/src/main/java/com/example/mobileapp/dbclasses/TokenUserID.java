package com.example.mobileapp.dbclasses;

public class TokenUserID {

    private String userID;
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String token;
    private boolean phoneVerified;
    private boolean userHasInternet;

    public TokenUserID(String userID, String userName, String userEmail, String userPhoneNumber, String token) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.token = token;
        this.phoneVerified = false;
        this.userHasInternet = true;
    }

    public TokenUserID() {
    }

    public TokenUserID(String token, String userID) {
        this.token = token;
        this.userID = userID;
        this.userHasInternet = true;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public boolean isUserHasInternet() {
        return userHasInternet;
    }

    public void setUserHasInternet(boolean userHasInternet) {
        this.userHasInternet = userHasInternet;
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
                "userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPhoneNumber='" + userPhoneNumber + '\'' +
                ", token='" + token + '\'' +
                ", phoneVerified=" + phoneVerified +
                ", userHasInternet=" + userHasInternet +
                '}';
    }
}
