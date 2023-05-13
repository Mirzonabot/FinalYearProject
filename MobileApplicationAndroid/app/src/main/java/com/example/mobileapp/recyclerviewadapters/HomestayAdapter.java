package com.example.mobileapp.recyclerviewadapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.dbclasses.Homestay;
import com.example.mobileapp.HomestayCalender;
import com.example.mobileapp.R;
import com.example.mobileapp.fragments.ViewOnMap;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomestayAdapter extends RecyclerView.Adapter<HomestayAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Homestay> homestayList;
    private DatabaseReference databaseReference;
    private Boolean mainHome = false;


    public HomestayAdapter(Context mContext, ArrayList<Homestay> homestayList, Boolean mainHome) {
        this.mContext = mContext;
        this.mainHome = mainHome;
        this.homestayList = homestayList;
        this.databaseReference = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app").getReference("homestays");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (mainHome) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_my_hostel, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lis_item_my_hostel, parent, false);
        }
//        LayoutInflater.from(parent.getContext()).inflate(R.layout.lis_item_my_hostel,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Homestay homestay = homestayList.get(position);

        StorageReference storageReference = FirebaseStorage.getInstance("gs://homestaybooking-f8308.appspot.com/").getReference("images/homestays/"+ homestay.getId());


        if (!mainHome) {
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
                    dialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), "pick location");
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
        } else {
            holder.homestayName.setText(homestay.getHomestayName());
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get()
                        .load(uri)
                        .resize(180, 180)
                        .centerCrop()
                        .into(holder.homestayImage);
            });
        }

    }

    @Override
    public int getItemCount() {
        return homestayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView homestayName, homestayAddress, homestayCapacity, homestayOnMap, homestayCalendar;
        private ImageView homestayImage;
        ToggleButton toggleButton;

        public ViewHolder(View itemView) {
            super(itemView);
            if (!mainHome) {
                homestayName = itemView.findViewById(R.id.tv_name);
                homestayAddress = itemView.findViewById(R.id.tv_address);
                homestayCapacity = itemView.findViewById(R.id.tv_capacity);
                homestayOnMap = itemView.findViewById(R.id.btn_view_on_map);
                toggleButton = itemView.findViewById(R.id.tb_availability);
                homestayCalendar = itemView.findViewById(R.id.btn_calendar);
            } else {
                homestayName = itemView.findViewById(R.id.titleTextView);
                homestayImage = itemView.findViewById(R.id.imageView);
            }


        }
    }

    public void setHomestayList(ArrayList<Homestay> homestayList) {
        this.homestayList = homestayList;
        notifyDataSetChanged();
    }

}
