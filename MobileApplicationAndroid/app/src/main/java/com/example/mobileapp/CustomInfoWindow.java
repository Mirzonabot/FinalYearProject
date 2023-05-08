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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class CustomInfoWindow extends InfoWindow {
    private Context mContext;
    private Marker mMarker;

    private Homestay homestay;
    private TextView titleTextView;
    private TextView snippetTextView;
    private FragmentManager supportFragmentManager;
    private Button addToOffline;
    private SqlHelper sqlHelper;
    private ImageView homestayImage;
    private StorageReference storageReference;

    public CustomInfoWindow(Context context, MapView mapView, FragmentManager supportFragmentManager, Homestay homestay) {
//        System.out.println("CustomInfoWindow");
        super(R.layout.custom_info_window, mapView);
        System.out.println("CustomInfoWindow");
        mContext = context;
        this.homestay = homestay;
        storageReference = FirebaseStorage.getInstance("gs://homestaybooking-f8308.appspot.com/").getReference("images/homestays/"+homestay.getId());
        this.supportFragmentManager = supportFragmentManager;
        System.out.println("CustomInfoWindow");
        sqlHelper = new SqlHelper(context);
    }

    @Override
    public void onOpen(Object item) {
        System.out.println("onOpen");
        System.out.println(homestay);
        mMarker = (Marker) item;

        titleTextView = mView.findViewById(R.id.titleTextView);
        snippetTextView = mView.findViewById(R.id.snippetTextView);
        addToOffline = mView.findViewById(R.id.addToOffline);
        homestayImage = mView.findViewById(R.id.homestayImageView);

        if (!SharedPreferences.isInternetAvailable(mContext) || sqlHelper.homestayIsInOfflineTable(homestay.getId()))
        {
            addToOffline.setVisibility(View.GONE);
        }

        titleTextView.setText(mMarker.getTitle());
        snippetTextView.setText(mMarker.getSnippet());

        RelativeLayout relativeLayout = mView.findViewById(R.id.relativeLayout);


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
