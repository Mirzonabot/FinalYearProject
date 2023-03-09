package com.example.mobileapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerHome extends AppCompatActivity implements OnMapReadyCallback, SearchHomestayFiltersFragment.OnInputListener {
    private GoogleMap mMap;
//    private MaterialToolbar toolbar;

    private BottomNavigationView bottomNavigationView;
    private Location myLocation;
    private ImageView searchBar;
    private DatabaseReference homestaysRef;


    private ArrayList<Homestay> homestaysList = new ArrayList<>();
    private static final int PERMISSION_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);


//        toolbar = (MaterialToolbar) findViewById(R.id.toolbar);
//        searchBar = findViewById(R.id.search_bar);

        GPSTracker gpsTracker = new GPSTracker(this);

        myLocation = gpsTracker.getLocation();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.google_map);

        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

        supportMapFragment.getMapAsync(this);
        FirebaseApp.initializeApp(this);
        homestaysRef = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("homestays");

        homestaysRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Homestay homestay = dataSnapshot.getValue(Homestay.class);
//                    homestaysList.add(homestay);
                    System.out.println(homestay.getHomestayName());
                    System.out.println("___________________________");
                    System.out.println("___________________________");
                    System.out.println("Distance");
                    System.out.println(myLocation);
                    System.out.println(DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()), 'K'));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()))).title(homestay.getHomestayName()).snippet("Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()), 'K') + "km")).setTag(homestay.getId() + "," + homestay.getOwnerId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                        break;
                    default:
                        break;
                }
                return true;
            }
        });




    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        float zoomLevel = 6.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.861034, 71.27609299999995), zoomLevel));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                System.out.println("Marker Clicked");
                System.out.println(marker.getTitle());
                System.out.println(marker.getSnippet());
                System.out.println("TAg: " + marker.getTag());
                Bundle bundle = new Bundle();
                bundle.putString("homestay", (String) marker.getTag());
                BookingFragement dialog = new BookingFragement();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "pick location");
                return false;
            }
        });

        System.out.println("myLocation: " + myLocation);
        if (myLocation != null) {
            System.out.println("myLocation: " + myLocation);
            GPSTracker gpsTrackerr = new GPSTracker(this);
            Location myLocationn = gpsTrackerr.getLocation();
            double clon = myLocationn.getLongitude();
            double clat = myLocationn.getLatitude();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(clat,clon), zoomLevel));
            mMap.addMarker(new MarkerOptions().position(new LatLng(clat, clon)).title("Current Location")).
                    setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        }


    }




    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void sendInput(String checkIn, String checkOut, String address, String searchBy, String distance) {
        mMap.clear();

        // add current location marker with blue color
        if (myLocation != null) {
            System.out.println("myLocation: " + myLocation);
            double longitude = myLocation.getLongitude();
            double latitude = myLocation.getLatitude();
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Current Location")).
                    setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
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

        System.out.println("Urrrrrraaaaaa");
    }

    private void searchByDistance(String distance) {
        homestaysRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Homestay homestay = dataSnapshot.getValue(Homestay.class);
                    if (DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()), 'K') <= Double.valueOf(distance)) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()))).title(homestay.getHomestayName()).snippet("Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()), 'K') + "km")).setTag(homestay.getId() + "," + homestay.getOwnerId());;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()))).title(homestay.getHomestayName()).snippet("Distance: " + DistanceBetweenLocations.distance(myLocation.getLatitude(), myLocation.getLongitude(), Double.valueOf(homestay.getLatitude()), Double.valueOf(homestay.getLongitude()), 'K') + "km")).setTag(homestay.getId() + "," + homestay.getOwnerId());;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}