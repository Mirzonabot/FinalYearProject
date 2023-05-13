package com.example.mobileapp.smsmanager;

import android.content.Context;

import com.example.mobileapp.dbclasses.Booking;
import com.example.mobileapp.dbclasses.Homestay;
import com.example.mobileapp.memorymanager.SqlHelper;

public class SMSParser {

    public static void parseSMS(String message, String phoneNumber, Context context){
        System.out.println("parseSMS: " + message);
        System.out.println("parseSMS: " + phoneNumber);
        String[] messageParts = message.split(":");
        String messageType = messageParts[0];
        String messageBody = messageParts[1];
        switch (messageType){
            case "nb":
                createNewBooking(messageBody, phoneNumber,context);
                break;
            case "ab":
                wasAccepted(messageBody, phoneNumber,context);
                break;
            case "h":
                newHomestays(messageBody, phoneNumber,context);
//            case "ab":
//                parseAcceptBooking(messageBody);
//                break;
//            case "rb":
//                parseRejectBooking(messageBody);
//                break;
        }
    }

    private static void newHomestays(String messageBody, String phoneNumber, Context context) {
        String[] messageList = messageBody.split("\n");
        for (String message: messageList) {
            String[] homestay = message.split(",");
            SqlHelper sqlHelper = new SqlHelper(context, null, null);
            Homestay newHomestay = new Homestay();
            newHomestay.setId(homestay[0]);
            newHomestay.setHomestayName(homestay[1]);
            newHomestay.setHomestayCapacity(Integer.parseInt(homestay[2]));
            newHomestay.setOwnerId(homestay[3]);
            newHomestay.setAddress(homestay[4]);
            newHomestay.setLatitude(homestay[5]);
            newHomestay.setLongitude(homestay[6]);
            newHomestay.setVillage(homestay[7]);
            newHomestay.setDistrict(homestay[8]);
            newHomestay.setCity(homestay[9]);
            newHomestay.setStreet(homestay[10]);
            newHomestay.setHomestayPhoneNumber(homestay[11]);
            if (!sqlHelper.homestayIsInOfflineTable(newHomestay.getId())){
                sqlHelper.addHomestayToOfflineTable(newHomestay);
            }



//                    address + ',' +
//                    latitude + ',' +
//                    longitude + ',' +
//                    availability + ',' +
//                    village + ',' +
//                    district + ',' +
//                    city + ',' +
//                    street + ',' +
//                    homestayPhoneNumber;
        }
    }

    private static void wasAccepted(String messageBody, String phoneNumber, Context context) {
        SqlHelper sqlHelper = new SqlHelper(context,null,null);
        sqlHelper.acceptBookingInOtherHomestay(messageBody);

    }

    private static void createNewBooking(String messageBody, String phoneNumber, Context context){
        String[] messageParts = messageBody.split(",");
        System.out.println("createNewBooking: " + messageBody);

//String checkInDate, String checkOutDate, String nameBookedBy,  String userID,String homestayID,String homestayName, String ownerID, String phoneNumberBooker
//    String message = "nb:" + booking.getBookingID()+ "," + booking.getHomestayID() + "," + booking.getUserID()+ ","+ booking.getCheckInDate() + "," + booking.getCheckOutDate()+","+homestay.getHomestayName()+","+booking.getNameBookedBy();

        Booking booking = new Booking();
        booking.setBookingID(messageParts[0]);
        booking.setHomestayID(messageParts[1]);
        booking.setUserID(messageParts[2]);
        booking.setCheckInDate(messageParts[3]);
        booking.setCheckOutDate(messageParts[4]);
        booking.setHomestayName(messageParts[5]);
        booking.setNameBookedBy(messageParts[6]);
        booking.setPhoneNumberBooker(phoneNumber);

        SqlHelper sqlHelper = new SqlHelper(context,null,null);
        sqlHelper.createBookingInMyHomestay(booking);
    }
}
