package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.DeletePersonDTO;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.services.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person("Benoit", "Glorieux", "1509 Culver St", "Culver", "97451", "841-874-6512", "Ben@test.com");
    }

    // -----------------------------------------------------------------------
    // POST /person
    // -----------------------------------------------------------------------

    @Test
    void addPerson_shouldReturn200_withAddedPerson() {
        // GIVEN
        when(personService.addPerson(person)).thenReturn(person);

        // WHEN
        ResponseEntity<Person> response = personController.addPerson(person);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(person);
        verify(personService, times(1)).addPerson(person);
    }

    // -----------------------------------------------------------------------
    // PUT /person
    // -----------------------------------------------------------------------

    @Test
    void updatePerson_shouldReturn200_whenPersonExists() {
        // GIVEN
        Person updated = new Person("Benoit", "Glorieux", "999 New St", "Culver", "97451", "000-000-0000", "new@email.com");
        when(personService.updatePerson("Benoit", "Glorieux", person)).thenReturn(Optional.of(updated));

        // WHEN
        ResponseEntity<Person> response = personController.updatePerson(person);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updated);
        verify(personService, times(1)).updatePerson("Benoit", "Glorieux", person);
    }

    @Test
    void updatePerson_shouldReturn404_whenPersonNotFound() {
        // GIVEN
        when(personService.updatePerson("Benoit", "Glorieux", person)).thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<Person> response = personController.updatePerson(person);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(personService, times(1)).updatePerson("Benoit", "Glorieux", person);
    }

    // -----------------------------------------------------------------------
    // DELETE /person
    // -----------------------------------------------------------------------

    @Test
    void deletePerson_shouldReturn200_withDeletePersonDTO_whenPersonExists() {
        // GIVEN
        when(personService.deletePerson("Benoit", "Glorieux")).thenReturn(Optional.of(person));

        // WHEN
        ResponseEntity<DeletePersonDTO> response = personController.deletePerson(person);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Benoit Glorieux a bien été supprimé");
        assertThat(response.getBody().deletePerson()).isEqualTo(person);
        verify(personService, times(1)).deletePerson("Benoit", "Glorieux");
    }

    @Test
    void deletePerson_shouldReturn404_whenPersonNotFound() {
        // GIVEN
        when(personService.deletePerson("Benoit", "Glorieux")).thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<DeletePersonDTO> response = personController.deletePerson(person);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(personService, times(1)).deletePerson("Benoit", "Glorieux");
    }
}