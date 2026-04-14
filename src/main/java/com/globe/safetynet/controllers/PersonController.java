package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.DeletePersonDTO;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.services.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<Person> addPerson(@RequestBody Person person) {
        return ResponseEntity.ok(personService.addPerson(person));
    }

    @PutMapping
    public ResponseEntity<Person> updatePerson(@RequestBody Person person) {
        return personService.updatePerson(person.getFirstName(), person.getLastName(), person)
                .map(updated -> ResponseEntity.ok(updated))   // 200 + personne modifiée
                .orElse(ResponseEntity.notFound().build());          // 404
    }

    @DeleteMapping
    public ResponseEntity<DeletePersonDTO> deletePerson(@RequestBody Person person) {
        return personService.deletePerson(person.getFirstName(), person.getLastName())
                .map(deletedPerson -> ResponseEntity.ok(
                        new DeletePersonDTO(person.getFirstName() + " " + person.getLastName() + " a bien été supprimé", deletedPerson)
                ))
                .orElse(ResponseEntity.notFound().build());
    }


}
