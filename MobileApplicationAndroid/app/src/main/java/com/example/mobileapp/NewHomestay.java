package com.example.mobileapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.dbclasses.Homestay;
import com.example.mobileapp.dbclasses.TokenUserID;
import com.example.mobileapp.fragments.PickLocationDialog;
import com.example.mobileapp.fragments.PickLocationGoogleDialog;
import com.example.mobileapp.fragments.PickLocationOsmdroid;
import com.example.mobileapp.homepages.ProviderHome;
import com.example.mobileapp.memorymanager.SqlHelper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yandex.mapkit.MapKitFactory;


public class NewHomestay extends AppCompatActivity implements PickLocationDialog.OnInputListener, PickLocationGoogleDialog.OnInputListener, PickLocationOsmdroid.OnInputListener{

    ImageView addLocaionFromMap;
    ImageView addLocationFromGoogle;

    Button addLocationFromOsmdroid;

    private String lt;
    private String ln;

    private EditText homeStayName;
    private EditText city, district, village, street;
    private EditText homeStayCapacity;

    private Button addHomestay, addImage;
    private SqlHelper sqlHelper;
    private Uri imageUri;

    private StorageReference storageReference;
    private FirebaseDatabase database;

    // Create a GestureDetector object




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("69044d6e-9641-4c53-8d74-897fb9363d17");
        setContentView(R.layout.activity_new_homestay);
        sqlHelper = new SqlHelper(this);
//        getSupportActionBar().hide();
        initInputs();
        initListeners();




        addLocationFromOsmdroid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickLocationOsmdroid pickLocationOsmdroid = new PickLocationOsmdroid();
                pickLocationOsmdroid.show(getSupportFragmentManager(),"pick location from osmdroid");
            }
        });

        FirebaseApp.initializeApp(this);

        database = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app");


        DatabaseReference myRef = database.getReference();


        final String[] ownerPhoneNumber = {null};
        database.getReference("tokenUserID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        TokenUserID tokenUserID = dataSnapshot.getValue(TokenUserID.class);
                        ownerPhoneNumber[0] = tokenUserID.getUserPhoneNumber();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        addHomestay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String homestayName = String.valueOf(homeStayName.getText());
                String city = String.valueOf(NewHomestay.this.city.getText());
                String district = String.valueOf(NewHomestay.this.district.getText());
                String village = String.valueOf(NewHomestay.this.village.getText());
                String street = String.valueOf(NewHomestay.this.street.getText());
                String homestayAddress = city + ", " + district + ", " + village + ", " + street;
                int homestayCapacity = 0;
                String ownerID = FirebaseAuth.getInstance().getCurrentUser().getUid();



                try {
                    homestayCapacity = Integer.parseInt(String.valueOf(homeStayCapacity.getText()));

                } catch (NumberFormatException e) {
                    Toast.makeText(NewHomestay.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
                if (ln == null || lt == null) {
                    Toast.makeText(NewHomestay.this, "Please select a location", Toast.LENGTH_SHORT).show();
                    return;
                }
                String homestayLatitude = lt;
                String homestayLongitude = ln;


                if (homestayName.isEmpty() || homestayAddress.isEmpty() || String.valueOf(homeStayCapacity.getText()).isEmpty() || homestayLatitude.isEmpty() || homestayLongitude.isEmpty()) {
                    Toast.makeText(NewHomestay.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Homestay homestay = new Homestay(homestayName, homestayCapacity, ownerID, homestayAddress, homestayLatitude, homestayLongitude,village,street,district,city,ownerPhoneNumber[0]);
                    sqlHelper.addHomestay(homestay);
                    uploadImageToStorage(homestay.getId());
//                    myRef.child("homestays");

                    myRef.child("homestays").push().setValue(homestay);
                    Toast.makeText(NewHomestay.this, "Homestay added", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(NewHomestay.this, ProviderHome.class));
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void initListeners() {
    }

    private void initInputs() {
//        addLocaionFromMap = (ImageView) findViewById(R.id.addLocationFromMap);
//        addLocationFromGoogle = (ImageView) findViewById(R.id.addLocationFromGoogle);
        addLocationFromOsmdroid = (Button) findViewById(R.id.addLocationFromOsmdroid);
        city = findViewById(R.id.city);
        district = findViewById(R.id.district);
        village = findViewById(R.id.village);
        street = findViewById(R.id.street);
        homeStayCapacity = findViewById(R.id.numAvailableSpaces);
        homeStayName = findViewById(R.id.homestayName);
        addHomestay = findViewById(R.id.btnAddHomestay);
        addImage = findViewById(R.id.btnAddImage);
        storageReference = FirebaseStorage.getInstance("gs://homestaybooking-f8308.appspot.com/").getReference("images/homestays");
    }

    @Override
    public void sendInput(String lat, String lon) {
        ln = lon;
        lt = lat;
        System.out.println("______________________________________________________");
        System.out.println("Latitude: " + lat);
        System.out.println("Longitude: " + lon);
        Toast.makeText(this, "Latitude: " + lat + " Longitude: " + lon, Toast.LENGTH_SHORT).show();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 100) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                ImageView imageView = findViewById(R.id.imagePreview);
                Picasso.get()
                        .load(imageUri)
                        .resize(180, 180)
                        .centerCrop()
                        .into(imageView);

            }
        }
    }

    private void uploadImageToStorage(String homestayID) {
        storageReference.child(homestayID).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(NewHomestay.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                });
    }

}