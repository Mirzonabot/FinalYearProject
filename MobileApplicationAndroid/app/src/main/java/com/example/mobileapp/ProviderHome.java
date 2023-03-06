package com.example.mobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProviderHome extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_LOCATION = 1;
    TextView createNewHomestay;
    private ArrayList<Homestay> homestaysList;
    private HomestayAdapter homestayAdapter;
    private RecyclerView recyclerView;
    private LocationManager mLocationManager;

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_home);
        homestaysList = new ArrayList<>();

        recyclerView = findViewById(R.id.myHomestaysRecyclerView);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        toolbar = (MaterialToolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Homestays");
        

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        FirebaseApp.initializeApp(this);
        DatabaseReference homestaysRef = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("homestays");
        String ownerID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = homestaysRef.orderByChild("ownerId").equalTo(ownerID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                homestaysList.clear();

                for (DataSnapshot homestaySnapshot : dataSnapshot.getChildren()) {
                    Homestay homestay = homestaySnapshot.getValue(Homestay.class);
                    // Do something with the book object
                    homestaysList.add(homestay);
                }
                homestayAdapter.setHomestayList(homestaysList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors here
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_new_homestay:
                        startActivity(new Intent(getApplicationContext(), NewHomestay.class));
                        break;
                    default:
                        break;
                }
                return false;
            }
        });



        homestayAdapter = new HomestayAdapter(this, homestaysList);
        recyclerView.setAdapter(homestayAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the location manager

// Get the last known location from the location manager

        Location myLocation = getLastKnownLocation();
        System.out.println("myLocation: " + myLocation);
        if (myLocation != null) {
            System.out.println("myLocation: " + myLocation);
            double longitude = myLocation.getLongitude();
            double latitude = myLocation.getLatitude();
            System.out.println("longitude: " + longitude);
            System.out.println("latitude: " + latitude);
        }


    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

        System.out.println("Before permission check");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            System.out.println("asked for permission");
        } else {
            System.out.println("permission already granted");
            // Permission has already been granted
            // Do something that requires the permission
        }

        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }



}