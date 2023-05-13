package com.example.mobileapp.smsmanager;

import android.telephony.SmsManager;

import androidx.fragment.app.FragmentActivity;

import com.example.mobileapp.dbclasses.Booking;
import com.example.mobileapp.dbclasses.Homestay;

public class SMSSender {
    private static final SmsManager smsManager = SmsManager.getDefault();
    private static final String serverForwardsms = "+358454909638";
    private static final String severForwardsmsUSA = "+15076801067";
//    private static String phoneNumber = "+996552328280";


    public static void sendSMS(String message,String phoneNumber) {
        System.out.println("message: "+message);
        System.out.println("phone number: " + phoneNumber);

        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public static void smsNewBooking(Booking booking, Homestay homestay){
        String message = "nb:" + booking.getBookingID()+ "," + booking.getHomestayID() + "," + booking.getUserID()+ ","+ booking.getCheckInDate() + "," + booking.getCheckOutDate()+","+homestay.getHomestayName()+","+booking.getNameBookedBy();
        System.out.println("smsNewBooking: " + message);
        System.out.println("phone number: " + homestay.getHomestayName());
        System.out.println("phone number: " + homestay.getHomestayPhoneNumber());
        sendSMS(message, homestay.getHomestayPhoneNumber());
    }

    public static void smsCancelBooking(Booking booking){
        String message = "cbc:" + booking.getBookingID();
        sendSMS(message,null);
    }

    public static void smsAcceptBooking(Booking booking){
        String message = "ab:" + booking.getBookingID();
        System.out.println("smsAcceptBooking: " + message);
        System.out.println("phone number: " + booking.getPhoneNumberBooker());
        sendSMS(message,booking.getPhoneNumberBooker());
    }

    public static void smsRejectBooking(Booking booking){
        String message = "rb:" + booking.getBookingID();
        sendSMS(message,null);
    }


    public static void getHomestaysFromServer(FragmentActivity activity, String sortBy, String addres, String byKmm, String latitude, String longitude) {
        String message = "ghfs:" + sortBy + "," + addres + "," + byKmm + "," + latitude + "," + longitude;
        sendSMS(message,severForwardsmsUSA);
    }
}
