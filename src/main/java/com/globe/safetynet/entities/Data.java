package com.globe.safetynet.entities;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private List<Person> persons;
    @JsonProperty("firestations")
    private List<FireStation> fireStations;
    @JsonProperty("medicalrecords")
    private List<MedicalRecord> medicalRecords;

    public Data() {}

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }


    public List<FireStation> getFireStations() {
        return fireStations;
    }

    public void setFireStations(List<FireStation> firestations) {
        this.fireStations = firestations;
    }


    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }


}
