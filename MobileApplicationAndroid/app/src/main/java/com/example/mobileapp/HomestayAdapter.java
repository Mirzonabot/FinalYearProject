package com.example.mobileapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomestayAdapter extends RecyclerView.Adapter<HomestayAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Homestay> homestayList;
    private DatabaseReference databaseReference;

    public HomestayAdapter(Context mContext, ArrayList<Homestay> homestayList) {
        this.mContext = mContext;
        this.homestayList = homestayList;
        this.databaseReference = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("homestays");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lis_item_my_hostel,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Homestay homestay = homestayList.get(position);
        holder.homestayName.setText(homestay.getHomestayName());
        holder.homestayAddress.setText(homestay.getAddress());
        holder.homestayCapacity.setText(String.valueOf(homestay.getHomestayCapacity()));
        holder.homestayOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewOnMap dialog = new ViewOnMap();
                Bundle bundle = new Bundle();
                bundle.putString("lat", homestay.getLatitude());
                bundle.putString("lon", homestay.getLongitude());
                dialog.setArguments(bundle);
                dialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), "pick location");
            }
        });
        holder.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = databaseReference.orderByChild("id").equalTo(homestay.getId());
                if (holder.toggleButton.isChecked()) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                dataSnapshot.getRef().child("availability").setValue(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                dataSnapshot.getRef().child("availability").setValue(true);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        if (!homestay.isAvailability()) {
            holder.toggleButton.setChecked(true);
        }

        holder.homestayCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, HomestayCalender.class));
            }
        });


    }

    @Override
    public int getItemCount() {
        return homestayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView homestayName, homestayAddress, homestayCapacity,homestayOnMap,homestayCalendar;
        ToggleButton toggleButton;

        public ViewHolder(View itemView) {
            super(itemView);
            homestayName = itemView.findViewById(R.id.tv_name);
            homestayAddress = itemView.findViewById(R.id.tv_address);
            homestayCapacity = itemView.findViewById(R.id.tv_capacity);
            homestayOnMap = itemView.findViewById(R.id.btn_view_on_map);
            toggleButton = itemView.findViewById(R.id.tb_availability);
            homestayCalendar = itemView.findViewById(R.id.btn_calendar);


        }
    }

    public void setHomestayList(ArrayList<Homestay> homestayList) {
        this.homestayList = homestayList;
        notifyDataSetChanged();
    }

}
