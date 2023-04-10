package com.example.mobileapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FcmApi {
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String authorization,
            @Body NotificationRequest notificationRequest);
}
