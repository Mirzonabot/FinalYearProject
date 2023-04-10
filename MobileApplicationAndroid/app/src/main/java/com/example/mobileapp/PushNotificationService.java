package com.example.mobileapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://homestaybooking-f8308-default-rtdb.europe-west1.firebasedatabase.app");

    //reference to the token node
    DatabaseReference myRef = database.getReference();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        final String[] token = new String[1];
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    System.out.println("getInstanceId failed" + task.getException());
                    return;
                }
                // Get new Instance ID token
                token[0] = task.getResult().getToken();
                System.out.println("Token: " + token[0]);
            }});
        String userId = FirebaseAuth.getInstance().getUid();
        TokenUserID tokenUserID = new TokenUserID(token[0], userId);
        myRef.child("tokenUserID").push().setValue(tokenUserID);
    }

    private static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        System.out.println("Message received");

        NotificationManagerHelper.createNotification(this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        // Handle incoming message
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            System.out.println("message received");

        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message notification: " + remoteMessage.getNotification().getBody());
            System.out.println("message received null");
        }
    }

    @Override
    public void onSendError(@NonNull String s, @NonNull Exception e) {
        super.onSendError(s, e);
        System.out.println("Message error");
        System.out.println(e.getMessage());
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);
        System.out.println("A message was sent");
    }
}
