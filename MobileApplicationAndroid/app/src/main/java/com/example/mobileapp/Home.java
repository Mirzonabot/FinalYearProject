package com.example.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Home extends AppCompatActivity {

    TextView goToCustomerPage;
    TextView goToProviderPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        goToCustomerPage = findViewById(R.id.goingToBooking);
        goToProviderPage = findViewById(R.id.goingToHomestays);

        goToProviderPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ProviderHome.class);
                startActivity(intent);
            }
        });

        goToCustomerPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CustomerHome.class);
                startActivity(intent);
            }
        });
    }
}