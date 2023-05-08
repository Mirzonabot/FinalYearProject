package com.example.mobileapp.memorymanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.mobileapp.CustomInfoWindow;
import com.example.mobileapp.DataSender;
import com.example.mobileapp.DistanceBetweenLocations;
import com.example.mobileapp.GPSTracker;
import com.example.mobileapp.dbclasses.Homestay;
import com.example.mobileapp.homepages.CustomerHome;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class FirebaseCRUD{
    private final DatabaseReference homestayDatabaseReference = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("homestays");
    private final DatabaseReference bookingsDatabaseReference = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("booking");
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference();
    private final DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("tokenUserID");
    private Context context;
    private MapView mapView;

    private Location myLocation;
    private FragmentManager supportFragmentManager;
    private CustomerHome customerHome;

    public FirebaseCRUD(Context context, MapView mapView, FragmentManager supportFragmentManager) {
        this.context = context;
        this.mapView = mapView;
        this.myLocation = new GPSTracker(context).getLocation();
        this.supportFragmentManager = supportFragmentManager;
        this.customerHome = new CustomerHome();
    }
    public List<Homestay> getHomestays() {

        return updateList(homestayDatabaseReference, Homestay.class);
    }


    public void getAllHomestays(){

        List<Marker> markers = new ArrayList<>();
        GPSTracker myLocation = new GPSTracker(context);

        homestayDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (myLocation != null) {
                    Marker marker = new Marker(mapView);
                    marker.setPosition(new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), createCircleBitmap(Color.RED, Color.BLACK, 20));

                    marker.setIcon(bitmapDrawable);
                    markers.add(marker);
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Homestay homestay = dataSnapshot.getValue(Homestay.class);
                    GeoPoint point = new GeoPoint(Double.parseDouble(homestay.getLatitude()),Double.parseDouble(homestay.getLongitude()));
                    OverlayItem item = new OverlayItem(homestay.getHomestayName(), "Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K') + "km", point);

                    Marker marker = new Marker(mapView);
                    marker.setPosition(point);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setTitle(homestay.getHomestayName());
                    marker.setSnippet("Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K') + "km");
                    CustomInfoWindow infoWindow = new CustomInfoWindow(context,mapView, supportFragmentManager,homestay);
                    marker.setInfoWindow(infoWindow);
                    markers.add(marker);
                }

                customerHome.sendData(markers,mapView,myLocation.getLocation());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getAllHomestaysInProximity(double distance){

            List<Marker> markers = new ArrayList<>();


            homestayDatabaseReference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (myLocation != null) {
                        Marker marker = new Marker(mapView);
                        marker.setPosition(new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), createCircleBitmap(Color.RED, Color.BLACK, 20));

                        marker.setIcon(bitmapDrawable);
                        markers.add(marker);
                    }
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Homestay homestay = dataSnapshot.getValue(Homestay.class);
                        if(DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K') <= distance) {
                            GeoPoint point = new GeoPoint(Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()));
                            OverlayItem item = new OverlayItem(homestay.getHomestayName(), "Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K') + "km", point);

                            Marker marker = new Marker(mapView);
                            marker.setPosition(point);
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            marker.setTitle(homestay.getHomestayName());
                            marker.setSnippet("Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K') + "km");
                            CustomInfoWindow infoWindow = new CustomInfoWindow(context, mapView, supportFragmentManager, homestay);
                            marker.setInfoWindow(infoWindow);
                            markers.add(marker);
                        }
                    }

                    customerHome.sendData(markers,mapView,myLocation);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    public void getHomestaysByAddress(String searchBy, String address) {
        List<Marker> markers = new ArrayList<>();

        Query query = null;
        if (searchBy.equals("By village")) {
            System.out.println("By village");
            query = homestayDatabaseReference.orderByChild("village").equalTo(address);
        }
        if (searchBy.equals("By district")) {
            System.out.println("By district");
            query = homestayDatabaseReference.orderByChild("district").equalTo(address);
        }
        if (searchBy.equals("By city")) {
            System.out.println("By city");
            query = homestayDatabaseReference.orderByChild("city").equalTo(address);
        }
        if (searchBy.equals("By street")) {
            System.out.println("By street");
            query = homestayDatabaseReference.orderByChild("street").equalTo(address);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (myLocation != null) {
                    Marker marker = new Marker(mapView);
                    marker.setPosition(new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), createCircleBitmap(Color.RED, Color.BLACK, 20));

                    marker.setIcon(bitmapDrawable);
                    markers.add(marker);
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Homestay homestay = dataSnapshot.getValue(Homestay.class);
                        GeoPoint point = new GeoPoint(Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()));
                        OverlayItem item = new OverlayItem(homestay.getHomestayName(), "Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K') + "km", point);

                        Marker marker = new Marker(mapView);
                        marker.setPosition(point);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        marker.setTitle(homestay.getHomestayName());
                        marker.setSnippet("Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K') + "km");
                        CustomInfoWindow infoWindow = new CustomInfoWindow(context, mapView, supportFragmentManager, homestay);
                        marker.setInfoWindow(infoWindow);
                        markers.add(marker);
                }

                customerHome.sendData(markers,mapView,myLocation);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private <T> ArrayList<T> updateList(Query query, Class<T> valueType) {
        ArrayList<T> list = new ArrayList<>();
        System.out.println("____________________________________________");
        System.out.println("____________________________________________");
        System.out.println("____________________________________________");
        System.out.println("____________________________________________");
        System.out.println("");
        System.out.println("____________________________________________");
        System.out.println("____________________________________________");
        System.out.println("____________________________________________");
        System.out.println("____________________________________________");
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<GeoPoint> points = new ArrayList<>();
                ArrayList<Marker> markers = new ArrayList<>();
                List<OverlayItem> items = new ArrayList<>();


                if (myLocation != null) {
                    points.add(new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));

                    Marker marker = new Marker(mapView);
                    marker.setPosition(new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), createCircleBitmap(Color.RED, Color.BLACK, 20));

                    marker.setIcon(bitmapDrawable);
                    markers.add(marker);
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Homestay homestay = dataSnapshot.getValue(Homestay.class);
                    GeoPoint point = new GeoPoint(Double.parseDouble(homestay.getLatitude()),Double.parseDouble(homestay.getLongitude()));
                    OverlayItem item = new OverlayItem(homestay.getHomestayName(), "Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K') + "km", point);
                    items.add(item);
                    points.add(point);

                    Marker marker = new Marker(mapView);
                    marker.setPosition(point);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setTitle(homestay.getHomestayName());
                    marker.setSnippet("Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K') + "km");
//                    System.out.println("custom info window");
                    CustomInfoWindow infoWindow = new CustomInfoWindow(context,mapView, supportFragmentManager,homestay);
                    marker.setInfoWindow(infoWindow);



                    markers.add(marker);
                }

                mapView.getOverlays().addAll(markers);

                BoundingBox boundingBox = BoundingBox.fromGeoPoints(points);
                if (boundingBox != null) {
                    System.out.println("boundingBox: " + boundingBox);
                    System.out.println("not null");
                    IMapController mapController = mapView.getController();
                    mapController.zoomToSpan(boundingBox.getLatitudeSpan(), boundingBox.getLongitudeSpan());
                    mapController.setCenter(boundingBox.getCenter());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list;
    }



    private static Bitmap createCircleBitmap(int fillColor, int strokeColor, int radius) {
        Bitmap output = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setColor(fillColor);
        paint.setAntiAlias(true);

        canvas.drawCircle(radius, radius, radius, paint);

        paint.setColor(strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawCircle(radius, radius, radius, paint);

        return output;
    }


    public void setUserHasInternet(String userId, boolean hasInternet) {
        System.out.println("setUserHasInternet: " + userId + " " + hasInternet);
        userDatabaseReference.child(userId).child("userHasInternet").setValue(hasInternet).onSuccessTask(new SuccessContinuation<Void, Void>() {
            @NonNull
            @Override
            public Task<Void> then(@Nullable Void aVoid) throws Exception {
                System.out.println("setUserHasInternet: " + userId + " " + hasInternet + " success");
                return null;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("setUserHasInternet: " + userId + " " + hasInternet + " failed");
            }
        });
    }




    public void SearchHomestay(String search) {
        System.out.println("SearchHomestay: " + search);
        CustomerHome customerHome = new CustomerHome();
//        customerHome.sendData("search");
    }
}
