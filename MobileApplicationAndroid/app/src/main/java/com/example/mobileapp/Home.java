package com.example.mobileapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;

import java.util.Locale;

public class Home extends AppCompatActivity {

    TextView goToCustomerPage;
    TextView goToProviderPage;
    private MaterialToolbar toolbar;

    private static final String CHANNEL_ID = "channel1";
    private static final String CHANNEL_NAME = "Channel 1";
    private static final String CHANNEL_DESC = "This is Channel 1";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        sendNotification();
        OnlineTileSourceBase source = new XYTileSource("OpenStreetMap",4, 10,  256, ".png", new String[] {
                "http://a.tile.openstreetmap.org/",
                "http://b.tile.openstreetmap.org/",
                "http://c.tile.openstreetmap.org/"});
        MapTileDownloader downloader = new MapTileDownloader(source);
        downloader.setTileSource(TileSourceFactory.MAPNIK);



        goToCustomerPage = findViewById(R.id.bookingButton);
        goToProviderPage = findViewById(R.id.goingToHomestays);
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
            case R.id.tj:
                Locale locale = new Locale("tg");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.setLocale(locale);
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                System.out.println("Language setting clicked");
                Intent intent1 = getIntent();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            case R.id.en:
                Locale locale1 = new Locale("en");
                Locale.setDefault(locale1);
                Configuration config1 = new Configuration();
                config1.setLocale(locale1);
                getBaseContext().getResources().updateConfiguration(config1, getBaseContext().getResources().getDisplayMetrics());
                finish();
                startActivity(getIntent());
                return true;
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