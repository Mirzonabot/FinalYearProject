package com.example.mobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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

public class CustomerBookings extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RelativeLayout goBookingLayout;
    private TextView goBooking;
    private ArrayList<Booking> bookingsList = new ArrayList<>();
    private BookingCustomerAdopter bookingCustomerAdopter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_bookings);

        FirebaseApp.initializeApp(this);
        DatabaseReference homestaysRef = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("booking");
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = homestaysRef.orderByChild("userID").equalTo(customerId);
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
                    goBookingLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    goBooking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new android.content.Intent(CustomerBookings.this, CustomerHome.class));
                        }
                    });
                } else {
                    goBookingLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                bookingCustomerAdopter.setBookingList(bookingsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors here
            }
        });
        init();
        bookingCustomerAdopter = new BookingCustomerAdopter(this,bookingsList);
        recyclerView.setAdapter(bookingCustomerAdopter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    private void init() {
        recyclerView = findViewById(R.id.recycler_booking_customer);
        goBookingLayout = findViewById(R.id.goBookingLayout);
        goBooking = findViewById(R.id.goBooking);
    }

}