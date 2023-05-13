package com.example.mobileapp.homepages;
import android.content.Context;

import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.BuildConfig;
import com.example.mobileapp.CustomerBookings;
import com.example.mobileapp.DataSender;
import com.example.mobileapp.GPSTracker;
import com.example.mobileapp.MarkerCustomized;
import com.example.mobileapp.R;
import com.example.mobileapp.fragments.SearchHomestayFiltersFragment;
import com.example.mobileapp.memorymanager.FirebaseCRUD;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.example.mobileapp.memorymanager.SqlHelper;
import com.google.android.material.appbar.MaterialToolbar;
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
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import java.util.List;

public class CustomerHome extends AppCompatActivity implements SearchHomestayFiltersFragment.OnInputListener, MapEventsReceiver, DataSender {
    final private Context ctx = this;
    private MapView mapView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private Location myLocation;
    private FirebaseCRUD firebaseCRUD;
    private SqlHelper sqlHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        mapView = findViewById(R.id.mapView);
        myLocation = (new GPSTracker(this)).getLocation();
        firebaseCRUD = new FirebaseCRUD(this, mapView, getSupportFragmentManager());
        sqlHelper = new SqlHelper(this, mapView, getSupportFragmentManager());
        toolbar = findViewById(R.id.topAppBar);

        // reset

        MenuItem resetItem = toolbar.getMenu().findItem(R.id.deleteAllOfflineHomestays);

        resetItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sqlHelper.deleteAllOfflineHomestays();
                sqlHelper.setOfflineHomestaysToInterface();
                return true;
            }
        });

        toolbar.setNavigationOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                onBackPressed();
            }
        });

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        ITileSource tileSource = TileSourceFactory.OpenTopo;
        mapView.setTileSource(tileSource);
        mapView.setMultiTouchControls(true);
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        mapView.getOverlays().add(0, mapEventsOverlay);


        if (SharedPreferences.isInternetAvailable(this)) {
            firebaseCRUD.getAllHomestays();
            resetItem.setVisible(false);

        } else {
            sqlHelper.setOfflineHomestaysToInterface();
            resetItem.setVisible(true);
        }


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search_bar:
                        SearchHomestayFiltersFragment dialog = new SearchHomestayFiltersFragment();
                        dialog.show(getSupportFragmentManager(),"pick location from osmdroid");
                        break;
                    case R.id.viewBookings:
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
            if (SharedPreferences.isInternetAvailable(this)) {
                firebaseCRUD.getHomestaysByAddress(searchBy, address);
            }
            else {
                sqlHelper.getHomestaysByAddressMarker(searchBy,address);
            }

        }
        else {
            if (SharedPreferences.isInternetAvailable(this)) {
                firebaseCRUD.getAllHomestaysInProximity(Double.parseDouble(distance));
            }

            else {
                sqlHelper.offlineHomestayInTheRange(Double.valueOf(distance));
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
    public void sendData(List<MarkerCustomized> markers, MapView mapView, Location myLocation) {
        this.myLocation = myLocation;
        this.mapView = mapView;
        clearAllMarkers();
        mapView.getOverlays().addAll(markers);
    }

    @Override
    public void sendInputToNewHomestay(String input) {

    }
}