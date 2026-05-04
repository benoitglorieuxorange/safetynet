package com.globe.safetynet.services;

import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private Person person;

    @BeforeEach
    void setUp() {

        person = new Person();
        person.setFirstName("Benoit");
        person.setLastName("GLORIEUX");
        person.setAddress("1 rue Test");
        person.setCity("Belfort");
        person.setZip("90000");
        person.setPhone("0607080910");
        person.setEmail("ben@machin.com");

    }

// Add

    @Test
    void addPerson_ShouldReturnPerson_WhenPersonIsADD() throws ParseException {

        when(personRepository.add(person)).thenReturn(person);

        Person result = personService.addPerson(person);

        assertNotNull(result);
        assertEquals(person, result);
        assertEquals(person.getFirstName(), result.getFirstName());
        assertEquals(person.getLastName(), result.getLastName());
        assertEquals(person.getAddress(), result.getAddress());
        assertEquals(person.getCity(), result.getCity());
        assertEquals(person.getZip(), result.getZip());

   }

// Update

    @Test
    void updatePerson_shouldReturnUpdatedPerson_whenPersonExists() {
        Person updatedPerson = new Person();
        updatedPerson.setFirstName("Benoit");
        updatedPerson.setLastName("GLORIEUX");
        updatedPerson.setAddress("2 rue de la street");

        when(personRepository.update("Benoit", "GLORIEUX", updatedPerson))
                .thenReturn(Optional.of(updatedPerson));

        Optional<Person> result = personService.updatePerson("Benoit", "GLORIEUX", updatedPerson);

        assertTrue(result.isPresent());
        assertEquals("2 rue de la street", result.get().getAddress());
        verify(personRepository, times(1)).update("Benoit", "GLORIEUX", updatedPerson);
    }

    @Test
    void updatePerson_shouldReturnEmpty_whenPersonNotFound() {
        when(personRepository.update("Benoit", "Inconnu", person))
                .thenReturn(Optional.empty());

        Optional<Person> result = personService.updatePerson("Benoit", "Inconnu", person);

        assertFalse(result.isPresent());
        verify(personRepository, times(1)).update("Benoit", "Inconnu", person);
    }

// Delete

    @Test
    void deletePerson_shouldReturnDeletedPerson_whenPersonExists() {
        when(personRepository.delete("Benoit", "GLORIEUX"))
                .thenReturn(Optional.of(person));

        Optional<Person> result = personService.deletePerson("Benoit", "GLORIEUX");

        assertTrue(result.isPresent());
        assertEquals("Benoit", result.get().getFirstName());
        verify(personRepository, times(1)).delete("Benoit", "GLORIEUX");
    }

    @Test
    void deletePerson_shouldReturnEmpty_whenPersonNotFound() {
        when(personRepository.delete("Benoit", "Inconnu"))
                .thenReturn(Optional.empty());

        Optional<Person> result = personService.deletePerson("Benoit", "Inconnu");

        assertFalse(result.isPresent());
        verify(personRepository, times(1)).delete("Benoit", "Inconnu");
    }
}