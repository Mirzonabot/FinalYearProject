package com.example.mobileapp;

import com.example.mobileapp.dbclasses.Homestay;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MarkerCustomized extends Marker {
    private Homestay homestay;
    public MarkerCustomized(MapView mapView,Homestay homestay) {
        super(mapView);
        this.homestay = homestay;
    }

    public Homestay getHomestay() {
        return homestay;
    }

    public void setHomestay(Homestay homestay) {
        this.homestay = homestay;
    }
}
