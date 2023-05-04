package com.example.mobileapp;



import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReverseGeocodingTask extends AsyncTask<Double, Void, String> {

    @Override
    protected String doInBackground(Double... coordinates) {
        try {
            return reverseGeocode(coordinates[0], coordinates[1]);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String address) {
        if (address != null) {
            // Do something with the address, such as updating the UI
            System.out.println("____________________________________");
            System.out.println("Reverse Geocoding Results");
            System.out.println("____________________________________");
            System.out.println("Address: " + address);
            System.out.println("____________________________________");
            System.out.println("____________________________________");
            System.out.println("____________________________________");

        } else {
            // Handle the error, such as displaying an error message
            System.out.println("Error retrieving address");
        }
    }

    private String reverseGeocode(double latitude, double longitude) throws IOException, JSONException {
        String baseUrl = "https://nominatim.openstreetmap.org/reverse";
        String format = "json";
        String url = String.format("%s?format=%s&lat=%f&lon=%f", baseUrl, format, latitude, longitude);

        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();

        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        JSONObject jsonObject = new JSONObject(response.toString());
        return jsonObject.getString("display_name");
    }
}
