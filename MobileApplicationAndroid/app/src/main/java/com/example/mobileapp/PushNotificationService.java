package com.example.mobileapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mobileapp.dbclasses.Booking;
import com.example.mobileapp.dbclasses.TokenUserID;
import com.example.mobileapp.memorymanager.SqlHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

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
            }
        });
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

            //take the data from the message

//            String id = remoteMessage.getData().get("data");
//            String notificationType = remoteMessage.getData().get("notificationType");

            try {
                SqlHelper sqlHelper = new SqlHelper(this,null,null);

                JSONObject jsonData = new JSONObject(Objects.requireNonNull(remoteMessage.getData().get("data")));
                System.out.println("jsonData: " + jsonData);
                String notificationType = jsonData.getString("notificationType");
                String id = jsonData.getString("id");
                if (notificationType.equals("newBooking")) {
                    Query query = database.getReference("booking").orderByChild("bookingID").equalTo(id);

                    sqlHelper.acceptBookingInOtherHomestay(id);

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Booking booking = dataSnapshot.getValue(Booking.class);
                                System.out.println("booking: " + booking);
                                System.out.println("bookingID: " + booking.getBookingID());
                                System.out.println("bookingID: " + booking.getBookingID().equals(id));
                                sqlHelper.createBookingInMyHomestay(booking);
                                if (booking.getBookingID().equals(id)) {
                                    System.out.println("bookingID: " + booking.getBookingID());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors here
                        }
                    });
                }
                else if (notificationType.equals("ownerAccepted")){
                    sqlHelper.acceptBookingInOtherHomestay(id);
                }
                else if (notificationType.equals("rejectedByProvider")){
//                    sqlHelper.rejectBookingInOtherHomestays(id);
                }
                else if (notificationType.equals("cancelledByProvider")){
                    sqlHelper.cancelMyBookingInOtherHomestays(id);
                }
                else if (notificationType.equals("cancelledByCustomer")){
                    sqlHelper.cancelBookingInMyHomestays(id);
                }


                // Use the notificationType and id values as needed
            } catch (JSONException e) {
                // Handle any errors that may occur during parsing
            }


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
