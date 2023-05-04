package com.example.mobileapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationManagerHelper {
    public static void createNotification(Context context, String title, String body) {
        String CHANNEL_ID = "channel1";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            System.out.println("Permission not granted");
            return;
        }
        System.out.println("Permission granted");
        notificationManager.notify(1, mBuilder.build());
    }

    public static void sendNotification(String token,String title,String body, String notificationType, String bookingID) {
        String serverKey = "AAAAuYY_Bx8:APA91bGa-die4xVla-M5xSwN29RIjVfo1GlAtjnfuDvKBYmmFNcTpkM8PZ6iwuEGyWmZhpsDdm6m3eMDINqFbKikjEhR_PJ8M5uNdZMAbrKHBtCwW9x1NL55gYQ9alvsh9lfh6U2Jstg";
        String fcmUrl = "https://fcm.googleapis.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(fcmUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FcmApi fcmApi = retrofit.create(FcmApi.class);
        DataPayload dataPayload = null;
        NotificationData notificationData = new NotificationData(title, body);
        dataPayload = new DataPayload(Map.of("notificationType", notificationType, "id", bookingID));
        NotificationRequest notificationRequest = new NotificationRequest(token, notificationData, dataPayload);

        Call<ResponseBody> call = fcmApi.sendNotification("application/json", "key=" + serverKey, notificationRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    System.out.println("Notification sent successfully");
                } else {
                    System.out.println("Failed to send notification: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                System.out.println("Failed to send notification: " + t.getMessage());
            }
        });
    }


}
