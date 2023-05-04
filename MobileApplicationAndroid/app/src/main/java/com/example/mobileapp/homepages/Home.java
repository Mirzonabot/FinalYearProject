package com.example.mobileapp.homepages;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.Manifest;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mobileapp.Login;
import com.example.mobileapp.MapDownloader;
import com.example.mobileapp.R;
import com.example.mobileapp.SampleActivity;
import com.example.mobileapp.homepages.CustomerHome;
import com.example.mobileapp.homepages.ProviderHome;
import com.example.mobileapp.memorymanager.FirebaseCRUD;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.example.mobileapp.memorymanager.SqlHelper;
import com.example.mobileapp.smsmanager.SMSSender;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;


import java.util.Locale;

public class Home extends AppCompatActivity {

    SqlHelper sqlHelper;
    TextView goToCustomerPage;
    TextView goToProviderPage;
    private MaterialToolbar toolbar;
    private Button downloadMapButton, goToSampleActivityButton;

    private static final String CHANNEL_ID = "channel1";
    private static final String CHANNEL_NAME = "Channel 1";
    private static final String CHANNEL_DESC = "This is Channel 1";
    private static final int PERMISSION_REQUEST_SMS_RECEIVE = 1;
    private static final int PERMISSION_REQUEST_SMS_SEND = 2;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SMS_RECEIVE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted, do something
                System.out.println("Pemission granted");
            } else {
                // permission denied, do something
                System.out.println("Pemission denied");
            }
        }
        else if (requestCode == PERMISSION_REQUEST_SMS_SEND) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted, do something
                System.out.println("Pemission granted");
            } else {
                // permission denied, do something
                System.out.println("Pemission denied");
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS_SEND);
            Toast.makeText(this, "requestinggggg", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "not requestinggggg", Toast.LENGTH_SHORT).show();
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
            // internet is available
            Toast.makeText(this, "internet is available", Toast.LENGTH_SHORT).show();
            FirebaseCRUD firebaseCRUD = new FirebaseCRUD(this,null,null);
            if (!SharedPreferences.isInternetAvailable(this)){
                SharedPreferences.internetAvailable(this, true);
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                firebaseCRUD.setUserHasInternet(userId,true);
            }
        } else {
            SMSSender.sendSMS("Internet is not available", "+996552328280");
            SharedPreferences.internetAvailable(this, false);
            // internet is not available
            Toast.makeText(this, "Internet is not available", Toast.LENGTH_SHORT).show();
        }


//        sqlHelper = new SqlHelper(this);
//        sqlHelper.dropAllTables();
//        sendNotification();
//        OnlineTileSourceBase source = new XYTileSource("OpenStreetMap",4, 10,  256, ".png", new String[] {
//                "http://a.tile.openstreetmap.org/",
//                "http://b.tile.openstreetmap.org/",
//                "http://c.tile.openstreetmap.org/"});
//        MapTileDownloader downloader = new MapTileDownloader(source);
//        downloader.setTileSource(TileSourceFactory.MAPNIK);
//
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSION_REQUEST_SMS_RECEIVE);
        }



        goToCustomerPage = findViewById(R.id.bookingButton);
        goToProviderPage = findViewById(R.id.goingToHomestays);
        downloadMapButton = findViewById(R.id.downloadMapButton);
        goToSampleActivityButton = findViewById(R.id.sample);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        goToProviderPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProviderHome.class);
                startActivity(intent);
            }
        });
        goToCustomerPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CustomerHome.class);
                startActivity(intent);
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Toolbar clicked");
//                sendNotification();
            }
        });

        downloadMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapDownloader.class);
                startActivity(intent);
            }
        });

        goToSampleActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SampleActivity.class);
                startActivity(intent);
            }
        });
//        Utils.getTokens();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
//            case R.id.tj:
//                Locale locale = new Locale("tg");
//                Locale.setDefault(locale);
//                Configuration config = new Configuration();
//                config.setLocale(locale);
//                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//                System.out.println("Language setting clicked");
//                Intent intent1 = getIntent();
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
//                startActivity(intent1);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                return true;
//            case R.id.en:
//                Locale locale1 = new Locale("en");
//                Locale.setDefault(locale1);
//                Configuration config1 = new Configuration();
//                config1.setLocale(locale1);
//                getBaseContext().getResources().updateConfiguration(config1, getBaseContext().getResources().getDisplayMetrics());
//                finish();
//                startActivity(getIntent());
//                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        onDestroy();
    }


}