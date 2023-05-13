package com.example.mobileapp;

import android.location.Location;

import com.example.mobileapp.dbclasses.Homestay;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public interface DataSender {
    void sendData(List<MarkerCustomized> homestays, MapView mapView, Location myLocation);

    void sendInputToNewHomestay(String input);

}