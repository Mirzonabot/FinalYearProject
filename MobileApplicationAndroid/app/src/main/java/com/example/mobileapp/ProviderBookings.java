package com.example.mobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobileapp.dbclasses.Booking;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.example.mobileapp.memorymanager.SqlHelper;
import com.example.mobileapp.recyclerviewadapters.BookingProviderAdopter;
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
        init();
        if (SharedPreferences.isInternetAvailable(this)) {
            FirebaseApp.initializeApp(this);
            DatabaseReference homestaysRef = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("booking");
            String ownerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            bookingProviderAdopter = new BookingProviderAdopter(this,bookingsList);
            recyclerView.setAdapter(bookingProviderAdopter);
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
        }
        else {
            System.out.println("No internet");
            System.out.println("Getting data from local database");
            SqlHelper sqlHelper = new SqlHelper(this,null,null);
            ArrayList<Booking> bookingsList1 = sqlHelper.getAllBookingsInMyHomestays();
//            System.out.println("_______________________________________");
//            System.out.println("_______________________________________");
//            System.out.println("_______________________________________");
//            System.out.println(bookingsList1.toString());
//            System.out.println(bookingsList1.get(0).getCheckInDate());
//            System.out.println("_______________________________________");
//            System.out.println("_______________________________________");
//            System.out.println("_______________________________________");
//            bookingProviderAdopter.setBookingList(bookingsList1);
            bookingProviderAdopter = new BookingProviderAdopter(this,bookingsList1);
            recyclerView.setAdapter(bookingProviderAdopter);
        }




        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    private void init() {
        recyclerView = findViewById(R.id.providerBookingsRecyclerView);
        goNewHomestayLayout = findViewById(R.id.goNewHomestayLayout);
    }
}