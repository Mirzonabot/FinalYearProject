package com.example.mobileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapp.dbclasses.TokenUserID;
import com.example.mobileapp.homepages.Home;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class Signup extends AppCompatActivity {

    TextView alreadyHaveAccount;

    private static final String TAG = MainActivity.class.getSimpleName();
    EditText mFullName,mEmail,mphoneNumber,mPassword,confirmPassword;
    Button mRegisterBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        mRegisterBtn = findViewById(R.id.btnRegister);

        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.registerEmail);
        mphoneNumber = findViewById(R.id.phoneNumber);
        mPassword = findViewById(R.id.registerPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        progressBar = findViewById(R.id.progressBar);



        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Register.this,Login.class);
//                startActivity(intent);
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName, email,password, confPassword;

                fullName = mFullName.getText().toString();
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                confPassword = confirmPassword.getText().toString();

                if (TextUtils.isEmpty(fullName)){
                    mFullName.setError("Please Add Fullname!");
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required!");
                    return;
                }

                if (!email.matches(emailPattern)){
                    mEmail.setError("Enter Correct Email!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password is empty!");
                    return;
                }
                if (!email.matches(emailPattern)){
                    mEmail.setError("Provide Phone number!");
                    return;
                }
                if (!TextUtils.equals(password,confPassword)){
                    confirmPassword.setError("Passwords didn't match");
                    return;
                }




                System.out.println("you are in 1");

                fAuth = FirebaseAuth.getInstance();
                System.out.println(fAuth);
                System.out.println("you are in 2");
                progressBar.setVisibility(View.VISIBLE);
                System.out.println("you are in 3");
                System.out.println("you are in 5");
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    //                    System.out.println("you are in before 6");
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("you are in 6");
                        if (task.isSuccessful()) {

                            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build();
                            user1.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });

                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app");

                            DatabaseReference myRef = database.getReference("tokenUserID");

                            FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                            if (!task.isSuccessful()) {
                                                System.out.println("getInstanceId failed " + task.getException());
                                                return;
                                            }

                                            if (task.isSuccessful()) {
                                                String token = task.getResult().getToken();


                                                TokenUserID tokenUserID = new TokenUserID(user1.getUid(),fullName,user1.getEmail(),mphoneNumber.getText().toString(),token);
                                                SharedPreferences.setPhoneNumber(Signup.this,mphoneNumber.getText().toString());
//                                                myRef.child("tokenUserID").push().setValue();
                                                myRef.child(user1.getUid()).setValue(tokenUserID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(Signup.this, "Token saved", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(Signup.this, "Token not saved", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                // Handle error getting token ID
                                            }
                                        }
                                    });

                            myRef.child(user1.getUid()).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                                    TokenUserID user = snapshot.getValue(TokenUserID.class);
                                    System.out.println("______________________");
                                    System.out.println(user);
                                    System.out.println("______________________");
                                    SharedPreferences.setPhoneNumber(Signup.this, user.getUserPhoneNumber());
                                    SharedPreferences.setUserName(Signup.this, user.getUserName());
                                    SharedPreferences.setEmail(Signup.this, user.getUserEmail());
                                    SharedPreferences.setUserId(Signup.this, user.getUserID());
                                    startActivity(new Intent(getApplicationContext(), SampleActivity.class));
                                }

                                @Override
                                public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {

                                }
                            });
                            System.out.println("you are in 7");
                            Toast.makeText(Signup.this,"User created!",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);





                        }else {
                            System.out.println("you are in 8");
                            Toast.makeText(Signup.this,"error!"+task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });


    }
}