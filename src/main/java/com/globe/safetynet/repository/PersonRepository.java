package com.globe.safetynet.repository;

import com.globe.safetynet.entities.Person;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PersonRepository {

    private final JsonRepositoryBase jsonRepository;

    public PersonRepository(JsonRepositoryBase jsonRepository) {
        this.jsonRepository = jsonRepository;
    }

    private List<Person> getPersons() {
        return jsonRepository.getData().getPersons();
    }

    public Person add(Person person) {
        if (person == null) throw new IllegalArgumentException("La personne ne peut pas être null");
        getPersons().add(person);
        jsonRepository.saveData();
        return person;
    }

    public Optional<Person> update(String firstName, String lastName, Person updatedPerson) {
        Optional<Person> result = getPersons().stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(firstName)
                        && p.getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .map(p -> {
                    if (updatedPerson.getAddress() != null) p.setAddress(updatedPerson.getAddress());
                    if (updatedPerson.getCity()    != null) p.setCity(updatedPerson.getCity());
                    if (updatedPerson.getZip()     != null) p.setZip(updatedPerson.getZip());
                    if (updatedPerson.getPhone()   != null) p.setPhone(updatedPerson.getPhone());
                    if (updatedPerson.getEmail()   != null) p.setEmail(updatedPerson.getEmail());
                    return p;
                });
        result.ifPresent(p -> jsonRepository.saveData());
        return result;
    }

    public Optional<Person> delete(String firstName, String lastName) {
        Optional<Person> toDelete = getPersons().stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(firstName)
                        && p.getLastName().equalsIgnoreCase(lastName))
                .findFirst();
        toDelete.ifPresent(p -> {
            getPersons().remove(p);
            jsonRepository.saveData();
        });
        return toDelete;
    }
}