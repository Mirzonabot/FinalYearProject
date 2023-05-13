package com.example.mobileapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mobileapp.dbclasses.Booking;
import com.example.mobileapp.dbclasses.Homestay;
import com.example.mobileapp.homepages.CustomerHome;
import com.example.mobileapp.homepages.ProviderHome;
import com.example.mobileapp.memorymanager.FirebaseCRUD;
import com.example.mobileapp.memorymanager.SharedPreferences;

import com.example.mobileapp.memorymanager.SqlHelper;
import com.example.mobileapp.recyclerviewadapters.BookingCustomerAdopter;
import com.example.mobileapp.recyclerviewadapters.BookingProviderAdopter;
import com.example.mobileapp.recyclerviewadapters.HomestayAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SampleActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private BottomNavigationView bottomNavigationView;
    private Uri imageUri;
    private ImageView imageViewProfile;
    private StorageReference storageReference;
    private MaterialToolbar toolbar;
    private MenuItem logout, downloadMap;
    private MaterialButtonToggleGroup toggleGroup;
    private GPSTracker gpsTracker;
    private SwitchCompat intSwitch;
    private RecyclerView recyclerViewHomestays;
    private RecyclerView recyclerViewBookings;
    private HomestayAdapter homestayAdapter;
    private BookingProviderAdopter bookingAdapterProvider;
    private BookingCustomerAdopter bookingAdapterCustomer;
    private SqlHelper sqlHelper;
    private MaterialTextView profileName, profileEmail,profilePhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        initViews();
        setSupportActionBar(toolbar);
        setupDrawerAndToggle();
        checkInternetConnection();
        setupLogoutMenuItem();
        setupDownloadMapMenuItem();
        setupProfileImageClickListener();
        setupBottomNavigationView();
        setupToggleGroup();
        setupInternetSwitch();
        getLocation();
    }
    private void setupDownloadMapMenuItem() {
        downloadMap.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(SampleActivity.this, MapDownloader.class));
            return true;
        });
    }
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        imageViewProfile = navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        profileName = navigationView.getHeaderView(0).findViewById(R.id.profile_name);
        profileEmail = navigationView.getHeaderView(0).findViewById(R.id.profile_email);
        profilePhone = navigationView.getHeaderView(0).findViewById(R.id.profile_phone);
        logout = navigationView.getMenu().findItem(R.id.logout);
        downloadMap = navigationView.getMenu().findItem(R.id.downloadMap);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem menuItemToggle = navigationView.getMenu().findItem(R.id.buyer_seller_options);
        toggleGroup = (MaterialButtonToggleGroup) menuItemToggle.getActionView().findViewById(R.id.toggle_button_buyer_seller);
        intSwitch = navigationView.getMenu().findItem(R.id.internet_access).getActionView().findViewById(R.id.toggle_switch);
        storageReference = FirebaseStorage.getInstance("gs://homestaybooking-f8308.appspot.com/").getReference("images/profiles");
        gpsTracker = new GPSTracker(this);
        recyclerViewHomestays = findViewById(R.id.recycler_view_homestays);
        recyclerViewBookings = findViewById(R.id.recycler_view_bookings);
        homestayAdapter = new HomestayAdapter(this, null,true);
        bookingAdapterProvider = new BookingProviderAdopter(this, null);
        bookingAdapterCustomer = new BookingCustomerAdopter(this, null);
        sqlHelper = new SqlHelper(this, null, null);
        storageReference = FirebaseStorage.getInstance("gs://homestaybooking-f8308.appspot.com/").getReference("images/profiles/"+SharedPreferences.getUserId(this));

        if (storageReference != null) {
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get()
                        .load(uri)
                        .resize(180, 180)
                        .centerCrop()
                        .into(imageViewProfile);
            });
        }

        profileEmail.setText(SharedPreferences.getEmail(this));
        profileName.setText(SharedPreferences.getUserName(this));
        profilePhone.setText(SharedPreferences.getPhoneNumber(this));
    }
    private void setupDrawerAndToggle() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }
    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
            intSwitch.setChecked(true);
            Toast.makeText(this, "internet is available", Toast.LENGTH_SHORT).show();
            updateInternetAvailability(true);
        } else {
            SharedPreferences.internetAvailable(this, false);
        }
        SharedPreferences.configureInternet(this);
    }
    private void updateInternetAvailability(boolean isAvailable) {
        FirebaseCRUD firebaseCRUD = new FirebaseCRUD(this, null, null);

        SharedPreferences.internetAvailable(this, isAvailable);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseCRUD.setUserHasInternet(userId, isAvailable);

    }
    private void setupLogoutMenuItem() {
        logout.setOnMenuItemClickListener(item -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SampleActivity.this, Login.class));
            finish();
            return true;
        });
    }
    private void setupProfileImageClickListener() {
        imageViewProfile.setOnClickListener(v -> {
            selectImage();
        });
    }
    private void setupBottomNavigationView() {
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
    }
    private void initClient() {
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

        setRecyclerElements();


    }
    private void setRecyclerElements() {
        ArrayList<Homestay> homestays = (ArrayList<Homestay>) sqlHelper.getAllOfflineHomestaysList();
        ArrayList<Booking> bookings = (ArrayList<Booking>) sqlHelper.getAllMyBookingsInOtherHomestays();
        if (bookings.size() > 0) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerViewBookings.setLayoutManager(layoutManager);
            recyclerViewBookings.setAdapter(bookingAdapterCustomer);

            bookingAdapterCustomer.setBookingList(bookings);
        } else {
            recyclerViewBookings.setVisibility(View.GONE);
        }

        if (homestays != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            if (homestays.size() > 0 && homestays != null) {
                recyclerViewHomestays.setLayoutManager(layoutManager);
                recyclerViewHomestays.setAdapter(homestayAdapter);
                homestayAdapter.setHomestayList(homestays);
            } else {
                recyclerViewHomestays.setVisibility(View.GONE);
            }
        }
    }
    private void initProvider() {
        MenuItem menuItemHomestays = bottomNavigationView.getMenu().findItem(R.id.homestays);
        MenuItem menuItemBookingsProvider = bottomNavigationView.getMenu().findItem(R.id.bookings_provider);
        menuItemHomestays.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(SampleActivity.this, ProviderHome.class));
            return true;
        });
        menuItemBookingsProvider.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(SampleActivity.this, ProviderBookings.class));
            return true;
        });

        ArrayList<Homestay> homestays = (ArrayList<Homestay>) sqlHelper.getAllHomestays();
        ArrayList<Booking> bookings = (ArrayList<Booking>) sqlHelper.getAllBookingsInMyHomestays();
        if (bookings.size() > 0) {
            recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewBookings.setAdapter(bookingAdapterProvider);
            bookingAdapterProvider.setBookingList(bookings);
        } else {
//            recyclerViewBookings.setVisibility(View.GONE);
        }

        if (homestays.size() > 0) {
            recyclerViewHomestays.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewHomestays.setAdapter(homestayAdapter);
            homestayAdapter.setHomestayList(homestays);
        } else {
//            recyclerViewHomestays.setVisibility(View.GONE);
        }

    }
    private void setupToggleGroup() {
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
    }
    private void setupInternetSwitch() {
        intSwitch.setOnClickListener(view -> {
            if (intSwitch.isChecked()) {
                Toast.makeText(SampleActivity.this, "internet is available", Toast.LENGTH_SHORT).show();
                updateInternetAvailability(true);
            } else {
                Toast.makeText(SampleActivity.this, "internet is not available", Toast.LENGTH_SHORT).show();
                updateInternetAvailability(false);
            }
        });
    }
    private void getLocation() {
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        ReverseGeocodingTask reverseGeocodingTask = new ReverseGeocodingTask();
        reverseGeocodingTask.execute(latitude, longitude);
    }
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                ImageView imageView = findViewById(R.id.profile_image);
                Picasso.get()
                        .load(imageUri)
                        .resize(180, 180)
                        .centerCrop()
                        .into(imageView);
                uploadImageToStorage();
            }
        }
    }
    private void uploadImageToStorage() {
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(SampleActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                });
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        setRecyclerElements();
    }

}