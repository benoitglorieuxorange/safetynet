package com.globe.safetynet.services;

import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonService {

    public final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person addPerson(Person person){
        return personRepository.add(person);
    }

    public Optional<Person> updatePerson(String firstName, String lastName, Person updatedPerson) {
        return personRepository.update(firstName, lastName, updatedPerson);
    }

    public Optional<Person> deletePerson(String firstName, String lastName) {
        return personRepository.delete(firstName, lastName);
    }

}
