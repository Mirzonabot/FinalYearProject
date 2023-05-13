package com.example.mobileapp.memorymanager;



import android.content.Context;

public class SharedPreferences {
   public static void internetAvailable(Context context, boolean status) {
       android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("internetStatus", Context.MODE_PRIVATE);
       android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
       editor.putBoolean("internetStatus", status);
       editor.apply();
   }

    public static boolean isInternetAvailable(Context context) {
         android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("internetStatus", Context.MODE_PRIVATE);
         return sharedPreferences.getBoolean("internetStatus", false);
    }

    public static void setUserId(Context context, String userID){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("userId", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userID);
        editor.apply();
    }

    public static String getUserId(Context context){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("userId", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", "");
    }

    public static void setPhoneNumber(Context context, String phoneNumber){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("phoneNumber", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();
    }

    public static String getPhoneNumber(Context context){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("phoneNumber", Context.MODE_PRIVATE);
        return sharedPreferences.getString("phoneNumber", "");
    }

    public static void configureInternet(Context context){
        if (SharedPreferences.isInternetAvailable(context)){
            SharedPreferences.internetAvailable(context,true);
        }
        else {
            SharedPreferences.internetAvailable(context,false);
        }
    }

    public static void setUserType(Context context, String userType){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("userType", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userType", userType);
        editor.apply();
    }

    public static String getUserType(Context context){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("userType", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userType", "");
    }

    public static void setUserName(Context context, String userName){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("userName", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.apply();
    }

    public static String getUserName(Context context){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("userName", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userName", "");
    }

    //set email
    public static void setEmail(Context context, String email){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("email", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    //get email
    public static String getEmail(Context context){
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("email", Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", "");
    }


}
