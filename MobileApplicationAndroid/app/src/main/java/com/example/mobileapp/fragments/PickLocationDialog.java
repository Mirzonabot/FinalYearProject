package com.example.mobileapp.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.mobileapp.R;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;

import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;

import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.mapview.MapView;




public class PickLocationDialog extends DialogFragment {

    public interface OnInputListener{
        void sendInput(String lat, String lon);
    }

    public OnInputListener onInputListener;

    private static final String TAG = "PickLocationDialog";
    private MapView mapView;
    private Button pickLocationButton;
    private String latitude;
    private String longitude;



    private InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
            System.out.println("Tapped!!!!!!!!!!!!!!!");
            System.out.println("Latitude: " + point.getLatitude());
            System.out.println("Longitude: " + point.getLongitude());
            latitude = String.valueOf(point.getLatitude());
            longitude = String.valueOf(point.getLongitude());
            map.getMapObjects().clear();
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
        pickLocationButton = view.findViewById(R.id.btnPickLocation);
        mapView.showContextMenu();



        mapView.getMap().move(
                new CameraPosition(new Point(38.8582, 71.2480), 5.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        mapView.getMap().setMapType(MapType.VECTOR_MAP);

        mapView.getMap().addInputListener(inputListener);

        pickLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("Clicked!!!!!!!!!!!!!!!");
//                System.out.println(mapView.getMap().);
                System.out.println(mapView.getMap().getCameraPosition().getTarget().getLongitude());
                System.out.println(mapView.getMap().getCameraPosition().getTarget().getLatitude());

                //  pass latitude and longitude to the activity
                onInputListener.sendInput(latitude,longitude);
                getDialog().dismiss();


            }
        });

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onInputListener = (OnInputListener) getActivity();
        }catch (ClassCastException e){
            System.out.println("ClassCastException: " + e.getMessage());
        }
    }
}
