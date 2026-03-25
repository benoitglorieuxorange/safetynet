package com.globe.safetynet.entities;

public class FireStation {

    private String address;
    //@JsonProperty("station")
    private String station;

    public FireStation() {}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
}
