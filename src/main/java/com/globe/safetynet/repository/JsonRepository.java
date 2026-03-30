package com.globe.safetynet.repository;

import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.Person;
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

    private Data data;

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


    private static final String DATA_FILE_PATH = "src/main/resources/data.json";

    private void saveData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(DATA_FILE_PATH), data);
        } catch (Exception e) {
            System.err.println("Error during backup : " + e.getMessage());
            e.printStackTrace();
        }
    }



}