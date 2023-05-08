package com.example.mobileapp.memorymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mobileapp.DistanceBetweenLocations;
import com.example.mobileapp.dbclasses.Booking;
import com.example.mobileapp.dbclasses.Homestay;

import java.util.ArrayList;
import java.util.List;

public class SqlHelper extends SQLiteOpenHelper {
    Context context;
    private static final String DATABASE_NAME = "homestays.db";
    private static final int DATABASE_VERSION = 1;
    private static final String MY_HOMESTAYS_TABLE = "my_homestays";
    private static final String OFFLINE_HOMESTAYS_TABLE = "offline_homestays";
    private static final String MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE = "my_bookings";
    private static final String BOOKINGS_IN_MY_HOMESTAYS_TABLE = "bookings_in_my_homestays";


    public SqlHelper(@Nullable Context  context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MY_HOMESTAYS_TABLE + " (homestay_id INTEGER, homestayName TEXT, homestayCapacity INTEGER, ownerId TEXT, address TEXT, latitude TEXT, longitude TEXT, availability BOOLEAN, village TEXT, district TEXT, city TEXT, street TEXT, homestayPhoneNumber TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + OFFLINE_HOMESTAYS_TABLE + " (homestay_id INTEGER, homestayName TEXT, homestayCapacity INTEGER, ownerId TEXT, address TEXT, latitude TEXT, longitude TEXT, availability BOOLEAN, village TEXT, district TEXT, city TEXT, street TEXT, homestayPhoneNumber TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE + " (bookingID TEXT, checkInDate TEXT, checkOutDate TEXT, nameBookedBy TEXT, homestayID TEXT, homestayName TEXT, userID TEXT, ownerID TEXT, booked BOOLEAN, rejected BOOLEAN, isCancelled BOOLEAN, timestampLong REAL, phoneNumberBooker TEXT, phoneNumberHomestayOwner TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + BOOKINGS_IN_MY_HOMESTAYS_TABLE +" (bookingID TEXT, checkInDate TEXT, checkOutDate TEXT, nameBookedBy TEXT, homestayID TEXT, homestayName TEXT, userID TEXT, ownerID TEXT, booked BOOLEAN, rejected BOOLEAN, isCancelled BOOLEAN, timestampLong REAL, phoneNumberBooker TEXT, phoneNumberHomestayOwner TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + MY_HOMESTAYS_TABLE);
    }

    public void addHomestay(Homestay homestay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("homestay_id", homestay.getId());
        cv.put("homestayName", homestay.getHomestayName());
        cv.put("homestayCapacity", homestay.getHomestayCapacity());
        cv.put("ownerId", homestay.getOwnerId());
        cv.put("address", homestay.getAddress());
        cv.put("latitude", homestay.getLatitude());
        cv.put("longitude", homestay.getLongitude());
        cv.put("availability", homestay.isAvailability());
        cv.put("village", homestay.getVillage());
        cv.put("district", homestay.getDistrict());
        cv.put("city", homestay.getCity());
        cv.put("street", homestay.getStreet());
        cv.put("homestayPhoneNumber", homestay.getHomestayPhoneNumber());
        long result = db.insert(MY_HOMESTAYS_TABLE, null, cv);
        if (result == -1) {
            System.out.println("Failed to insert homestay");
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("Homestay inserted successfully");
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }

//        db.execSQL("INSERT INTO " + MY_HOMESTAYS_TABLE_NAME + " (homestay_id, homestayName, homestayCapacity, ownerId, address, latitude, longitude, availability, village, district, city, street) VALUES (" + homestay.getId() + ", '" + homestay.getHomestayName() + "', " + homestay.getHomestayCapacity() + ", '" + homestay.getOwnerId() + "', '" + homestay.getAddress() + "', '" + homestay.getLatitude() + "', '" + homestay.getLongitude() + "', " + homestay.isAvailability() + ", '" + homestay.getVillage() + "', '" + homestay.getDistrict() + "', '" + homestay.getCity() + "', '" + homestay.getStreet() + "')");
    }

    public void createBookingInMyHomestay(Booking booking) {
        System.out.println("createBookingInMyHomestay sqlite");
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("createBookingInMyHomestay sqlite 1");
        ContentValues cv = new ContentValues();
        System.out.println("createBookingInMyHomestay sqlite 2.1");
        cv.put("bookingID", booking.getBookingID());
        System.out.println("createBookingInMyHomestay sqlite 2.2");
        cv.put("checkInDate", booking.getCheckInDate());
        System.out.println("createBookingInMyHomestay sqlite 2.3");
        cv.put("checkOutDate", booking.getCheckOutDate());
        System.out.println("createBookingInMyHomestay sqlite 2.4");
        cv.put("nameBookedBy", booking.getNameBookedBy());
        System.out.println("createBookingInMyHomestay sqlite 2.5");
        cv.put("homestayID", booking.getHomestayID());
        System.out.println("createBookingInMyHomestay sqlite 2.6");
        cv.put("homestayName", booking.getHomestayName());
        System.out.println("createBookingInMyHomestay sqlite 2.7");
        cv.put("userID", booking.getUserID());
        System.out.println("createBookingInMyHomestay sqlite 2.8");
        cv.put("ownerID", booking.getOwnerID());
        System.out.println("createBookingInMyHomestay sqlite 2.9");
        cv.put("booked", booking.isBooked());
        System.out.println("createBookingInMyHomestay sqlite 2.10");
        cv.put("rejected", booking.isRejected());
        System.out.println("createBookingInMyHomestay sqlite 2.11");
        cv.put("isCancelled", booking.isCancelled());
        System.out.println("createBookingInMyHomestay sqlite 2.12");
        cv.put("timestampLong", booking.getTimestampLong());
        cv.put("phoneNumberHomestayOwner",booking.getPhoneNumberHomestayOwner());
        cv.put("phoneNumberBooker",booking.getPhoneNumberBooker());

        System.out.println("createBookingInMyHomestay sqlite 2");
        long result = db.insert(BOOKINGS_IN_MY_HOMESTAYS_TABLE, null, cv);
        System.out.println("createBookingInMyHomestay sqlite 3");
        if (result == -1) {
            System.out.println("Failed to insert booking");
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("Booking inserted successfully");
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }
    }

    public void addHomestayToOfflineTable(Homestay homestay){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("homestay_id", homestay.getId());
        cv.put("homestayName", homestay.getHomestayName());
        cv.put("homestayCapacity", homestay.getHomestayCapacity());
        cv.put("ownerId", homestay.getOwnerId());
        cv.put("address", homestay.getAddress());
        cv.put("latitude", homestay.getLatitude());
        cv.put("longitude", homestay.getLongitude());
        cv.put("availability", homestay.isAvailability());
        cv.put("village", homestay.getVillage());
        cv.put("district", homestay.getDistrict());
        cv.put("city", homestay.getCity());
        cv.put("street", homestay.getStreet());
        cv.put("homestayPhoneNumber", homestay.getHomestayPhoneNumber());
        long result = db.insert(OFFLINE_HOMESTAYS_TABLE, null, cv);
        if (result == -1) {
            System.out.println("Failed to insert homestay");
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("Homestay inserted successfully");
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean homestayIsInOfflineTable(String homestayID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + OFFLINE_HOMESTAYS_TABLE + " WHERE homestay_id = '" + homestayID + "'", null);
        if (cursor.getCount() > 0){
            System.out.println("Homestay is in offline table");
            return true;
        }
        return false;
    }

    public void createMyBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("bookingID", booking.getBookingID());
        cv.put("checkInDate", booking.getCheckInDate());
        cv.put("checkOutDate", booking.getCheckOutDate());
        cv.put("nameBookedBy", booking.getNameBookedBy());
        cv.put("homestayID", booking.getHomestayID());
        cv.put("homestayName", booking.getHomestayName());
        cv.put("userID", booking.getUserID());
        cv.put("ownerID", booking.getOwnerID());
        cv.put("booked", booking.isBooked());
        cv.put("rejected", booking.isRejected());
        cv.put("isCancelled", booking.isCancelled());
        cv.put("timestampLong", booking.getTimestampLong());
        cv.put("phoneNumberHomestayOwner",booking.getPhoneNumberHomestayOwner());
        cv.put("phoneNumberBooker",booking.getPhoneNumberBooker());
        long result = db.insert(MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE, null, cv);
        if (result == -1) {
            System.out.println("Failed to insert booking");
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("Booking inserted successfully");
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }
    }

    public void dropAllTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + MY_HOMESTAYS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + BOOKINGS_IN_MY_HOMESTAYS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + OFFLINE_HOMESTAYS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE);
        onCreate(db);
    }


    public List<Homestay> getAllOfflineHomestays() {
        List<Homestay> homestayList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + OFFLINE_HOMESTAYS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Homestay homestay = new Homestay();
                homestay.setId(((Cursor) cursor).getString(0));
                homestay.setHomestayName(cursor.getString(1));
                homestay.setHomestayCapacity(cursor.getInt(2));
                homestay.setOwnerId(cursor.getString(3));
                homestay.setAddress(cursor.getString(4));
                homestay.setLatitude(String.valueOf(cursor.getDouble(5)));
                homestay.setLongitude(String.valueOf(cursor.getDouble(6)));
                homestay.setAvailability(cursor.getInt(7) == 1);
                homestay.setVillage(cursor.getString(8));
                homestay.setDistrict(cursor.getString(9));
                homestay.setCity(cursor.getString(10));
                homestay.setStreet(cursor.getString(11));
                homestayList.add(homestay);
            } while (cursor.moveToNext());
        }
        return homestayList;
    }

    public ArrayList<Homestay> getAllHomestays() {
        ArrayList<Homestay> homestayList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + MY_HOMESTAYS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Homestay homestay = new Homestay();
                homestay.setId(((Cursor) cursor).getString(0));
                homestay.setHomestayName(cursor.getString(1));
                homestay.setHomestayCapacity(cursor.getInt(2));
                homestay.setOwnerId(cursor.getString(3));
                homestay.setAddress(cursor.getString(4));
                homestay.setLatitude(String.valueOf(cursor.getDouble(5)));
                homestay.setLongitude(String.valueOf(cursor.getDouble(6)));
                homestay.setAvailability(cursor.getInt(7) == 1);
                homestay.setVillage(cursor.getString(8));
                homestay.setDistrict(cursor.getString(9));
                homestay.setCity(cursor.getString(10));
                homestay.setStreet(cursor.getString(11));

                homestayList.add(homestay);
            } while (cursor.moveToNext());
        }
        return homestayList;
    }

    public ArrayList<Booking> getAllBookingsInMyHomestays() {
        ArrayList<Booking> bookingList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + BOOKINGS_IN_MY_HOMESTAYS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Booking booking = new Booking();
                booking.setBookingID(cursor.getString(0));
                booking.setCheckInDate(cursor.getString(1));
                booking.setCheckOutDate(cursor.getString(2));
                booking.setNameBookedBy(cursor.getString(3));
                booking.setHomestayID(cursor.getString(4));
                booking.setHomestayName(cursor.getString(5));
                booking.setUserID(cursor.getString(6));
                booking.setOwnerID(cursor.getString(7));
                booking.setBooked(cursor.getInt(8) == 1);
                booking.setRejected(cursor.getInt(9) == 1);
                booking.setCancelled(cursor.getInt(10) == 1);
                booking.setTimestampLong(cursor.getLong(11));
                booking.setPhoneNumberBooker(cursor.getString(12));
                booking.setPhoneNumberHomestayOwner(cursor.getString(13));
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }
        return bookingList;
    }

    public ArrayList<Booking> getAllMyBookingsInOtherHomestays() {
        ArrayList<Booking> bookingList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Booking booking = new Booking();
                booking.setBookingID(cursor.getString(0));
                booking.setCheckInDate(cursor.getString(1));
                booking.setCheckOutDate(cursor.getString(2));
                booking.setNameBookedBy(cursor.getString(3));
                booking.setHomestayID(cursor.getString(4));
                booking.setHomestayName(cursor.getString(5));
                booking.setUserID(cursor.getString(6));
                booking.setOwnerID(cursor.getString(7));
                booking.setBooked(cursor.getInt(8) == 1);
                booking.setRejected(cursor.getInt(9) == 1);
                booking.setCancelled(cursor.getInt(10) == 1);
                booking.setTimestampLong(cursor.getLong(11));
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }
        return bookingList;
    }

    public void deleteOfflineHomestay(String homestayID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(OFFLINE_HOMESTAYS_TABLE, "homestayID = ?", new String[]{homestayID});
        db.close();
    }

    public void deleteHomestay(String homestayID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MY_HOMESTAYS_TABLE, "homestayID = ?", new String[]{homestayID});
        db.close();
    }

    public void deleteBookingInMyHomestay(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BOOKINGS_IN_MY_HOMESTAYS_TABLE, "bookingID = ?", new String[]{bookingID});
        db.close();
    }

    public void deleteMyBookingInOtherHomestay(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE, "bookingID = ?", new String[]{bookingID});
        db.close();
    }

    public void deleteAllOfflineHomestays() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(OFFLINE_HOMESTAYS_TABLE, null, null);
        db.close();
    }

    public void deleteAllHomestays() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MY_HOMESTAYS_TABLE, null, null);
        db.close();
    }

    public void deleteAllBookingsInMyHomestays() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BOOKINGS_IN_MY_HOMESTAYS_TABLE, null, null);
        db.close();
    }

    public void deleteAllMyBookingsInOtherHomestays() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE, null, null);
        db.close();
    }

    public Homestay getOfflineHomestayById(String homestayID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(OFFLINE_HOMESTAYS_TABLE, new String[]{"*"}, "homestay_id" + "=?", new String[]{homestayID}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Homestay homestay = new Homestay();
        homestay.setId(cursor.getString(0));
        homestay.setHomestayName(cursor.getString(1));
        homestay.setHomestayCapacity(cursor.getInt(2));
        homestay.setOwnerId(cursor.getString(3));
        homestay.setAddress(cursor.getString(4));
        homestay.setLatitude(String.valueOf(cursor.getDouble(5)));
        homestay.setLongitude(String.valueOf(cursor.getDouble(6)));
        homestay.setAvailability(cursor.getInt(7) == 1);
        homestay.setVillage(cursor.getString(8));
        homestay.setDistrict(cursor.getString(9));
        homestay.setCity(cursor.getString(10));
        homestay.setStreet(cursor.getString(11));
        homestay.setHomestayPhoneNumber(cursor.getString(12));
        return homestay;
    }

    public Homestay getMyHomestayById(String homestayID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MY_HOMESTAYS_TABLE, new String[]{"*"}, homestayID + "=?", new String[]{homestayID}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Homestay homestay = new Homestay();
        homestay.setId(cursor.getString(0));
        homestay.setHomestayName(cursor.getString(1));
        homestay.setHomestayCapacity(cursor.getInt(2));
        homestay.setOwnerId(cursor.getString(3));
        homestay.setAddress(cursor.getString(4));
        homestay.setLatitude(String.valueOf(cursor.getDouble(5)));
        homestay.setLongitude(String.valueOf(cursor.getDouble(6)));
        homestay.setAvailability(cursor.getInt(7) == 1);
        homestay.setVillage(cursor.getString(8));
        homestay.setDistrict(cursor.getString(9));
        homestay.setCity(cursor.getString(10));
        homestay.setStreet(cursor.getString(11));
        return homestay;
    }

    public Homestay getOfflineHomestayByOwnerId(String ownerID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(OFFLINE_HOMESTAYS_TABLE, new String[]{"*"}, ownerID + "=?", new String[]{ownerID}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Homestay homestay = new Homestay();
        homestay.setId(cursor.getString(0));
        homestay.setHomestayName(cursor.getString(1));
        homestay.setHomestayCapacity(cursor.getInt(2));
        homestay.setOwnerId(cursor.getString(3));
        homestay.setAddress(cursor.getString(4));
        homestay.setLatitude(String.valueOf(cursor.getDouble(5)));
        homestay.setLongitude(String.valueOf(cursor.getDouble(6)));
        homestay.setAvailability(cursor.getInt(7) == 1);
        homestay.setVillage(cursor.getString(8));
        homestay.setDistrict(cursor.getString(9));
        homestay.setCity(cursor.getString(10));
        homestay.setStreet(cursor.getString(11));
        return homestay;
    }

//    public Homestay getMyHomestayByOwnerId(String ownerID){
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(MY_HOMESTAYS_TABLE, new String[]{"*"}, "ownerID" + "=?", new String[]{ownerID}, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//        Homestay homestay = new Homestay();
//        homestay.setId(cursor.getString(cursor.getColumnIndex("id")));
//        homestay.setHomestayName(cursor.getString(cursor.getColumnIndex("homestayName")));
//        homestay.setHomestayCapacity(cursor.getInt(cursor.getColumnIndex("homestayCapacity")));
//        homestay.setOwnerId(cursor.getString(cursor.getColumnIndex("ownerId")));
//        homestay.setAddress(cursor.getString(cursor.getColumnIndex("address")));
//        homestay.setLatitude(String.valueOf(cursor.getDouble(cursor.getColumnIndex("latitude"))));
//        homestay.setLongitude(String.valueOf(cursor.getDouble(cursor.getColumnIndex("longitude"))));
//        homestay.setAvailability(cursor.getInt(cursor.getColumnIndex("availability")) == 1);
//        homestay.setVillage(cursor.getString(cursor.getColumnIndex("village")));
//        homestay.setDistrict(cursor.getString(cursor.getColumnIndex("district")));
//        homestay.setCity(cursor.getString(cursor.getColumnIndex("city")));
//        homestay.setStreet(cursor.getString(cursor.getColumnIndex("street")));
//        return homestay;
//    }

    public Booking getBookingInMyHomestayById(String bookingID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(BOOKINGS_IN_MY_HOMESTAYS_TABLE, new String[]{"*"}, bookingID + "=?", new String[]{bookingID}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        System.out.println("cursor.getString(0) = " + cursor.getString(0));
        System.out.println(cursor);
        System.out.println("____________________________________________________");
        System.out.println("____________________________________________________");
        System.out.println("____________________________________________________");
        System.out.println("____________________________________________________");


        Booking booking = new Booking();
        booking.setBookingID(cursor.getString(0));
        booking.setCheckInDate(cursor.getString(1));
        booking.setCheckOutDate(cursor.getString(2));

        return booking;
    }

    public void acceptBooking(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("booked", true);
        db.update(BOOKINGS_IN_MY_HOMESTAYS_TABLE, values, "bookingID" + "=?", new String[]{bookingID});
    }

    public void acceptBookingInOtherHomestay(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("booked", true);
        db.update(MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE, values, "bookingID" + "=?", new String[]{bookingID});
    }

    public void rejectBookingInMyHomestays(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("rejected", true);
        db.update(BOOKINGS_IN_MY_HOMESTAYS_TABLE, values, "bookingID" + "=?", new String[]{bookingID});
    }

    public void rejectBookingInOtherHomestays(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("rejected", true);
        db.update(MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE, values, "bookingID" + "=?", new String[]{bookingID});
    }

    public void cancelBookingInMyHomestays(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isCancelled", true);
        db.update(BOOKINGS_IN_MY_HOMESTAYS_TABLE, values, "bookingID" + "=?", new String[]{bookingID});
    }

    public void cancelMyBookingInOtherHomestays(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isCancelled", true);
        db.update(MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE, values, "bookingID" + "=?", new String[]{bookingID});
    }


    public List<Homestay> inTheRange(Double valueOf, double latitude, double longitude) {

        List<Homestay> homestays = new ArrayList<>();

        for (Homestay homestay : getAllHomestays()) {
            if (DistanceBetweenLocations.distance(latitude, longitude, Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()),'K') <= valueOf) {
                homestays.add(homestay);
            }
        }

        return homestays;


    }
}
