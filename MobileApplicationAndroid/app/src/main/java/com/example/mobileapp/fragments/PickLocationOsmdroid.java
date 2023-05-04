package com.example.mobileapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.mobileapp.BuildConfig;
import com.example.mobileapp.GPSTracker;
import com.example.mobileapp.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.List;


public class PickLocationOsmdroid extends DialogFragment implements MapEventsReceiver {
//    private View view;
    private MapView mapView;
    private Button btnPickLocation;



    public interface OnInputListener{
        void sendInput(String lat, String lon);
    }
    public PickLocationOsmdroid.OnInputListener onInputListener;

    private String latitude;
    private String longitude;
    public PickLocationOsmdroid() {
        // Required empty public constructor
    }





    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_pick_location_osmdroid,null);
        final Context ctx = view.getContext();
        mapView = view.findViewById(R.id.mapView);
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getContext(), this);
        mapView.getOverlays().add(0, mapEventsOverlay);

        btnPickLocation = view.findViewById(R.id.btnPickLocation);

        mapView.setTileSource(TileSourceFactory.MAPNIK); // Set the tile source to OpenStreetMap
        mapView.setBuiltInZoomControls(true); // Enable built-in zoom controls
        mapView.setMultiTouchControls(true); // Enable multi-touch controls
        mapView.setZ(10);
        GPSTracker gpsTracker = new GPSTracker(getContext());
        mapView.setExpectedCenter(new GeoPoint(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
        // Create a GestureDetector object

        btnPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInputListener.sendInput(latitude, longitude);
                getDialog().dismiss();
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onInputListener = (PickLocationOsmdroid.OnInputListener) getActivity();
        }catch (ClassCastException e){
            System.out.println("ClassCastException: " + e.getMessage());
        }
    }
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        System.out.println("singleTapConfirmedHelper");
        List<Overlay> overlays = mapView.getOverlays();
        for (Overlay overlay : overlays) {
            if (overlay instanceof Marker) {
                mapView.getOverlays().remove(overlay);
            }
        }
        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(p);
        latitude = String.valueOf(p.getLatitude());
        longitude = String.valueOf(p.getLongitude());
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        mapView.getOverlays().add(startMarker);
        mapView.invalidate();
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        System.out.println("longPressHelper");
        return false;
    }
}
