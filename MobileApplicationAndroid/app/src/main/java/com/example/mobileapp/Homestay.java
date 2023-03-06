package com.example.mobileapp;


import java.util.UUID;

public class Homestay {
    private String id;
    private String homestayName;
    private int homestayCapacity;
    private String ownerId;
    private String address;
    private String latitude;
    private String longitude;
    private boolean availability;

    private String village;
    private String district;
    private String city;
    private String street;

    public Homestay() {
    }

    public Homestay(String homestayName, int homestayCapacity, String ownerId, String address, String latitude, String longitude, String village,String street, String district, String city) {
        this.id = UUID.randomUUID().toString();
        this.homestayName = homestayName;
        this.homestayCapacity = homestayCapacity;
        this.ownerId = ownerId;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.availability = true;
        this.village = village;
        this.district = district;
        this.city = city;
        this.street = street;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHomestayName() {
        return homestayName;
    }

    public void setHomestayName(String homestayName) {
        this.homestayName = homestayName;
    }

    public int getHomestayCapacity() {
        return homestayCapacity;
    }

    public void setHomestayCapacity(int homestayCapacity) {
        this.homestayCapacity = homestayCapacity;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return "Homestay{" +
                "homestayName='" + homestayName + '\'' +
                ", homestayCapacity=" + homestayCapacity +
                ", ownerId='" + ownerId + '\'' +
                ", address='" + address + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", availability=" + availability +
                '}';
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
