package com.example.mobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProviderBookings extends AppCompatActivity {
    private RecyclerView recyclerView, recyclerView1;
    private ArrayList<Booking> bookingsList = new ArrayList<>();
    private RelativeLayout goNewHomestayLayout;
    private TextView goNewHomestay;
    private BookingProviderAdopter bookingProviderAdopter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_bookings);

        FirebaseApp.initializeApp(this);
        DatabaseReference homestaysRef = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("booking");
        String ownerID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = homestaysRef.orderByChild("ownerID").equalTo(ownerID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                bookingsList.clear();

                for (DataSnapshot homestaySnapshot : dataSnapshot.getChildren()) {
                    Booking booking = homestaySnapshot.getValue(Booking.class);
                    // Do something with the book object
                    bookingsList.add(booking);
                }
//                System.out.println(bookingsList.toString());
//                System.out.println(bookingsList.get(0).getCheckInDate());

                if (bookingsList.size() == 0) {
                    goNewHomestayLayout.setVisibility(RelativeLayout.VISIBLE);
                    recyclerView.setVisibility(RelativeLayout.GONE);
                } else {
                    goNewHomestayLayout.setVisibility(RelativeLayout.GONE);
                    recyclerView.setVisibility(RelativeLayout.VISIBLE);
                }

                bookingProviderAdopter.setBookingList(bookingsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors here
            }
        });

        init();
        bookingProviderAdopter = new BookingProviderAdopter(this,bookingsList);
        recyclerView.setAdapter(bookingProviderAdopter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    private void init() {
        recyclerView = findViewById(R.id.providerBookingsRecyclerView);
        goNewHomestayLayout = findViewById(R.id.goNewHomestayLayout);
    }
}