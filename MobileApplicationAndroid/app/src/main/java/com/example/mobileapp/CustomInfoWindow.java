package com.example.mobileapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;


import com.example.mobileapp.dbclasses.Homestay;
import com.example.mobileapp.fragments.BookingFragement;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.example.mobileapp.memorymanager.SqlHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.text.DecimalFormat;

public class CustomInfoWindow extends InfoWindow {
    private Context mContext;
    private Marker mMarker;

    private Homestay homestay;
    private TextView titleTextView, addressTextView, capacityTextView, distanceTextView;
    private FragmentManager supportFragmentManager;
    private Button addToOffline;
    private SqlHelper sqlHelper;
    private ImageView homestayImage;
    private StorageReference storageReference;
    private MapView mapView;

    public CustomInfoWindow(Context context, MapView mapView, FragmentManager supportFragmentManager, Homestay homestay) {
//        System.out.println("CustomInfoWindow");
        super(R.layout.custom_info_window, mapView);
        System.out.println("CustomInfoWindow");
        mContext = context;
        this.homestay = homestay;
        this.mapView = mapView;
        storageReference = FirebaseStorage.getInstance("gs://homestaybooking-f8308.appspot.com/").getReference("images/homestays/"+homestay.getId());
        this.supportFragmentManager = supportFragmentManager;
        System.out.println("CustomInfoWindow");
        sqlHelper = new SqlHelper(context,null,null);
    }

    @Override
    public void onOpen(Object item) {
        InfoWindow.closeAllInfoWindowsOn(mapView);
        mMarker = (Marker) item;

        titleTextView = mView.findViewById(R.id.titleTextView);
        addressTextView = mView.findViewById(R.id.addressTextView);
        capacityTextView = mView.findViewById(R.id.capacityTextView);
        distanceTextView = mView.findViewById(R.id.distanceTextView);
        addToOffline = mView.findViewById(R.id.addToOffline);
        homestayImage = mView.findViewById(R.id.homestayCardView);

        if (!SharedPreferences.isInternetAvailable(mContext) || sqlHelper.homestayIsInOfflineTable(homestay.getId()))
        {
            addToOffline.setVisibility(View.GONE);
        }

        titleTextView.setText(mMarker.getTitle());
        DecimalFormat df = new DecimalFormat("#.##");

        String formatted = df.format(Double.parseDouble(mMarker.getSnippet()));

        distanceTextView.setText("Distance: " + formatted+" km");
        addressTextView.setText("Address: " + homestay.getAddress());
        capacityTextView.setText("Capacity: " + homestay.getHomestayCapacity());

        MaterialCardView relativeLayout = mView.findViewById(R.id.relativeLayout);


        try {
            if (storageReference != null) {
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(homestayImage);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("homestay", (String) (homestay.getId() + "," + homestay.getOwnerId() + "," + homestay.getHomestayName())+","+homestay.getHomestayPhoneNumber());
                BookingFragement dialog = new BookingFragement();
                dialog.setArguments(bundle);
                dialog.show(supportFragmentManager, "pick location");
            }
        });

        addToOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Added to offline", Toast.LENGTH_SHORT).show();
                addToOffline.setVisibility(View.GONE);
                sqlHelper.addHomestayToOfflineTable(homestay);
            }
        });



    }

    @Override
    public void onClose() {
        // Remove the info window here
    }




}
