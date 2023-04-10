package com.example.mobileapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PickLocationGoogleDialog extends DialogFragment implements OnMapReadyCallback {
    public interface OnInputListener{
        void sendInput(String lat, String lon);
    }
    private static View view;


    public PickLocationGoogleDialog.OnInputListener onInputListener;
    private String latitude;
    private String longitude;
    private GoogleMap mMap;
    private Button btnPickLocation;
    private SupportMapFragment supportMapFragment;
    public PickLocationGoogleDialog() {
        // Required empty public constructor
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        System.out.println("onCreateDialog");
        System.out.println(savedInstanceState);

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = getActivity().getLayoutInflater().inflate(R.layout.dialog_pick_location_google, null,false);
//            setSetting();
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        if (mMap != null) {
            System.out.println("mMap != null");
            setSetting();
        }
        else {
            System.out.println("mMap == null");
        }


        btnPickLocation = view.findViewById(R.id.btnPickLocation);


        btnPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInputListener.sendInput(latitude, longitude);
                getDialog().dismiss();
            }
        });

        if (supportMapFragment == null) {
            supportMapFragment = (SupportMapFragment)
                    getActivity().getSupportFragmentManager().findFragmentById(R.id.google_map);
        }


        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(getActivity());

        supportMapFragment.getMapAsync(this);
//        setSetting();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);
        return builder.create();
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        System.out.println("onMapReady");
        System.out.println("googleMap: " + googleMap);
        mMap = googleMap;
        setSetting();


    }

    private void setSetting() {
        System.out.println("setSetting is set!!");
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //set map to a location and zoom in
        float zoomLevel = 6.0f;
        GPSTracker gpsTrackerr = new GPSTracker(getActivity());
        Location myLocationn = gpsTrackerr.getLocation();
        double clon = myLocationn.getLongitude();
        double clat = myLocationn.getLatitude();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(clat,clon), zoomLevel));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                System.out.println("map clicked");
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                latitude = String.valueOf(latLng.latitude);
                longitude = String.valueOf(latLng.longitude);
            }
        });

        GPSTracker gpsTracker = new GPSTracker(getActivity());
        Location myLocation = gpsTracker.getLocation();
        System.out.println("myLocation: " + myLocation);
        if (myLocation != null) {
            System.out.println("myLocation: " + myLocation);
            double longitude = myLocation.getLongitude();
            double latitude = myLocation.getLatitude();
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("Current Location")).
                    setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            System.out.println("longitude: " + longitude);
            System.out.println("latitude: " + latitude);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onInputListener = (PickLocationGoogleDialog.OnInputListener) getActivity();
        }catch (ClassCastException e){
            System.out.println("ClassCastException: " + e.getMessage());
        }
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mMap.clear();
//        supportMapFragment.onDestroy();
//        supportMapFragment.onDestroyView();

    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
