package com.example.mobileapp.homepages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.BuildConfig;
import com.example.mobileapp.CustomInfoWindow;
import com.example.mobileapp.CustomerBookings;
import com.example.mobileapp.DistanceBetweenLocations;
import com.example.mobileapp.GPSTracker;
import com.example.mobileapp.dbclasses.Homestay;
import com.example.mobileapp.R;
import com.example.mobileapp.SearchHomestayFiltersFragment;
import com.example.mobileapp.memorymanager.FirebaseCRUD;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.example.mobileapp.memorymanager.SqlHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.List;

public class CustomerHome extends AppCompatActivity implements SearchHomestayFiltersFragment.OnInputListener, MapEventsReceiver {
    private GoogleMap mMap;
    final private Context ctx = this;
//    private MaterialToolbar toolbar;

    private MapView mapView;

    private BottomNavigationView bottomNavigationView;
    private Location myLocation;
    private ImageView searchBar;
    private DatabaseReference homestaysRef;




    private ArrayList<Homestay> homestaysList = new ArrayList<>();
    private static final int PERMISSION_REQUEST_LOCATION = 1;
    private List<Homestay> homestays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        mapView = findViewById(R.id.mapView);
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        ITileSource tileSource = TileSourceFactory.OpenTopo;
        mapView.setTileSource(tileSource); // Set the tile source to OpenStreetMap
        mapView.setBuiltInZoomControls(true); // Enable built-in zoom controls
        mapView.setMultiTouchControls(true); // Enable multi-touch controls
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        mapView.getOverlays().add(0, mapEventsOverlay);

        List<GeoPoint> points = new ArrayList<>();
        ArrayList<Marker> markers = new ArrayList<>();
        List<OverlayItem> items = new ArrayList<>();


        myLocation = (new GPSTracker(this)).getLocation();

        if (myLocation != null) {
            points.add(new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));

            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), createCircleBitmap(Color.RED, Color.BLACK, 20));

            marker.setIcon(bitmapDrawable);
            markers.add(marker);
        }
        if (SharedPreferences.isInternetAvailable(this)) {
            FirebaseCRUD firebaseCRUD = new FirebaseCRUD(this, mapView, getSupportFragmentManager());
            firebaseCRUD.getHomestays();
        }

        else {
            homestays = new SqlHelper(this).getAllOfflineHomestays();

            for (Homestay homestay : homestays) {

                GeoPoint point = new GeoPoint(Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()));
                OverlayItem item = new OverlayItem(homestay.getHomestayName(), "Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K') + "km", point);
                items.add(item);
                points.add(point);


                Marker marker = new Marker(mapView);
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setTitle(homestay.getHomestayName());
                marker.setSnippet("Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K') + "km");
//                    System.out.println("custom info window");
                CustomInfoWindow infoWindow = new CustomInfoWindow(this, mapView, getSupportFragmentManager(), homestay);
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


        GPSTracker gpsTracker = new GPSTracker(this);

        myLocation = gpsTracker.getLocation();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);


//        homestaysRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                LatLngBounds.Builder bc = new LatLngBounds.Builder();
//                if (myLocation != null) {
//                    bc.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
//                }
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Homestay homestay = dataSnapshot.getValue(Homestay.class);
//                    bc.include(new LatLng(Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude())));
//                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()))).title(homestay.getHomestayName()).snippet("Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude()), 'K') + "km")).setTag(homestay.getId() + "," + homestay.getOwnerId() + "," + homestay.getHomestayName());
//                }
//                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 100));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        searchBar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("Clicked");

//            }
//        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search_bar:
                        System.out.println("Home");
                        SearchHomestayFiltersFragment dialog = new SearchHomestayFiltersFragment();
                        dialog.show(getSupportFragmentManager(), "pick location");
                        break;
                    case R.id.viewBookings:
                        System.out.println("Bookings");
                        startActivity(new android.content.Intent(CustomerHome.this, CustomerBookings.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });


    }


//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//        float zoomLevel = 6.0f;
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.861034, 71.27609299999995), zoomLevel));
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//

