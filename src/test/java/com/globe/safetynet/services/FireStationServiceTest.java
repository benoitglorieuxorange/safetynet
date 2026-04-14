package com.globe.safetynet.services;

import com.globe.safetynet.dtos.FireStationResponseDTO;
import com.globe.safetynet.entities.*;
import com.globe.safetynet.repository.JsonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


class FireStationServiceTest {

    @Mock
    private JsonRepository repository;

    @InjectMocks
    private FireStationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetPersonsByFireStationNumber_withValidData() {

        // Arrange
        String stationNumber = "1";
        //  public Person(String firstName, String lastName, String address, String city, String zip, String phone, String email)
        //  public MedicalRecord(String firstName, String lastName, String birthdate,  List<String> medications, List<String> allergies)
        Person person1 = new Person("Benoit", "GLORIEUX", "rue du stade", "Dole", "90000", "123-456-7890", "truc@machin.com");
        Person person2 = new Person("Bob", "Saint-Clar", "rue du stade", "Belfort", "90000", "789-123-5555", "bob@dgse.fr");
        MedicalRecord record1 = new MedicalRecord(
                "Benoit",
                "GLORIEUX",
                "01/01/1972",
                List.of("aznol:350mg", "hydrapermazol:100mg"),
                List.of("TestUnitaire"));

        MedicalRecord record2 = new MedicalRecord(
                "Bob",
                "Saint-Clar",
                "01/01/2012",
                List.of("aznol:550mg", "hydrapermazol:1000mg"),
                List.of("Mockito"));

        FireStation station1 = new FireStation("rue du stade", "1");
        FireStation station2 = new FireStation("rue du chemin", "2");

        Data data = new Data(
                Arrays.asList(person1, person2),
                Arrays.asList(station1, station2),
                Arrays.asList(record1, record2)
        );

        when(repository.getData()).thenReturn(data);

        // Act
       FireStationResponseDTO result = service.getPersonsByFireStationNumber(stationNumber);

        // Assert

        System.out.println(result);

        assertNotNull(result);
        assertEquals(1, result.childCount());
        assertEquals(1, result.childCount());


    } //EofM

    @Test
    void testGetPersonsByFireStationNumber_withInvalidData() {
        // Arrange
        String stationNumber = "99";
        Person person1 = new Person("Benoit", "GLORIEUX", "rue du stade", "Dole", "90000", "123-456-7890", "truc@machin.com");
        Person person2 = new Person("Bob", "Saint-Clar", "rue du stade", "Belfort", "90000", "789-123-5555", "bob@dgse.fr");
        MedicalRecord record1 = new MedicalRecord(
                "Benoit",
                "GLORIEUX",
                "01/01/1972",
                List.of("aznol:350mg", "hydrapermazol:100mg"),
                List.of("TestUnitaire"));

        MedicalRecord record2 = new MedicalRecord(
                "Bob",
                "Saint-Clar",
                "01/01/2012",
                List.of("aznol:550mg", "hydrapermazol:1000mg"),
                List.of("Mockito"));

        FireStation station1 = new FireStation("rue du stade", "1");
        FireStation station2 = new FireStation("rue du chemin", "2");

        Data data = new Data(
                Arrays.asList(person1, person2),
                Arrays.asList(station1, station2),
                Arrays.asList(record1, record2)
        );

        when(repository.getData()).thenReturn(data);

        // Act
        FireStationResponseDTO result = service.getPersonsByFireStationNumber(stationNumber);

        // Assert
        assertNull(result);


    }

}// EofC