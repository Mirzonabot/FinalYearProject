package com.example.mobileapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    private static final String TAG = "Utils";

    public static void getTokens() {
        FirebaseFunctions firebaseFunctions = FirebaseFunctions.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", "IR58DT89l0at22J0bXgg9uzU9av2");

        firebaseFunctions
                .getHttpsCallable("getFCMToken")
                .call(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                        String token = (String) result.get("token");
                        if (token != null) {
                            System.out.println("______________________________");
                            System.out.println("______________________________");
                            System.out.println("______________________________");
                            System.out.println("______________________________");
                            System.out.println("Token: " + token);
                            System.out.println("______________________________");
                            System.out.println("______________________________");
                            System.out.println("______________________________");
                            System.out.println("______________________________");
                            // Use the FCM token here
                        } else {
                            String error = (String) result.get("error");
                            Log.e(TAG, "Error getting FCM token: " + error);
                        }
                    } else {
                        Exception e = task.getException();
                        Log.e(TAG, "Error calling Cloud Function: " + e.getMessage());
                    }
                });
    }
    public static Bitmap createCircleBitmap(int fillColor, int strokeColor, int radius) {
        Bitmap output = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setColor(fillColor);
        paint.setAntiAlias(true);

        canvas.drawCircle(radius, radius, radius, paint);

        paint.setColor(strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawCircle(radius, radius, radius, paint);

        return output;
    }
}
