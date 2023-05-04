package com.example.mobileapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mobileapp.homepages.CustomerHome;
import com.example.mobileapp.homepages.ProviderHome;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SampleActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private BottomNavigationView bottomNavigationView;
    private Uri imageUri;
    private ImageView imageViewProfile;
    private StorageReference storageReference;
    private MaterialToolbar toolbar;
    private MenuItem logout;
    private MaterialButtonToggleGroup toggleGroup;

    private GPSTracker gpsTracker = new GPSTracker(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        SharedPreferences.configureInternet(this);

        init();
        setSupportActionBar(toolbar);




        // Initialize the ActionBarDrawerToggle and set it as the drawer listener
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // This syncs the state of the drawer indicator (hamburger icon) with the drawer's state
        actionBarDrawerToggle.syncState();





        logout.setOnMenuItemClickListener(item -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SampleActivity.this, Login.class));
            finish();
            return true;
        });

        imageViewProfile.setOnClickListener(v -> {
            System.out.println("Clicked");
            selectImage();
        });
        if (SharedPreferences.getUserType(this).equals("provider")) {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.menu_home_provider);
            initProvider();
            toggleGroup.check(R.id.provider_mode);
        } else {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.menu_home_client);
            initClient();
            toggleGroup.check(R.id.client_mode);
        }


        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.provider_mode) {
                    bottomNavigationView.getMenu().clear();
                    bottomNavigationView.inflateMenu(R.menu.menu_home_provider);
                    SharedPreferences.setUserType(this, "provider");
                    initProvider();
                    Toast.makeText(this, "Buyer", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.client_mode) {
                    bottomNavigationView.getMenu().clear();
                    bottomNavigationView.inflateMenu(R.menu.menu_home_client);
                    SharedPreferences.setUserType(this, "client");
                    initClient();
                    Toast.makeText(this, "Seller", Toast.LENGTH_SHORT).show();
                }
            }
        });


        getLocation();


    }

    private void getLocation() {


        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        ReverseGeocodingTask reverseGeocodingTask = new ReverseGeocodingTask();
        reverseGeocodingTask.execute(latitude, longitude);
    }


    private void init() {
        // Initialize the toolbar and set it as the action bar for the activity
        toolbar = findViewById(R.id.toolbar);

        // Initialize the drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);

        // Initialize the NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Set up your navigation view listener here, if needed
        imageViewProfile = navigationView.getHeaderView(0).findViewById(R.id.profile_image);

        // get the logout menu item and set its click listener
        logout = navigationView.getMenu().findItem(R.id.logout);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem menuItemToggle = navigationView.getMenu().findItem(R.id.buyer_seller_options);
        toggleGroup = (MaterialButtonToggleGroup) menuItemToggle.getActionView().findViewById(R.id.toggle_button_buyer_seller);

        storageReference = FirebaseStorage.getInstance("gs://homestaybooking-f8308.appspot.com/").getReference("images/profiles");
        System.out.println("Storage reference: " + storageReference);


    }

    private void initClient(){
        MenuItem menuItemHomestaySearch = bottomNavigationView.getMenu().findItem(R.id.search_homestay);
        MenuItem menuItemBookingsClient = bottomNavigationView.getMenu().findItem(R.id.bookings_client);
        menuItemHomestaySearch.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(SampleActivity.this, CustomerHome.class));
            return true;
        });
        menuItemBookingsClient.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(SampleActivity.this, ProviderHome.class));
            return true;
        });
    }

    private void initProvider(){
        MenuItem menuItemHomestays = bottomNavigationView.getMenu().findItem(R.id.homestays);
        MenuItem menuItemBookingsProvider = bottomNavigationView.getMenu().findItem(R.id.bookings_provider);
        menuItemHomestays.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(SampleActivity.this, ProviderHome.class));
            return true;
        });
        menuItemBookingsProvider.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(SampleActivity.this, ProviderHome.class));
            return true;
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            assert data != null;
            if (data.getData() != null) {
                imageUri = data.getData();
                ImageView imageView = findViewById(R.id.profile_image);
                Picasso.get()
                        .load(imageUri)
                        .resize(180, 180)
                        .centerCrop()
                        .into(imageView);
                storageReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(SampleActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        }

    }


}