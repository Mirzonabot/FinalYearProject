package com.example.mobileapp;

import java.util.Map;

public class DataPayload {
    private Map<String, String> data;

    // Getters, setters, and constructors

    public DataPayload(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
