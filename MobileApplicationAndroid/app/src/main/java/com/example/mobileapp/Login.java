package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.dbclasses.TokenUserID;
import com.example.mobileapp.homepages.Home;
import com.example.mobileapp.memorymanager.SharedPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class Login extends AppCompatActivity {
    TextView createNewAccount;
    EditText mEmail, mPassword;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    Button btnLogin, btnLoginGoogle, forgotPassword;

    // email validation pattern
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final String TOKENS_NODE = "tokenUserID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        createNewAccount = findViewById(R.id.create_new_account);
        btnLogin = findViewById(R.id.button_login);
        mEmail = findViewById(R.id.edit_text_username);
        mPassword = findViewById(R.id.edit_text_password);
        progressBar = findViewById(R.id.progressBar);


        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        // check if user is already logged in
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), SampleActivity.class));
        }

        // create new account
        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });

        // login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required!");
                    return;
                }

                if (!email.matches(emailPattern)) {
                    mEmail.setError("Enter Correct Email!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                            saveToken();
                            startActivity(new Intent(getApplicationContext(), SampleActivity.class));
                        } else {
                            Toast.makeText(Login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });


    }

    private void saveToken() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        System.out.println("getInstanceId failed " + task.getException());
                                        return;
                                    }

                                    String token = task.getResult().getToken();
                                    if (task.isSuccessful()) {
//                                        String s = task.getResult().getToken();

                                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app");
                                        //reference to the token node
                                        DatabaseReference myRef = database.getReference(TOKENS_NODE);
                                        String userId = FirebaseAuth.getInstance().getUid();

                                        myRef.child(userId).child("token").setValue(token);

                                        myRef.child(userId).child("userPhoneNumber").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                                                String userPhoneNumber = snapshot.getValue(String.class);
                                                SharedPreferences.setPhoneNumber(Login.this, userPhoneNumber);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            });

        } else {
            Toast.makeText(this, "User is null", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onDestroy();

    }
}