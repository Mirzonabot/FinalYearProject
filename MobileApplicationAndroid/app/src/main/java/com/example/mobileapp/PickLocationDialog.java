package com.example.mobileapp;


import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.GeoObjectTapEvent;
import com.yandex.mapkit.layers.GeoObjectTapListener;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapLoadStatistics;
import com.yandex.mapkit.map.MapLoadedListener;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.mapview.MapView;




public class PickLocationDialog extends DialogFragment {
    private static final String TAG = "PickLocationDialog";
    private MapView mapView;



    private InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
            System.out.println("Tapped!!!!!!!!!!!!!!!");
            System.out.println("Latitude: " + point.getLatitude());
            System.out.println("Longitude: " + point.getLongitude());
            map.getMapObjects().addPlacemark(point);

        }

        @Override
        public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
            System.out.println("Long tapped!!!!!!!!!!!!!!!");
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        MapKitFactory.initialize(getActivity());


        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_pick_location,null);
        mapView = view.findViewById(R.id.mapView);

        mapView.showContextMenu();



        mapView.getMap().move(
                new CameraPosition(new Point(38.8582, 71.2480), 5.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        mapView.getMap().setMapType(MapType.VECTOR_MAP);

        mapView.getMap().addInputListener(inputListener);



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);

        return builder.create();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("finalized!!!!!");
        super.finalize();
    }
}
