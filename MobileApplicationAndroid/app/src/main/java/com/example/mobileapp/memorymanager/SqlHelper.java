package com.example.mobileapp.memorymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.mobileapp.CustomInfoWindow;
import com.example.mobileapp.DistanceBetweenLocations;
import com.example.mobileapp.GPSTracker;
import com.example.mobileapp.MarkerCustomized;
import com.example.mobileapp.dbclasses.Booking;
import com.example.mobileapp.dbclasses.Homestay;
import com.example.mobileapp.homepages.CustomerHome;
import com.example.mobileapp.homepages.Home;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

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
    private MapView mapView;
    private FragmentManager supportFragmentManager;
    private Location myLocation;
    private CustomerHome customerHome;


    public SqlHelper(Context context, MapView mapView, FragmentManager supportFragmentManager) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.mapView = mapView;
        this.supportFragmentManager = supportFragmentManager;
        myLocation = new GPSTracker(context).getLocation();
        customerHome = new CustomerHome();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db, MY_HOMESTAYS_TABLE);
        createTable(db, OFFLINE_HOMESTAYS_TABLE);
        createBookingsTable(db, MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE);
        createBookingsTable(db, BOOKINGS_IN_MY_HOMESTAYS_TABLE);

    }

    private void createTable(SQLiteDatabase db, String tableName) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (homestay_id INTEGER, homestayName TEXT, homestayCapacity INTEGER, ownerId TEXT, address TEXT, latitude TEXT, longitude TEXT, availability BOOLEAN, village TEXT, district TEXT, city TEXT, street TEXT, homestayPhoneNumber TEXT)");
    }

    private void createBookingsTable(SQLiteDatabase db, String tableName) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (bookingID TEXT, checkInDate TEXT, checkOutDate TEXT, nameBookedBy TEXT, homestayID TEXT, homestayName TEXT, userID TEXT, ownerID TEXT, booked BOOLEAN, rejected BOOLEAN, isCancelled BOOLEAN, timestampLong REAL, phoneNumberBooker TEXT, phoneNumberHomestayOwner TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + MY_HOMESTAYS_TABLE);
    }


    // My offline homestays table
    public List<Homestay> getAllOfflineHomestaysList() {
        List<Homestay> homestaysOffline = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + OFFLINE_HOMESTAYS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Homestay homestay = createHomestayFromCursor(cursor);
                homestaysOffline.add(homestay);
                
            } while (cursor.moveToNext());
        }
        return homestaysOffline;
    }
    public Homestay getOfflineHomestayById(String homestayID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(OFFLINE_HOMESTAYS_TABLE, new String[]{"*"}, "homestay_id" + "=?", new String[]{homestayID}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Homestay homestay = createHomestayFromCursor(cursor);

        return homestay;
    }
    public void addHomestayToOfflineTable(Homestay homestay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = createContentValuesFromHomestay(homestay);
        long result = db.insert(OFFLINE_HOMESTAYS_TABLE, null, cv);

        if (result == -1) {
            System.out.println("Failed to insert homestay");
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("Homestay inserted successfully");
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }
    }
    public void offlineHomestayInTheRange(Double valueOf) {

        List<Homestay> homestays = new ArrayList<>();

        for (Homestay homestay : getAllOfflineHomestaysList()) {
            if (DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()),'K') <= valueOf) {
                homestays.add(homestay);
            }
        }

        return;


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
    public void setOfflineHomestaysToInterface() {


        List<MarkerCustomized> markers = new ArrayList<>();
        List<Homestay> homestays = getAllOfflineHomestaysList();
        for (Homestay homestay: homestays) {
            GeoPoint point = new GeoPoint(Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()));
             MarkerCustomized marker = new  MarkerCustomized(mapView,homestay);
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(homestay.getHomestayName());
            marker.setSnippet(String.valueOf(DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K')));
            CustomInfoWindow infoWindow = new CustomInfoWindow(context,mapView, supportFragmentManager,homestay);
            marker.setInfoWindow(infoWindow);
            markers.add(marker);
        }
        customerHome.sendData(markers,mapView,myLocation);

        return ;
    }
    public void deleteOfflineHomestay(String homestayID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(OFFLINE_HOMESTAYS_TABLE, "homestayID = ?", new String[]{homestayID});
        db.close();
    }
    public void deleteAllOfflineHomestays() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(OFFLINE_HOMESTAYS_TABLE, null, null);
        db.close();
    }
    public Homestay getOfflineHomestayByOwnerId(String ownerID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(OFFLINE_HOMESTAYS_TABLE, new String[]{"*"}, ownerID + "=?", new String[]{ownerID}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Homestay homestay = createHomestayFromCursor(cursor);
        return homestay;
    }

    public List<Homestay> getHomestaysByAddressList(String searchBy, String address) {
        System.out.println("address: " + address);
        List<Homestay> homestaysAddress = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(OFFLINE_HOMESTAYS_TABLE,new String[]{"*"},searchBy.split(" ")[1]+"=?",new String[]{address},null,null,null,null);

        if (cursor.moveToFirst()) {
            do {
                Homestay homestay = createHomestayFromCursor(cursor);
                homestaysAddress.add(homestay);

            } while (cursor.moveToNext());
        }
        return homestaysAddress;
    }

    public void getHomestaysByAddressMarker(String searchBy, String address){

        List<Homestay> homestays = getHomestaysByAddressList(searchBy,address);
        List< MarkerCustomized> markers = new ArrayList<>();
        for (Homestay homestay: homestays) {
            GeoPoint point = new GeoPoint(Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()));
             MarkerCustomized marker = new  MarkerCustomized(mapView,homestay);
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(homestay.getHomestayName());
            marker.setSnippet(String.valueOf(DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K')));
            CustomInfoWindow infoWindow = new CustomInfoWindow(context,mapView, supportFragmentManager,homestay);
            marker.setInfoWindow(infoWindow);
            markers.add(marker);
        }
        customerHome.sendData(markers,mapView,myLocation);

    }





    // My homestays table
    public void addHomestay(Homestay homestay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = createContentValuesFromHomestay(homestay);
        long result = db.insert(MY_HOMESTAYS_TABLE, null, cv);
        if (result == -1) {
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }
        
    }
    public ArrayList<Homestay> getAllHomestays() {
        ArrayList<Homestay> homestayList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + MY_HOMESTAYS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Homestay homestay = createHomestayFromCursor(cursor);

                homestayList.add(homestay);
            } while (cursor.moveToNext());
        }
        return homestayList;
    }
    public Homestay getMyHomestayById(String homestayID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MY_HOMESTAYS_TABLE, new String[]{"*"}, homestayID + "=?", new String[]{homestayID}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Homestay homestay = createHomestayFromCursor(cursor);
        return homestay;
    }
    public void deleteHomestay(String homestayID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MY_HOMESTAYS_TABLE, "homestayID = ?", new String[]{homestayID});
        db.close();
    }
    public void deleteAllHomestays() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MY_HOMESTAYS_TABLE, null, null);
        db.close();
    }



    // Bookings in my homestay table
    public void createBookingInMyHomestay(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = createContentValuesFromBooking(booking);
        long result = db.insert(BOOKINGS_IN_MY_HOMESTAYS_TABLE, null, cv);

        if (result == -1) {
            System.out.println("Failed to insert booking");
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("Booking inserted successfully");
//            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }
    }
    public ArrayList<Booking> getAllBookingsInMyHomestays() {
        ArrayList<Booking> bookingList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + BOOKINGS_IN_MY_HOMESTAYS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Booking booking = createBookingFromCursor(cursor);
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }
        return bookingList;
    }
    public void deleteBookingInMyHomestay(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BOOKINGS_IN_MY_HOMESTAYS_TABLE, "bookingID = ?", new String[]{bookingID});
        db.close();
    }
    public void deleteAllBookingsInMyHomestays() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BOOKINGS_IN_MY_HOMESTAYS_TABLE, null, null);
        db.close();
    }
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
    public void rejectBookingInMyHomestays(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("rejected", true);
        db.update(BOOKINGS_IN_MY_HOMESTAYS_TABLE, values, "bookingID" + "=?", new String[]{bookingID});
    }
    public void cancelBookingInMyHomestays(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isCancelled", true);
        db.update(BOOKINGS_IN_MY_HOMESTAYS_TABLE, values, "bookingID" + "=?", new String[]{bookingID});
    }






    // My bookings in other homestays table
    public void createMyBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = createContentValuesFromBooking(booking);
        long result = db.insert(MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE, null, cv);
        if (result == -1) {
            System.out.println("Failed to insert booking");
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("Booking inserted successfully");
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }
    }
    public ArrayList<Booking> getAllMyBookingsInOtherHomestays() {
        ArrayList<Booking> bookingList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Booking booking = createBookingFromCursor(cursor);
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }
        return bookingList;
    }
    public void deleteMyBookingInOtherHomestay(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE, "bookingID = ?", new String[]{bookingID});
        db.close();
    }
    public void deleteAllMyBookingsInOtherHomestays() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE, null, null);
        db.close();
    }
    public void acceptBookingInOtherHomestay(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("booked", true);
        db.update(MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE, values, "bookingID" + "=?", new String[]{bookingID});
    }
    public void rejectBookingInOtherHomestays(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("rejected", true);
        db.update(MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE, values, "bookingID" + "=?", new String[]{bookingID});
    }
    public void cancelMyBookingInOtherHomestays(String bookingID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isCancelled", true);
        db.update(MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE, values, "bookingID" + "=?", new String[]{bookingID});
    }

    //helper methods for creating content values from objects and vice versa
    private ContentValues createContentValuesFromHomestay(Homestay homestay) {
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
        return cv;
    }

    private ContentValues createContentValuesFromBooking(Booking booking){
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
        return cv;
    }

    private Homestay createHomestayFromCursor(Cursor cursor) {
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

    private Booking createBookingFromCursor(Cursor cursor){
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
        booking.setPhoneNumberHomestayOwner(cursor.getString(12));
        booking.setPhoneNumberBooker(cursor.getString(13));
        return booking;
    }


    //helper methods for resetting the database
    public void dropAllTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + MY_HOMESTAYS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + BOOKINGS_IN_MY_HOMESTAYS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + OFFLINE_HOMESTAYS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MY_BOOKINGS_IN_OTHER_HOMESTAY_TABLE);
        onCreate(db);
    }

    public void deleteAllHomestayss(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MY_HOMESTAYS_TABLE, null, null);
        db.delete(OFFLINE_HOMESTAYS_TABLE, null, null);
        db.close();
    }


}
