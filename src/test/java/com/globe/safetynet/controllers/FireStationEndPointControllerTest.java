package com.globe.safetynet.controllers;

import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.services.FireStationEndPointService;
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
class FireStationEndPointControllerTest {

    @Mock
    private FireStationEndPointService fireStationEndPointService;

    @InjectMocks
    private FireStationEndPointController fireStationEndPointController;

    private FireStation fireStation;

    @BeforeEach
    void setUp() {
        fireStation = new FireStation("1509 Culver St", "3");
    }

    // -----------------------------------------------------------------------
    // POST /firestation
    // -----------------------------------------------------------------------

    @Test
    void addFireStation_shouldReturn200_whenServiceReturnsFireStation() {
        // GIVEN
        when(fireStationEndPointService.addFireStation(fireStation)).thenReturn(fireStation);

        // WHEN
        ResponseEntity<FireStation> response = fireStationEndPointController.addFireStation(fireStation);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(fireStation);
        verify(fireStationEndPointService, times(1)).addFireStation(fireStation);
    }

    @Test
    void addFireStation_shouldReturn400_whenServiceReturnsNull() {
        // GIVEN
        when(fireStationEndPointService.addFireStation(fireStation)).thenReturn(null);

        // WHEN
        ResponseEntity<FireStation> response = fireStationEndPointController.addFireStation(fireStation);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
        verify(fireStationEndPointService, times(1)).addFireStation(fireStation);
    }

    @Test
    void addFireStation_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        when(fireStationEndPointService.addFireStation(fireStation))
                .thenThrow(new RuntimeException("Database error"));

        // WHEN
        ResponseEntity<FireStation> response = fireStationEndPointController.addFireStation(fireStation);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(fireStationEndPointService, times(1)).addFireStation(fireStation);
    }

    // -----------------------------------------------------------------------
    // DELETE /firestation
    // -----------------------------------------------------------------------

    @Test
    void deleteFireStation_shouldReturn200_whenServiceReturnsOptional() {
        // GIVEN
        when(fireStationEndPointService.deleteFireStation(fireStation))
                .thenReturn(Optional.of(fireStation));

        // WHEN
        ResponseEntity<Optional<FireStation>> response =
                fireStationEndPointController.deleteFireStation(fireStation);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isPresent().contains(fireStation);
        verify(fireStationEndPointService, times(1)).deleteFireStation(fireStation);
    }

    @Test
    void deleteFireStation_shouldReturn200_whenServiceReturnsEmptyOptional() {
        // GIVEN — Optional.empty() n'est pas null → la garde ne déclenche pas → 200
        when(fireStationEndPointService.deleteFireStation(fireStation))
                .thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<Optional<FireStation>> response =
                fireStationEndPointController.deleteFireStation(fireStation);

        // THEN — comportement actuel : Optional.empty() != null donc retourne 200
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(fireStationEndPointService, times(1)).deleteFireStation(fireStation);
    }

    @Test
    void deleteFireStation_shouldReturn400_whenServiceReturnsNull() {
        // GIVEN
        when(fireStationEndPointService.deleteFireStation(fireStation)).thenReturn(null);

        // WHEN
        ResponseEntity<Optional<FireStation>> response =
                fireStationEndPointController.deleteFireStation(fireStation);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(fireStationEndPointService, times(1)).deleteFireStation(fireStation);
    }

    @Test
    void deleteFireStation_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        when(fireStationEndPointService.deleteFireStation(fireStation))
                .thenThrow(new RuntimeException("Database error"));

        // WHEN
        ResponseEntity<Optional<FireStation>> response =
                fireStationEndPointController.deleteFireStation(fireStation);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(fireStationEndPointService, times(1)).deleteFireStation(fireStation);
    }

    // -----------------------------------------------------------------------
    // PUT /firestation
    // -----------------------------------------------------------------------

    @Test
    void updateFireStation_shouldReturn200_whenServiceReturnsOptional() {
        // GIVEN
        when(fireStationEndPointService.updateFireStation(fireStation))
                .thenReturn(Optional.of(fireStation));

        // WHEN
        ResponseEntity<Optional<FireStation>> response =
                fireStationEndPointController.updateFireStation(fireStation);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isPresent().contains(fireStation);
        verify(fireStationEndPointService, times(1)).updateFireStation(fireStation);
    }

    @Test
    void updateFireStation_shouldReturn200_whenServiceReturnsEmptyOptional() {
        // GIVEN — Optional.empty() n'est pas null → la garde ne déclenche pas → 200
        when(fireStationEndPointService.updateFireStation(fireStation))
                .thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<Optional<FireStation>> response =
                fireStationEndPointController.updateFireStation(fireStation);

        // THEN — comportement actuel : Optional.empty() != null donc retourne 200
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(fireStationEndPointService, times(1)).updateFireStation(fireStation);
    }

    @Test
    void updateFireStation_shouldReturn400_whenServiceReturnsNull() {
        // GIVEN
        when(fireStationEndPointService.updateFireStation(fireStation)).thenReturn(null);

        // WHEN
        ResponseEntity<Optional<FireStation>> response =
                fireStationEndPointController.updateFireStation(fireStation);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(fireStationEndPointService, times(1)).updateFireStation(fireStation);
    }

    @Test
    void updateFireStation_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        when(fireStationEndPointService.updateFireStation(fireStation))
                .thenThrow(new RuntimeException("Database error"));

        // WHEN
        ResponseEntity<Optional<FireStation>> response =
                fireStationEndPointController.updateFireStation(fireStation);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(fireStationEndPointService, times(1)).updateFireStation(fireStation);
    }
}