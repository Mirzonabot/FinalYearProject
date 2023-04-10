package com.example.mobileapp;

public class NotificationRequest {
    private String to;
    private NotificationData notification;
    private DataPayload data;

    // Getters, setters, and constructors

    public NotificationRequest(String to, NotificationData notification, DataPayload data) {
        this.to = to;
        this.notification = notification;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public NotificationData getNotification() {
        return notification;
    }

    public void setNotification(NotificationData notification) {
        this.notification = notification;
    }

    public DataPayload getData() {
        return data;
    }

    public void setData(DataPayload data) {
        this.data = data;
    }
}
