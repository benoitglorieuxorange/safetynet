package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.FireStationResponseDTO;
import com.globe.safetynet.dtos.PersonFireStationDTO;
import com.globe.safetynet.services.FireStationService;
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
class FireStationControllerTest {

    @Mock
    private FireStationService fireStationService;

    @InjectMocks
    private FireStationController fireStationController;

    private FireStationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        PersonFireStationDTO person1 = new PersonFireStationDTO("John", "Boyd", "1509 Culver St", "841-874-6512");
        PersonFireStationDTO person2 = new PersonFireStationDTO("Jane", "Boyd", "1509 Culver St", "841-874-6513");
        responseDTO = new FireStationResponseDTO(List.of(person1, person2), 2, 0);
    }

    // -----------------------------------------------------------------------
    // Cas nominal : station valide avec données → 200
    // -----------------------------------------------------------------------
    @Test
    void getPersonByFireStation_shouldReturn200_whenStationIsValidAndDataFound() {
        // GIVEN
        when(fireStationService.getPersonsByFireStationNumber("1")).thenReturn(responseDTO);

        // WHEN
        ResponseEntity<?> response = fireStationController.getPersonByFireStation(1);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDTO);
        verify(fireStationService, times(1)).getPersonsByFireStationNumber("1");
    }

    // -----------------------------------------------------------------------
    // Toutes les bornes valides : 1, 2, 3, 4
    // -----------------------------------------------------------------------
    @Test
    void getPersonByFireStation_shouldReturn200_forStationNumber4() {
        // GIVEN — borne supérieure valide
        when(fireStationService.getPersonsByFireStationNumber("4")).thenReturn(responseDTO);

        // WHEN
        ResponseEntity<?> response = fireStationController.getPersonByFireStation(4);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(fireStationService).getPersonsByFireStationNumber("4");
    }

    // -----------------------------------------------------------------------
    // stationNumber < 1 → 400
    // -----------------------------------------------------------------------
    @Test
    void getPersonByFireStation_shouldReturn400_whenStationNumberIsZero() {
        // WHEN
        ResponseEntity<?> response = fireStationController.getPersonByFireStation(0);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("station Number must be between 1 and 4 inclusive.");
        verifyNoInteractions(fireStationService);
    }

    @Test
    void getPersonByFireStation_shouldReturn400_whenStationNumberIsNegative() {
        // WHEN
        ResponseEntity<?> response = fireStationController.getPersonByFireStation(-1);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("station Number must be between 1 and 4 inclusive.");
        verifyNoInteractions(fireStationService);
    }

    // -----------------------------------------------------------------------
    // stationNumber > 4 → 400
    // -----------------------------------------------------------------------
    @Test
    void getPersonByFireStation_shouldReturn400_whenStationNumberIsAbove4() {
        // WHEN
        ResponseEntity<?> response = fireStationController.getPersonByFireStation(5);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("station Number must be between 1 and 4 inclusive.");
        verifyNoInteractions(fireStationService);
    }

    // -----------------------------------------------------------------------
    // Service retourne null → 404
    // -----------------------------------------------------------------------
    @Test
    void getPersonByFireStation_shouldReturn404_whenServiceReturnsNull() {
        // GIVEN
        when(fireStationService.getPersonsByFireStationNumber("2")).thenReturn(null);

        // WHEN
        ResponseEntity<?> response = fireStationController.getPersonByFireStation(2);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("No data found for station 2");
        verify(fireStationService, times(1)).getPersonsByFireStationNumber("2");
    }

    // -----------------------------------------------------------------------
    // Service lève une exception → 500
    // -----------------------------------------------------------------------
    @Test
    void getPersonByFireStation_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        when(fireStationService.getPersonsByFireStationNumber("3"))
                .thenThrow(new RuntimeException("Database error"));

        // WHEN
        ResponseEntity<?> response = fireStationController.getPersonByFireStation(3);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Internal Server Error");
        verify(fireStationService, times(1)).getPersonsByFireStationNumber("3");
    }
}