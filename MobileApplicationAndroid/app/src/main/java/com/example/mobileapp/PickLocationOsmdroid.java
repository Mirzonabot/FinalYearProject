package com.example.mobileapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.views.MapView;

import java.io.File;


public class PickLocationOsmdroid extends DialogFragment {
//    private View view;
    private MapView mapView;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_pick_location_osmdroid,null);
        final Context ctx = view.getContext();
        System.out.println("_______________________________");
        System.out.println(mapView);
        System.out.println("_______________________________");
        mapView = view.findViewById(R.id.mapView);
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        System.out.println("_______________________________");
        System.out.println(mapView);
        System.out.println("_______________________________");
        mapView.setTileSource(TileSourceFactory.MAPNIK); // Set the tile source to OpenStreetMap
        mapView.setBuiltInZoomControls(true); // Enable built-in zoom controls
        mapView.setMultiTouchControls(true); // Enable multi-touch controls
        File tilesDir = new File(Environment.getExternalStorageDirectory().getPath() + "/tiles");
        mapView.setTileSource(new XYTileSource("MapQuest",
                0, 18, 256, ".png", new String[]{}));
        mapView.setUseDataConnection(false);
        mapView.setTileSource(new XYTileSource(tilesDir.getAbsolutePath(), 0, 18, 256, ".jpg", new String[] {}));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);
        return builder.create();
    }
}
