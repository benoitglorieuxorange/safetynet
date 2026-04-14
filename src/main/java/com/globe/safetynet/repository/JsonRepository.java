package com.globe.safetynet.repository;

import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.services.FireStationService;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Repository
public class JsonRepository {

   // private final FireStationService fireStationService;
    private Data data;

//    public JsonRepository(FireStationService fireStationService) {
//        this.fireStationService = fireStationService;
//    }

    @PostConstruct
    public void loadData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("data.json").getInputStream();

            this.data = mapper.readValue(inputStream, Data.class);

            // Vérification et logs
            if (data != null) {
                System.out.println(" Données chargées avec succès :");
                System.out.println("   - Personnes : " +
                        (data.getPersons() != null ? data.getPersons().size() : 0));
                System.out.println("   - FireStations : " +
                        (data.getFireStations() != null ? data.getFireStations().size() : 0));
                System.out.println("   - MedicalRecords : " + data.getMedicalRecords().size());
            } else {
                System.err.println("Les données sont null après chargement !");
            }

        } catch (IOException e) {
            System.err.println(" Erreur lors du chargement du fichier JSON : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Data getData() {
        if (data == null) {
            throw new IllegalStateException("Les données n'ont pas été chargées");
        }
        return data;
    }

    public Person addPerson(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("La personne ne peut pas être null");
        }
        data.getPersons().add(person);
        saveData();
        return person;
    }

    public FireStation addFireStation(FireStation fireStation) {
        if (fireStation == null) {
            throw new IllegalArgumentException("Le numéro de FireStation ne peut pas être null");
        }
        data.getFireStations().add(fireStation);
        saveData();
        return fireStation;
    }


    public Optional<Person> updatePerson(String firstName, String lastName, Person updatedPerson) {
        Optional<Person> result = data.getPersons().stream()
                .filter(person ->
                        person.getFirstName().equalsIgnoreCase(firstName) &&
                                person.getLastName().equalsIgnoreCase(lastName)
                )
                .findFirst()
                .map(person -> {
                    if (updatedPerson.getAddress() != null) person.setAddress(updatedPerson.getAddress());
                    if (updatedPerson.getCity()    != null) person.setCity(updatedPerson.getCity());
                    if (updatedPerson.getZip()     != null) person.setZip(updatedPerson.getZip());
                    if (updatedPerson.getPhone()   != null) person.setPhone(updatedPerson.getPhone());
                    if (updatedPerson.getEmail()   != null) person.setEmail(updatedPerson.getEmail());
                    return person;
                });
        result.ifPresent(p -> saveData());
        return result;
    }




    public Optional<Person> deletePerson(String firstName, String lastName) {
        Optional<Person> personToDelete = data.getPersons().stream()
                .filter(person ->
                        person.getFirstName().equalsIgnoreCase(firstName) &&
                                person.getLastName().equalsIgnoreCase(lastName)
                )
                .findFirst();

        personToDelete.ifPresent(person -> {
            data.getPersons().remove(person);
            saveData();
        });

        return personToDelete;
    }

    public Optional<FireStation> deleteFireStation(String address, String station) {
        Optional<FireStation> fireStationToDelete = data.getFireStations()
                .stream()
                .filter(stations -> stations.getAddress().equalsIgnoreCase(address) && stations.getStation().equals(station))
                .findFirst();

        fireStationToDelete.ifPresent(Fire  -> {
            data.getFireStations().remove(Fire);
            saveData();
        });

        return fireStationToDelete;
    }




    private String dataFilePath = "src/main/resources/data.json";

    private void saveData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(dataFilePath), data);
        } catch (Exception e) {
            System.err.println("Error during backup : " + e.getMessage());
            e.printStackTrace();
        }
    }



}