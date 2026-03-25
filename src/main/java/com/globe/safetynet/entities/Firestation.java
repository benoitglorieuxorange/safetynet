package com.globe.safetynet.entities;

public class Firestation {

    private String address;
    private String stations;

    public Firestation() {}

    public  Firestation(String address, String stations) {
        this.address = address;
        this.stations = stations;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStations() {
        return stations;
    }

    public void setStations(String stations) {
        this.stations = stations;
    }
}
