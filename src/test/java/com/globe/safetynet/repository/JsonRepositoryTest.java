package com.globe.safetynet.repository;


import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonRepositoryTest {


    @Mock
    private Data data;

    @InjectMocks
    private JsonRepository repository;

    private Person person;
    private List<Person> personList;

    // Arrange
    @BeforeEach
    void setUp() {
        personList = new ArrayList<>();
        person = new Person();
        person.setFirstName("Benoit");
        person.setLastName("GLORIEUX");
        person.setAddress("11 rue du chemin");
        person.setZip("39000");
        person.setCity("Belfort");
        person.setPhone("123-456-7890");
        person.setEmail("truc@machin.com");

        personList.add(person);

        //when(data.getPersons()).thenReturn(personList);

    }

    @Test
    void updatePerson_ShouldUpdatePerson_WithSomeFieldAreNull() {

        // Arrange
        Person personUpdate = new Person();
        personUpdate.setZip("90000");
        personUpdate.setEmail("truc2@machin.com");
        when(data.getPersons()).thenReturn(personList);

        // Act
        Optional<Person> resultUpdate = repository.updatePerson("Benoit", "GLORIEUX", personUpdate);

        // Assert
        assertTrue(resultUpdate.isPresent());
        Person updated = resultUpdate.get();
        assertEquals(updated.getZip(), "90000");
        assertEquals(updated.getEmail(), "truc2@machin.com");

   }

   @Test
    void updatePerson_ShouldReturnEmptyOptional_WhenPersonIsNotFound() {

        // Arrange
        Person personUpdate = new Person();
        personUpdate.setZip("90000");
        personUpdate.setEmail("truc2@machin.com");
        when(data.getPersons()).thenReturn(personList);

        // Act
        Optional<Person> result = repository.updatePerson("toto", "titi", personUpdate);

        // Assert
        assertFalse(result.isPresent());

   }

   @Test
    void updatePerson_ShouldReturnEmptyOptional_WhenPersonIsEmpty() {

        // Arrange
        Person personUpdate = new Person();
        personUpdate.setZip("90000");
        personUpdate.setEmail("");

        // Act
        Optional<Person> result = repository.updatePerson("toto", "titi", personUpdate);

        // Assert
        assertFalse(result.isPresent());

   }

   @Test
    void updatePerson_ShouldBeCaseInsensitive() {

        // Arrange
        Person personUpdate = new Person();
        personUpdate.setZip("90000");
        when(data.getPersons()).thenReturn(personList);

        // Act
        Optional<Person> result = repository.updatePerson("BENOIT", "glorieux", personUpdate);

        // Assert
        assertTrue(result.isPresent());
        Person updated = result.get();
        assertEquals(updated.getZip(), "90000");

   }

   @Test
    void updatePerson_ShouldReturnEmptyOptional_WhenPersonIsNull() {

        // Act
        Optional<Person> result = repository.updatePerson(null, "titi", person);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void deletePerson() {
        // Arrange
        when(data.getPersons()).thenReturn(personList);

        // Act
        Optional<Person> result  = repository.deletePerson("Benoit", "GLORIEUX");

        Person deleted = result.get();
        assertFalse(personList.contains(deleted));

    }

    @Test
    void deletePerson_ShouldReturnEmptyOptional_WhenPersonIsNotFound() {
        Optional<Person> result = repository.deletePerson("toto", "titi");
        assertFalse(result.isPresent());

    }


    @Test
    void deletePerson_ShouldBeCaseInsensitive() {
        // Arrange
        when(data.getPersons()).thenReturn(personList);

        // Assert précondition
        assertEquals(1, personList.size());

        // Act
        Optional<Person> result = repository.deletePerson("benoit", "glorieux");

        // Assert
        assertTrue(result.isPresent(), "La personne devrait être trouvée et supprimée");
        assertTrue(personList.isEmpty(), "La liste doit être vide après suppression");
    }

    @Test
    void deletePerson_ShouldReturnEmptyOptional_WhenPersonIsEmpty() {
        Optional<Person> result = repository.deletePerson("toto", "titi");
        assertFalse(result.isPresent());
    }

    @Test
    void getData_ShouldReturnEmptyOptional_WhenPersonIsNotFound() {

        // Arrange
        ReflectionTestUtils.setField(repository, "data", null);

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.getData()
        );

        assertEquals("Les données n'ont pas été chargées", exception.getMessage());

    }

    @Test
    void getData_ShouldReturnData_WhenDataIsLoaded() {
        // Arrange
        ReflectionTestUtils.setField(repository, "data", data);

        // Act
        Data result = repository.getData();

        // Assert
        assertNotNull(result);
        assertSame(data, result);
    }


} //End Class
