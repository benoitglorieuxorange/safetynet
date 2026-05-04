package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.FireAlertDTO;
import com.globe.safetynet.dtos.PersonFireAlertDTO;
import com.globe.safetynet.services.FireAlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FireAlertControllerTest {

    @Mock
    private FireAlertService fireAlertService;

    @InjectMocks
    private FireAlertController fireAlertController;

    private FireAlertDTO fireAlertDTO;

    @BeforeEach
    void setUp() {
        PersonFireAlertDTO person1 = new PersonFireAlertDTO("John", "Boyd", 36,
                List.of("aznol:350mg", "hydrapermazol:100mg"), List.of("nillacilan"));
        PersonFireAlertDTO person2 = new PersonFireAlertDTO("Jane", "Boyd", 32,
                List.of(), List.of());
        fireAlertDTO = new FireAlertDTO(List.of(person1, person2), "3");
    }

    // -----------------------------------------------------------------------
    // Cas nominal : adresse valide avec données → 200
    // -----------------------------------------------------------------------
    @Test
    void getFireAlert_shouldReturn200_whenAddressIsValidAndDataFound() {
        // GIVEN
        String address = "1509 Culver St";
        when(fireAlertService.getPersonByAddress(address)).thenReturn(fireAlertDTO);

        // WHEN
        ResponseEntity<FireAlertDTO> response = fireAlertController.getFireAlert(address);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(fireAlertDTO);
        verify(fireAlertService, times(1)).getPersonByAddress(address);
    }

    // -----------------------------------------------------------------------
    // Service retourne null → 404
    // -----------------------------------------------------------------------
    @Test
    void getFireAlert_shouldReturn404_whenServiceReturnsNull() {
        // GIVEN
        String address = "unknown address";
        when(fireAlertService.getPersonByAddress(address)).thenReturn(null);

        // WHEN
        ResponseEntity<FireAlertDTO> response = fireAlertController.getFireAlert(address);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(fireAlertService, times(1)).getPersonByAddress(address);
    }

    // -----------------------------------------------------------------------
    // Service lève une exception → 500
    // -----------------------------------------------------------------------
    @Test
    void getFireAlert_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        String address = "1509 Culver St";
        when(fireAlertService.getPersonByAddress(address))
                .thenThrow(new RuntimeException("Database error"));

        // WHEN
        ResponseEntity<FireAlertDTO> response = fireAlertController.getFireAlert(address);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
        verify(fireAlertService, times(1)).getPersonByAddress(address);
    }

    // -----------------------------------------------------------------------
    // address null → 400
    // Note : Spring @RequestParam empêche null en pratique (400 automatique),
    // mais la garde existe dans le code → on la couvre en appelant la méthode directement.
    // -----------------------------------------------------------------------
    @Test
    void getFireAlert_shouldReturn400_whenAddressIsNull() {
        // WHEN
        ResponseEntity<FireAlertDTO> response = fireAlertController.getFireAlert(null);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
        verifyNoInteractions(fireAlertService);
    }
}