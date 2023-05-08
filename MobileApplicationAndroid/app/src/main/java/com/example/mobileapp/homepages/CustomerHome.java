package com.example.mobileapp.homepages;

import android.content.Context;

import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.BuildConfig;
import com.example.mobileapp.CustomInfoWindow;
import com.example.mobileapp.CustomerBookings;
import com.example.mobileapp.DataSender;
import com.example.mobileapp.DistanceBetweenLocations;
import com.example.mobileapp.GPSTracker;
import com.example.mobileapp.dbclasses.Homestay;
import com.example.mobileapp.R;
import com.example.mobileapp.SearchHomestayFiltersFragment;
import com.example.mobileapp.memorymanager.FirebaseCRUD;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.example.mobileapp.memorymanager.SqlHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.List;

public class CustomerHome extends AppCompatActivity implements SearchHomestayFiltersFragment.OnInputListener, MapEventsReceiver, DataSender {
    final private Context ctx = this;
    private MapView mapView;

    private BottomNavigationView bottomNavigationView;
    private Location myLocation;


    private ArrayList<Marker> markers;
    private List<GeoPoint> points;
    private List<OverlayItem> items;


    private ArrayList<Homestay> homestaysList = new ArrayList<>();

    private List<Homestay> homestays;
    private FirebaseCRUD firebaseCRUD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        mapView = findViewById(R.id.mapView);
        myLocation = (new GPSTracker(this)).getLocation();
        firebaseCRUD = new FirebaseCRUD(this, mapView, getSupportFragmentManager());

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        ITileSource tileSource = TileSourceFactory.OpenTopo;
        mapView.setTileSource(tileSource); // Set the tile source to OpenStreetMap
//        mapView.setBuiltInZoomControls(true); // Enable built-in zoom controls
        mapView.setMultiTouchControls(true); // Enable multi-touch controls
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        mapView.getOverlays().add(0, mapEventsOverlay);


        markers = new ArrayList<>();


        if (SharedPreferences.isInternetAvailable(this)) {
            firebaseCRUD.getAllHomestays();
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


        }


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);



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



    private void clearAllMarkers() {
        List<Overlay> overlays = mapView.getOverlays();
        for (Overlay overlay : overlays) {
            if (overlay instanceof Marker) {
                this.mapView.getOverlays().remove(overlay);
            }
        }
        mapView.getController().setCenter(new GeoPoint(this.myLocation.getLatitude(), this.myLocation.getLongitude()));
        mapView.getController().setZoom(7.0);

        mapView.setMinZoomLevel(1.0);
        mapView.setMaxZoomLevel(20.0);
    }





    @Override
    public void sendInput(String checkIn, String checkOut, String address, String searchBy, String distance) {


        if (distance.isEmpty()) {
            System.out.println("Distance is empty");
            firebaseCRUD.getHomestaysByAddress(searchBy, address);
        }
        if (!distance.isEmpty()) {
            System.out.println("Distance is not empty");
            if (SharedPreferences.isInternetAvailable(this)) {
                firebaseCRUD.getAllHomestaysInProximity(Double.parseDouble(distance));
            }

            else {
                homestays.clear();
                homestays = new SqlHelper(this).inTheRange(Double.valueOf(distance), myLocation.getLatitude(), myLocation.getLongitude());
            }
        }


    }
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(mapView);
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }


    @Override
    public void sendData(List<Marker> markers, MapView mapView, Location myLocation) {
        this.myLocation = myLocation;
        this.mapView = mapView;
        clearAllMarkers();
        mapView.getOverlays().addAll(markers);
    }
}