//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(@NonNull Marker marker) {
//                System.out.println("Info Window Clicked");
//                System.out.println(marker.getTitle());
//                System.out.println(marker.getSnippet());
//                System.out.println("TAg: " + marker.getTag());
//                Bundle bundle = new Bundle();
//                bundle.putString("homestay", (String) marker.getTag());
//                BookingFragement dialog = new BookingFragement();
//                dialog.setArguments(bundle);
//                dialog.show(getSupportFragmentManager(), "pick location");
//            }
//        });

//        System.out.println("myLocation: " + myLocation);
//        if (myLocation != null) {
//            System.out.println("myLocation: " + myLocation);
//            GPSTracker gpsTrackerr = new GPSTracker(this);
//            Location myLocationn = gpsTrackerr.getLocation();
//            double clon = myLocationn.getLongitude();
//            double clat = myLocationn.getLatitude();
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(clat, clon), zoomLevel));
//            mMap.addMarker(new MarkerOptions().position(new LatLng(clat, clon)).title("Current Location")).
//                    setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//
//        }
//
//
//    }


    @Override
    public void sendInput(String checkIn, String checkOut, String address, String searchBy, String distance) {
        mMap.clear();

        // add current location marker with blue color
        if (myLocation != null) {
            System.out.println("myLocation: " + myLocation);
            double longitude = myLocation.getLongitude();
            double latitude = myLocation.getLatitude();
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Current Location")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            System.out.println("longitude: " + longitude);
            System.out.println("latitude: " + latitude);
        }

        if (distance.isEmpty()) {
            System.out.println("Distance is empty");
            searchByFunction(searchBy, address);
        }
        if (!distance.isEmpty()) {
            System.out.println("Distance is not empty");
            searchByDistance(distance);
        }


    }

    //
    private void searchByDistance(String distance) {
        if (SharedPreferences.isInternetAvailable(this)){
        homestaysRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LatLngBounds.Builder bc = new LatLngBounds.Builder();
                bc.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Homestay homestay = dataSnapshot.getValue(Homestay.class);
                    if (DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()), 'K') <= Double.valueOf(distance)) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()))).title(homestay.getHomestayName()).snippet("Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()), 'K') + "km")).setTag(homestay.getId() + "," + homestay.getOwnerId() + "," + homestay.getHomestayName());
                        bc.include(new LatLng(Double.parseDouble(homestay.getLatitude()), Double.parseDouble(homestay.getLongitude())));

                    }
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 100));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        }else {

        }
    }

    //
    private void searchByFunction(String searchBy, String address) {
        Query query = null;
        if (searchBy.equals("By village")) {
            System.out.println("By village");
            query = homestaysRef.orderByChild("village").equalTo(address);
        }
        if (searchBy.equals("By district")) {
            System.out.println("By district");
            query = homestaysRef.orderByChild("district").equalTo(address);
        }
        if (searchBy.equals("By city")) {
            System.out.println("By city");
            query = homestaysRef.orderByChild("city").equalTo(address);
        }
        if (searchBy.equals("By street")) {
            System.out.println("By street");
            query = homestaysRef.orderByChild("street").equalTo(address);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Homestay homestay = dataSnapshot.getValue(Homestay.class);
                    homestaysList.add(homestay);
                    System.out.println(homestay.getHomestayName());
                    System.out.println("___________________________");
                    System.out.println("___________________________");
                    System.out.println("Distance");
                    System.out.println(myLocation);
                    System.out.println(DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()), 'K'));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()))).title(homestay.getHomestayName()).snippet("Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()), 'K') + "km")).setTag(homestay.getId() + "," + homestay.getOwnerId() + "," + homestay.getHomestayName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        System.out.println("singleTapConfirmedHelper");
        InfoWindow.closeAllInfoWindowsOn(mapView);
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        System.out.println("longPressHelper");
        return false;
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


}