package com.globe.safetynet.repository;

import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonRepositoryTest {

    @Mock
    private JsonRepositoryBase jsonRepository;

    @InjectMocks
    private PersonRepository personRepository;

    private Data data;
    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person();
        person.setFirstName("Benoit");
        person.setLastName("GLORIEUX");
        person.setAddress("1 rue de la street");
        person.setCity("Belfort");
        person.setZip("90000");
        person.setPhone("0607080910");
        person.setEmail("benoit@test.com");

        data = new Data(
                new ArrayList<>(List.of(person)),
                new ArrayList<>(),
                new ArrayList<>()
        );

        lenient().when(jsonRepository.getData()).thenReturn(data);  // ← ici
    }

    // add

    @Test
    void add_shouldReturnPerson_whenPersonIsValid() {
        Person newPerson = new Person();
        newPerson.setFirstName("Jane");
        newPerson.setLastName("GLORIEUX");

        Person result = personRepository.add(newPerson);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
    }

    @Test
    void add_shouldAddPersonToList() {
        Person newPerson = new Person();
        newPerson.setFirstName("Jane");
        newPerson.setLastName("GLORIEUX");

        personRepository.add(newPerson);

        assertEquals(2, data.getPersons().size());
        assertTrue(data.getPersons().contains(newPerson));
    }

    @Test
    void add_shouldCallSaveData_whenPersonIsAdded() {
        Person newPerson = new Person();
        newPerson.setFirstName("Jane");
        newPerson.setLastName("GLORIEUX");

        personRepository.add(newPerson);

        verify(jsonRepository, times(1)).saveData();
    }

    @Test
    void add_shouldThrowIllegalArgumentException_whenPersonIsNull() {
        assertThrows(IllegalArgumentException.class, () -> personRepository.add(null));
        verify(jsonRepository, never()).saveData();
    }

    //update

    @Test
    void update_shouldReturnUpdatedPerson_whenPersonExists() {
        Person updatedPerson = new Person();
        updatedPerson.setAddress("2 rue Nouvelle");
        updatedPerson.setCity("Lyon");

        Optional<Person> result = personRepository.update("Benoit", "GLORIEUX", updatedPerson);

        assertTrue(result.isPresent());
        assertEquals("2 rue Nouvelle", result.get().getAddress());
        assertEquals("Lyon", result.get().getCity());
    }

    @Test
    void update_shouldOnlyUpdateNonNullFields() {
        Person updatedPerson = new Person();
        updatedPerson.setAddress("2 rue Nouvelle");
        // city, zip, phone, email restent null → ne doivent pas être écrasés

        personRepository.update("Benoit", "GLORIEUX", updatedPerson);

        assertEquals("2 rue Nouvelle", person.getAddress());
        assertEquals("Belfort", person.getCity());       // inchangé
        assertEquals("90000", person.getZip());        // inchangé
        assertEquals("0607080910", person.getPhone()); // inchangé
        assertEquals("benoit@test.com", person.getEmail()); // inchangé
    }

    @Test
    void update_shouldReturnEmpty_whenPersonNotFound() {
        Person updatedPerson = new Person();
        updatedPerson.setAddress("2 rue Nouvelle");

        Optional<Person> result = personRepository.update("Jane", "Inconnu", updatedPerson);

        assertFalse(result.isPresent());
    }

    @Test
    void update_shouldCallSaveData_whenPersonIsUpdated() {
        Person updatedPerson = new Person();
        updatedPerson.setAddress("2 rue Nouvelle");

        personRepository.update("Benoit", "GLORIEUX", updatedPerson);

        verify(jsonRepository, times(1)).saveData();
    }

    @Test
    void update_shouldNotCallSaveData_whenPersonNotFound() {
        Person updatedPerson = new Person();
        updatedPerson.setAddress("2 rue Nouvelle");

        personRepository.update("Jane", "Inconnu", updatedPerson);

        verify(jsonRepository, never()).saveData();
    }

    //delete

    @Test
    void delete_shouldReturnDeletedPerson_whenPersonExists() {
        Optional<Person> result = personRepository.delete("Benoit", "GLORIEUX");

        assertTrue(result.isPresent());
        assertEquals("Benoit", result.get().getFirstName());
    }

    @Test
    void delete_shouldRemovePersonFromList() {
        personRepository.delete("Benoit", "GLORIEUX");

        assertTrue(data.getPersons().isEmpty());
    }

    @Test
    void delete_shouldCallSaveData_whenPersonIsDeleted() {
        personRepository.delete("Benoit", "GLORIEUX");

        verify(jsonRepository, times(1)).saveData();
    }

    @Test
    void delete_shouldReturnEmpty_whenPersonNotFound() {
        Optional<Person> result = personRepository.delete("Jane", "Inconnu");

        assertFalse(result.isPresent());
    }

    @Test
    void delete_shouldNotCallSaveData_whenPersonNotFound() {
        personRepository.delete("Jane", "Inconnu");

        verify(jsonRepository, never()).saveData();
    }

    @Test
    void delete_shouldBeCaseInsensitive() {
        Optional<Person> result = personRepository.delete("Benoit", "GLORIEUX");

        assertTrue(result.isPresent());
        assertTrue(data.getPersons().isEmpty());
    }
}