package com.example.mobileapp;


import androidx.appcompat.app.AppCompatActivity;



import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;

import com.yandex.mapkit.MapKitFactory;


public class NewHomestay extends AppCompatActivity {

    ImageView addLocaionFromMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("69044d6e-9641-4c53-8d74-897fb9363d17");
        setContentView(R.layout.activity_new_homestay);

        addLocaionFromMap = (ImageView) findViewById(R.id.addLocationFromMap);

        addLocaionFromMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickLocationDialog dialog = new PickLocationDialog();
                dialog.show(getSupportFragmentManager(),"pick location");
            }
        });



    }

}