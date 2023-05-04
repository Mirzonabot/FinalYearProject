package com.example.mobileapp.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import com.example.mobileapp.dbclasses.Booking;
import com.example.mobileapp.NotificationManagerHelper;
import com.example.mobileapp.R;
import com.example.mobileapp.dbclasses.Homestay;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.example.mobileapp.memorymanager.SqlHelper;
import com.example.mobileapp.smsmanager.SMSSender;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class BookingFragement extends DialogFragment {

    private LinearLayout dateRange;
    private TextView checkInDate;
    private TextView checkOutDate;
    private EditText comments;
    private Button book;
    private String checkInDateStr;
    private String checkOutDateStr;

    private String homestayID = "";
    private String homestayName = "";
    private String ownerID = "";
    private String bookerPhoneNumber = null;
    private String homestayOwnerPhoneNumber = null;
    private DatabaseReference myRef;
    private SqlHelper sqlHelper;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.booking_fragment, null, false);
        init(view);
        initListners();

        FirebaseApp.initializeApp(getActivity());

        myRef = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        Bundle bundle = getArguments();
        if (bundle != null) {
            String args = bundle.getString("homestay");
            if (args != null) {
                System.out.println(args);
                List<String> items = Arrays.asList(args.split("\\s*,\\s*"));
                System.out.println(items.toString());
                homestayID = items.get(0);
                ownerID = items.get(1);
                homestayName = items.get(2);
                homestayOwnerPhoneNumber = items.get(3);
            }

        }
        System.out.println(homestayID);
        System.out.println(ownerID);



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);
        return builder.create();
    }

    private void initListners() {
        sqlHelper  = new SqlHelper(getActivity());
        dateRange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setTitleText("Select check in and check out dates");
                builder.setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build());
                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override public void onPositiveButtonClick(Pair<Long,Long> selection) {
                        Long startDate = selection.first;
                        Long endDate = selection.second;
                        Calendar startDateDF = Calendar.getInstance();
                        startDateDF.setTimeInMillis(startDate);
                        Calendar endDateDF = Calendar.getInstance();
                        endDateDF.setTimeInMillis(endDate);
                        checkInDate.setText(startDateDF.get(Calendar.DAY_OF_MONTH) + "/" + (startDateDF.get(Calendar.MONTH) + 1) + "/" + startDateDF.get(Calendar.YEAR));
                        checkInDateStr = startDateDF.get(Calendar.DAY_OF_MONTH) + "/" + (startDateDF.get(Calendar.MONTH) + 1) + "/" + startDateDF.get(Calendar.YEAR);
                        checkOutDate.setText(endDateDF.get(Calendar.DAY_OF_MONTH) + "/" + (endDateDF.get(Calendar.MONTH) + 1) + "/" + endDateDF.get(Calendar.YEAR));
                        checkOutDateStr = endDateDF.get(Calendar.DAY_OF_MONTH) + "/" + (endDateDF.get(Calendar.MONTH) + 1) + "/" + endDateDF.get(Calendar.YEAR);
                    }
                });

                materialDatePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

                materialDatePicker.show(getFragmentManager(), "DATE_PICKER");
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String comment = comments.getText().toString();
                Booking booking = new Booking(checkInDateStr,checkOutDateStr,userName,userID,homestayID,homestayName,ownerID,SharedPreferences.getPhoneNumber(getContext()),homestayOwnerPhoneNumber);
                sqlHelper.createMyBooking(booking);
                if (SharedPreferences.isInternetAvailable(getContext())) {
                    myRef.child("booking").push().setValue(booking);
                    DatabaseReference tokenRef = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("tokenUserID");
                    Query query = tokenRef.orderByChild("userID").equalTo(booking.getOwnerID());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String token = dataSnapshot.child("token").getValue(String.class);
                                System.out.println(token);
                                // send notification to the owner
                                String title = "Booking request";
                                String body = "You have a new request at " + booking.getHomestayName();

                                NotificationManagerHelper.sendNotification(token, title, body,"newBooking",booking.getBookingID());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    // get the token of the owner
                    DatabaseReference tokenRef1 = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("tokenUserID");
                    Query query1 = tokenRef1.orderByChild("ownerID").equalTo(ownerID);
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String token = dataSnapshot.child("token").getValue(String.class);
                                System.out.println(token);
                                // send notification to the owner
                                String title = "New Booking";
                                String body = "You have a new booking for " + homestayName;
                                NotificationManagerHelper.sendNotification(token, title, body,"newBooking",booking.getBookingID());

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    Homestay homestay = sqlHelper.getOfflineHomestayById(booking.getHomestayID());
                    SMSSender.smsNewBooking(booking,homestay);
                }
                Toast.makeText(getActivity(), "Booking Successful", Toast.LENGTH_SHORT).show();
                dismiss();



            }
        });
    }

    private void init(View view) {
        dateRange = view.findViewById(R.id.dateRange);
        checkInDate = view.findViewById(R.id.textview2);
        checkOutDate = view.findViewById(R.id.textview4);
        comments = view.findViewById(R.id.comment);
        book = view.findViewById(R.id.book);
    }
}
