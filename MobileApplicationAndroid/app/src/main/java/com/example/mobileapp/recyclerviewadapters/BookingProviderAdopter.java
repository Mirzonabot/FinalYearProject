package com.example.mobileapp.recyclerviewadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.dbclasses.Booking;
import com.example.mobileapp.NotificationManagerHelper;
import com.example.mobileapp.R;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.example.mobileapp.memorymanager.SqlHelper;
import com.example.mobileapp.smsmanager.SMSSender;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookingProviderAdopter extends RecyclerView.Adapter<BookingProviderAdopter.ViewHolder> {

    private Context mContext;

    private ArrayList<Booking> bookingList;
    private SqlHelper sqlHelper;
    private DatabaseReference databaseReference;

    public BookingProviderAdopter(Context mContext, ArrayList<Booking> bookingList) {
        this.mContext = mContext;
        this.bookingList = bookingList;
        this.databaseReference = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("booking");;
        this.sqlHelper = new SqlHelper(mContext,null,null);
        System.out.println("urreeee");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_provider_booking,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        System.out.println(booking.getCheckInDate());
        System.out.println("urreeee1111");
        holder.checkInDate.setText("Check-in: "+booking.getCheckInDate());
        holder.checkOutDate.setText("Check-out: " + booking.getCheckOutDate());

        if (booking.isBooked()) {
            holder.acceptBooking.setVisibility(View.GONE);
            holder.rejectBooking.setVisibility(View.GONE);
            holder.cancelBooking.setVisibility(View.VISIBLE);
            if (!booking.isCancelled()) {
                holder.cancelBooking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sqlHelper.cancelBookingInMyHomestays(booking.getBookingID());
                        Query query = databaseReference.orderByChild("bookingID").equalTo(booking.getBookingID());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    dataSnapshot.getRef().child("cancelled").setValue(true);
                                    DatabaseReference tokenRef = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("tokenUserID");
                                    Query query = tokenRef.orderByChild("userID").equalTo(booking.getUserID());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                String token = dataSnapshot.child("token").getValue(String.class);
                                                System.out.println(token);
                                                // send notification to the owner
                                                String title = "Booking Cancelled";
                                                String body = "cancelled booking at " + booking.getHomestayName();

                                                NotificationManagerHelper.sendNotification(token,title,body,"cancelledByProvider",booking.getBookingID());
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        holder.cancelBooking.setEnabled(false);
                    }
                });
            }

        }

        if (booking.isCancelled() == true) {
            holder.acceptBooking.setVisibility(View.GONE);
            holder.rejectBooking.setVisibility(View.GONE);
            holder.cancelBooking.setVisibility(View.VISIBLE);
            holder.cancelBooking.setEnabled(false);
            holder.cancelBooking.setText("Cancelled");
        }

        if (booking.isRejected() == true) {
            holder.acceptBooking.setVisibility(View.GONE);
            holder.rejectBooking.setVisibility(View.GONE);
            holder.cancelBooking.setVisibility(View.VISIBLE);
            holder.cancelBooking.setEnabled(false);
            holder.cancelBooking.setText("Rejected");
        }

        holder.acceptBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sqlHelper.acceptBooking(booking.getBookingID());
                if (SharedPreferences.isInternetAvailable(mContext)) {
                    Query query = databaseReference.orderByChild("bookingID").equalTo(booking.getBookingID());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                dataSnapshot.getRef().child("booked").setValue(true);
                                DatabaseReference tokenRef = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("tokenUserID");
                                Query query = tokenRef.orderByChild("userID").equalTo(booking.getUserID());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            String token = dataSnapshot.child("token").getValue(String.class);
                                            System.out.println(token);
                                            // send notification to the owner
                                            String title = "Booking accepted";
                                            String body = "Accepted booking for " + booking.getHomestayName();

                                            NotificationManagerHelper.sendNotification(token, title, body, "ownerAccepted", booking.getBookingID());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {

                    SMSSender.smsAcceptBooking(booking);
                }
                holder.rejectBooking.setEnabled(false);
            }
        });

        holder.rejectBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlHelper.rejectBookingInMyHomestays(booking.getBookingID());
                Query query = databaseReference.orderByChild("bookingID").equalTo(booking.getBookingID());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataSnapshot.getRef().child("rejected").setValue(true);
                            // notify user
                            DatabaseReference tokenRef = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("tokenUserID");
                            Query query = tokenRef.orderByChild("userID").equalTo(booking.getUserID());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        String token = dataSnapshot.child("token").getValue(String.class);
                                        System.out.println(token);
                                        // send notification to the owner
                                        String title = "Booking Rejected";
                                        String body = "Rejected booking for " + booking.getHomestayName();

                                        NotificationManagerHelper.sendNotification(token,title,body,"rejectedByProvider",booking.getBookingID());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.acceptBooking.setEnabled(false);
            }
        });
        holder.userName.setText("By: " + booking.getNameBookedBy());

    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView checkInDate,   checkOutDate, userName;
        private Button acceptBooking, rejectBooking,cancelBooking;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkInDate = itemView.findViewById(R.id.checkin);
            checkOutDate = itemView.findViewById(R.id.checkout);
            acceptBooking = itemView.findViewById(R.id.btn_accept);
            rejectBooking = itemView.findViewById(R.id.btn_reject);
            userName = itemView.findViewById(R.id.userBookedBy);
            cancelBooking = itemView.findViewById(R.id.btn_cancel);
        }
    }

    public void setBookingList(ArrayList<Booking> bookingList) {
        this.bookingList = bookingList;
        notifyDataSetChanged();
    }
}
