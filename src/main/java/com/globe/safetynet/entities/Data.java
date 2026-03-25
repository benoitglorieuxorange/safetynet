package com.globe.safetynet.entities;


import java.util.List;

public class Data {
    private List<Person> persons;
    private List<Firestation> firestations;

    public Data() {}

    public Data(List<Person> persons, List<Firestation> firestations) {
        this.persons = persons;
        this.firestations = firestations;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Firestation> getFirestations() {
        return firestations;
    }

    public void setFirestations(List<Firestation> firestations) {
        this.firestations = firestations;
    }

    @Override
    public String toString() {
        return "Data{" +
                "persons=" + persons +
                ", firestations=" + firestations +
                '}';
    }
}
