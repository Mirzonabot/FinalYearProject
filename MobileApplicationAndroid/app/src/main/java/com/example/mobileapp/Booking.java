package com.example.mobileapp;

import java.util.UUID;

public class Booking {
    private String bookingID;
    private String checkInDate;
    private String checkOutDate;
    private String nameBookedBy;
    private String homestayID;
    private String userID;
    private String ownerID;
    private boolean booked;
    private boolean rejected;
    private boolean isCancelled;

    public Booking() {
    }
    public Booking( String checkInDate, String checkOutDate, String nameBookedBy,  String userID,String homestayID, String ownerID) {
        this.bookingID = UUID.randomUUID().toString();
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.userID = userID;
        this.homestayID = homestayID;
        this.ownerID = ownerID;
        this.booked = false;
        this.rejected = false;
        this.nameBookedBy = nameBookedBy;
        this.isCancelled = false;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public String getNameBookedBy() {
        return nameBookedBy;
    }

    public void setNameBookedBy(String nameBookedBy) {
        this.nameBookedBy = nameBookedBy;
    }

    public String getBookingID() {
        return bookingID;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getHomestayID() {
        return homestayID;
    }

    public void setHomestayID(String homestayID) {
        this.homestayID = homestayID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }
}
