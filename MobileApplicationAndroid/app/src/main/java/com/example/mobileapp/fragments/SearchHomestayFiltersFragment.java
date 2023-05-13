package com.example.mobileapp.fragments;




import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import com.example.mobileapp.GPSTracker;
import com.example.mobileapp.R;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.example.mobileapp.smsmanager.SMSSender;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Locale;

public class SearchHomestayFiltersFragment extends DialogFragment {

    public interface OnInputListener{
        void sendInput(String checkIn, String checkOut, String address, String searchBy, String distance);
    }
    public SearchHomestayFiltersFragment.OnInputListener onInputListener;

    public SearchHomestayFiltersFragment() {
        // Required empty public constructor
    }

    private LinearLayout btnPickDateRAnge;
    private LinearLayout addressLayout;
    private TextView checkInDate;
    private TextView checkOutDate;
    private ToggleButton toggleButton;
    private Spinner spinner;
    private EditText byKm, address;
    private Button searchBtn, loadBtn;
    private GPSTracker gpsTracker;
    private String checkInDateStr, checkOutDateStr;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Locale locale = new Locale("tg"); // replace "fr" with the desired language code
        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        Context context = getActivity().createConfigurationContext(config);
        gpsTracker = new GPSTracker(context);
        View view = getActivity().getLayoutInflater().inflate(R.layout.search_homestay_filters, null,false);
        init(view);
        initListerners();

        if (SharedPreferences.isInternetAvailable(getActivity())){
            loadBtn.setVisibility(View.GONE);
        }
        else {
            loadBtn.setVisibility(View.VISIBLE);
        }




        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view);
        return builder.create();
    }

    private void initListerners() {
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggleButton.isChecked()){
                    addressLayout.setVisibility(View.GONE);
                }else{
                    addressLayout.setVisibility(View.VISIBLE);
                }
                if(toggleButton.isChecked()){
                    byKm.setVisibility(View.VISIBLE);
                }else{
                    byKm.setVisibility(View.GONE);
                }
            }
        });

        btnPickDateRAnge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setTitleText("Select check in and check out dates");
                builder.setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build());
                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override public void onPositiveButtonClick(Pair<Long,Long> selection) {
                        Long startDate = selection.first;
                        Long endDate = selection.second;
                        Calendar startDateDF = Calendar.getInstance();
                        startDateDF.setTimeInMillis(startDate);
                        Calendar endDateDF = Calendar.getInstance();
                        endDateDF.setTimeInMillis(endDate);
                        checkInDate.setText(startDateDF.get(Calendar.DAY_OF_MONTH) + "/" + (startDateDF.get(Calendar.MONTH) + 1) + "/" + startDateDF.get(Calendar.YEAR));
                        checkInDateStr = startDateDF.get(Calendar.DAY_OF_MONTH) + "/" + (startDateDF.get(Calendar.MONTH) + 1) + "/" + startDateDF.get(Calendar.YEAR);
                        checkOutDate.setText(endDateDF.get(Calendar.DAY_OF_MONTH) + "/" + (endDateDF.get(Calendar.MONTH) + 1) + "/" + endDateDF.get(Calendar.YEAR));
                        checkOutDateStr = endDateDF.get(Calendar.DAY_OF_MONTH) + "/" + (endDateDF.get(Calendar.MONTH) + 1) + "/" + endDateDF.get(Calendar.YEAR);
                    }
                });

                materialDatePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

                materialDatePicker.show(getFragmentManager(), "DATE_PICKER");

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String checkIn = checkInDate.getText().toString();
                String checkOut = checkOutDate.getText().toString();
                System.out.println("checkIn: " + checkIn);
                System.out.println("checkOut: " + checkOut);

                    String sortBy = spinner.getSelectedItem().toString();
                    System.out.println("sortBy: " + sortBy);
                    String addres = address.getText().toString();
                    System.out.println("address: " + addres);

                    String byKmm = byKm.getText().toString();
                    System.out.println("byKm: " + byKmm);



                if (!checkIn.isEmpty() && !checkOut.isEmpty() && ((!addres.isEmpty() && !sortBy.isEmpty()) || !byKmm.isEmpty())) {
                    onInputListener.sendInput(checkIn, checkOut, addres, sortBy, byKmm);
                    dismiss();
                }
                else {
                    Snackbar.make(view, "Please fill all the necessary  fields", Snackbar.LENGTH_LONG).setAnchorView(R.id.search)
                            .show();

                }
            }
        });

        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sortBy = spinner.getSelectedItem().toString();
                System.out.println("sortBy: " + sortBy);
                String addres = address.getText().toString();
                System.out.println("address: " + addres);

                String byKmm = byKm.getText().toString();
                System.out.println("byKm: " + byKmm);



                if (((!addres.isEmpty() && !sortBy.isEmpty()) || !byKmm.isEmpty())) {
                    SMSSender.getHomestaysFromServer(getActivity(), sortBy, addres, byKmm,String.valueOf(gpsTracker.getLatitude()),String.valueOf(gpsTracker.getLongitude()));
//                    onInputListener.sendInput(checkIn, checkOut, addres, sortBy, byKmm);
//                    dismiss();
                }
                else {
                    Snackbar.make(view, "Please fill all the necessary  fields", Snackbar.LENGTH_LONG).setAnchorView(R.id.search)
                            .show();

                }


            }
        });


    }

    private void init(View view) {
        btnPickDateRAnge = view.findViewById(R.id.pickDateRange);
        addressLayout = view.findViewById(R.id.address_layout);
        checkInDate = view.findViewById(R.id.textview2);
        checkOutDate = view.findViewById(R.id.textview4);
        toggleButton = view.findViewById(R.id.address_toggle_button);
        byKm = view.findViewById(R.id.by_km);
        spinner = view.findViewById(R.id.dropdown);
        searchBtn = view.findViewById(R.id.search);
        loadBtn = view.findViewById(R.id.load);
        address = view.findViewById(R.id.searchTerm);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onInputListener = (SearchHomestayFiltersFragment.OnInputListener) getActivity();
            System.out.println("all set");
            System.out.println("onAttach: " + onInputListener);
        }catch (ClassCastException e){
            System.out.println("not set");
            System.out.println("ClassCastException: " + e.getMessage());
        }
    }

}

