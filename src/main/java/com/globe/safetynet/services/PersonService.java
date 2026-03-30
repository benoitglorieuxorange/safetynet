package com.globe.safetynet.services;

import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonService {

    public final JsonRepository jsonRepository;

    public PersonService(JsonRepository jsonRepository) {
        this.jsonRepository = jsonRepository;
    }

    public Person addPerson(Person person){
        return jsonRepository.addPerson(person);
    }

    public Optional<Person> updatePerson(String firstName, String lastName, Person updatedPerson) {
        return jsonRepository.updatePerson(firstName, lastName, updatedPerson);
    }

    public Optional<Person> deletePerson(String firstName, String lastName) {
        return jsonRepository.deletePerson(firstName, lastName);
    }

}
