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
import com.example.mobileapp.memorymanager.SqlHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookingCustomerAdopter extends RecyclerView.Adapter<BookingCustomerAdopter.ViewHolder> {

    private Context mContext;
    private ArrayList<Booking> bookingList;
    private DatabaseReference databaseReference;
    private SqlHelper sqlHelper;

    public BookingCustomerAdopter(Context mContext, ArrayList<Booking> bookingList) {
        this.mContext = mContext;
        this.bookingList = bookingList;
        this.databaseReference = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("booking");
        sqlHelper = new SqlHelper(mContext,null,null);
        System.out.println("urreeee");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_customer_booking,parent,false);
        return new BookingCustomerAdopter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        System.out.println(booking.getCheckInDate());
        System.out.println("urreeee1111");
        holder.checkInDate.setText("Check-in: "+booking.getCheckInDate());
        holder.checkOutDate.setText("Check-out: "+booking.getCheckOutDate());
        holder.userName.setText("At" + booking.getHomestayName());
        if (booking.isBooked()) {
            holder.statusBooking.setText("Cancel");
            holder.statusBooking.setEnabled(true);
            holder.statusBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sqlHelper.cancelMyBookingInOtherHomestays(booking.getBookingID());
                    Query query = databaseReference.orderByChild("bookingID").equalTo(booking.getBookingID());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                dataSnapshot.getRef().child("isCancelled").setValue(true);
                                DatabaseReference tokenRef = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("tokenUserID");
                                Query query = tokenRef.orderByChild("userID").equalTo(booking.getUserID());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                            String token = dataSnapshot.child("token").getValue(String.class);
                                            System.out.println(token);
                                            // send notification to the owner
                                            String title = "Booking cancelled";
                                            String body = "Cancelled booking for " + booking.getHomestayName();

                                            NotificationManagerHelper.sendNotification(token,title,body,"cancelledByCustomer",booking.getBookingID());
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
                    holder.statusBooking.setEnabled(false);
                    holder.statusBooking.setText("CAncelled");
                }
            });
        }
        if (booking.isCancelled()){
            holder.statusBooking.setEnabled(false);
            holder.statusBooking.setText("Cancelled");
        } else if (booking.isRejected()) {
            holder.statusBooking.setEnabled(false);
            holder.statusBooking.setText("Rejected!");
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView checkInDate,   checkOutDate, userName;
        private Button statusBooking;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkInDate = itemView.findViewById(R.id.checkin);
            checkOutDate = itemView.findViewById(R.id.checkout);
            statusBooking = itemView.findViewById(R.id.btn_status);
            userName = itemView.findViewById(R.id.bookedAt);
        }
    }

    public void setBookingList(ArrayList<Booking> bookingList) {
        this.bookingList = bookingList;
        notifyDataSetChanged();
    }
}
