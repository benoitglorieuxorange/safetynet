package com.globe.safetynet.services;

import com.globe.safetynet.dtos.PhoneAlertDTO;
import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepositoryBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhoneAlertServiceTest {

    @Mock
    private JsonRepositoryBase  jsonRepositoryBase;

    @InjectMocks
    private PhoneAlertService phoneAlertService;

    private Data data;

    @BeforeEach
    void setUp() {
        FireStation fireStation = new FireStation("1 rue Test", "1");

        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Doe");
        person1.setAddress("1 rue Test");
        person1.setPhone("0600000001");

        Person person2 = new Person();
        person2.setFirstName("Jane");
        person2.setLastName("Doe");
        person2.setAddress("1 rue Test");
        person2.setPhone("0600000002");


        data = new Data(
                new ArrayList<>(List.of(person1, person2)),
                new ArrayList<>(List.of(fireStation)),
                new ArrayList<>()
        );
    }

    @Test
    void getPhoneNumberAlerts_shouldReturnPhoneList_whenStationExists() {
        when(jsonRepositoryBase.getData()).thenReturn(data);

        // Debug : vérifie que les données sont bien construites
        System.out.println("FireStations : " + data.getFireStations());
        System.out.println("Persons : " + data.getPersons());

        List<PhoneAlertDTO> result = phoneAlertService.getPhoneNumberAlerts("1");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.phoneNumber().equals("0600000001")));
        assertTrue(result.stream().anyMatch(dto -> dto.phoneNumber().equals("0600000002")));
    }


    @Test
    void getPhoneNumberAlerts_shouldReturnNull_whenNoAddressFoundForStation() {
        when(jsonRepositoryBase.getData()).thenReturn(data);

        // Station "99" n'existe pas dans les données
        List<PhoneAlertDTO> result = phoneAlertService.getPhoneNumberAlerts("99");

        assertNull(result);
    }


    @Test
    void getPhoneNumberAlerts_shouldReturnNull_whenNoPersonFoundAtAddress() {
        // Station connue mais aucune personne à cette adresse
        FireStation fireStation = new FireStation("2 rue Vide", "2");
        data.setFireStations(List.of(fireStation));
        data.setPersons(List.of()); // aucune personne

        when(jsonRepositoryBase.getData()).thenReturn(data);

        List<PhoneAlertDTO> result = phoneAlertService.getPhoneNumberAlerts("2");

        assertNull(result);
    }

    @Test
    void getPhoneNumberAlerts_shouldThrowException_whenDataIsNull() {
        when(jsonRepositoryBase.getData()).thenThrow(new IllegalStateException("Data cannot be load"));

        assertThrows(IllegalStateException.class,
                () -> phoneAlertService.getPhoneNumberAlerts("1"));
    }


    @Test
    void getPhoneNumberAlerts_shouldReturnOnlyPhoneNumbers() {
        when(jsonRepositoryBase.getData()).thenReturn(data);

        List<PhoneAlertDTO> result = phoneAlertService.getPhoneNumberAlerts("1");

        assertNotNull(result);
        result.forEach(dto -> assertNotNull(dto.phoneNumber()));
    }
}