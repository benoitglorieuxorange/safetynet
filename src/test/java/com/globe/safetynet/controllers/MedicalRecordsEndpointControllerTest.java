package com.globe.safetynet.controllers;

import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.services.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordsEndpointControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService;

    @InjectMocks
    private MedicalRecordsEndpointController medicalRecordsEndpointController;

    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        medicalRecord = new MedicalRecord(
                "John", "Boyd", "03/06/1984",
                List.of("aznol:350mg", "hydrapermazol:100mg"),
                List.of("nillacilan")
        );
    }

    // -----------------------------------------------------------------------
    // POST /medicalrecord
    // -----------------------------------------------------------------------

    @Test
    void addMedicalRecord_shouldReturn200_whenServiceReturnsRecord() {
        // GIVEN
        when(medicalRecordService.addMedicalRecord(medicalRecord)).thenReturn(medicalRecord);

        // WHEN
        ResponseEntity<Optional<MedicalRecord>> response =
                medicalRecordsEndpointController.addMedicalRecord(medicalRecord);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isPresent().contains(medicalRecord);
        verify(medicalRecordService, times(1)).addMedicalRecord(medicalRecord);
    }

    @Test
    void addMedicalRecord_shouldReturn200_whenServiceReturnsNull() {
        // GIVEN — Optional.ofNullable(null) = Optional.empty(), qui n'est pas null
        // donc la garde `if (response == null)` n'est jamais atteinte → 200 avec Optional.empty()
        when(medicalRecordService.addMedicalRecord(medicalRecord)).thenReturn(null);

        // WHEN
        ResponseEntity<Optional<MedicalRecord>> response =
                medicalRecordsEndpointController.addMedicalRecord(medicalRecord);

        // THEN — comportement actuel à documenter (Optional.ofNullable(null) != null)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(medicalRecordService, times(1)).addMedicalRecord(medicalRecord);
    }

    @Test
    void addMedicalRecord_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        when(medicalRecordService.addMedicalRecord(medicalRecord))
                .thenThrow(new RuntimeException("Database error"));

        // WHEN
        ResponseEntity<Optional<MedicalRecord>> response =
                medicalRecordsEndpointController.addMedicalRecord(medicalRecord);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(medicalRecordService, times(1)).addMedicalRecord(medicalRecord);
    }

    // -----------------------------------------------------------------------
    // DELETE /medicalrecord
    // -----------------------------------------------------------------------

    @Test
    void deleteMedicalRecord_shouldReturn200_whenServiceReturnsRecord() {
        // GIVEN
        when(medicalRecordService.deleteMedicalRecord(medicalRecord))
                .thenReturn(Optional.of(medicalRecord));

        // WHEN
        ResponseEntity<Optional<MedicalRecord>> response =
                medicalRecordsEndpointController.deleteMedicalRecord(medicalRecord);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isPresent().contains(medicalRecord);
        verify(medicalRecordService, times(1)).deleteMedicalRecord(medicalRecord);
    }

    @Test
    void deleteMedicalRecord_shouldReturn404_whenServiceReturnsNull() {
        // GIVEN
        when(medicalRecordService.deleteMedicalRecord(medicalRecord)).thenReturn(null);

        // WHEN
        ResponseEntity<Optional<MedicalRecord>> response =
                medicalRecordsEndpointController.deleteMedicalRecord(medicalRecord);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(medicalRecordService, times(1)).deleteMedicalRecord(medicalRecord);
    }

    @Test
    void deleteMedicalRecord_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        when(medicalRecordService.deleteMedicalRecord(medicalRecord))
                .thenThrow(new RuntimeException("Database error"));

        // WHEN
        ResponseEntity<Optional<MedicalRecord>> response =
                medicalRecordsEndpointController.deleteMedicalRecord(medicalRecord);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(medicalRecordService, times(1)).deleteMedicalRecord(medicalRecord);
    }

    // -----------------------------------------------------------------------
    // PUT /medicalrecord
    // -----------------------------------------------------------------------

    @Test
    void updateMedicalRecord_shouldReturn200_whenServiceReturnsRecord() {
        // GIVEN
        when(medicalRecordService.updateMedicalRecord(medicalRecord))
                .thenReturn(Optional.of(medicalRecord));

        // WHEN
        ResponseEntity<Optional<MedicalRecord>> response =
                medicalRecordsEndpointController.updateMedicalRecord(medicalRecord);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isPresent().contains(medicalRecord);
        verify(medicalRecordService, times(1)).updateMedicalRecord(medicalRecord);
    }

    @Test
    void updateMedicalRecord_shouldReturn404_whenServiceReturnsNull() {
        // GIVEN
        when(medicalRecordService.updateMedicalRecord(medicalRecord)).thenReturn(null);

        // WHEN
        ResponseEntity<Optional<MedicalRecord>> response =
                medicalRecordsEndpointController.updateMedicalRecord(medicalRecord);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(medicalRecordService, times(1)).updateMedicalRecord(medicalRecord);
    }

    @Test
    void updateMedicalRecord_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        when(medicalRecordService.updateMedicalRecord(medicalRecord))
                .thenThrow(new RuntimeException("Database error"));

        // WHEN
        ResponseEntity<Optional<MedicalRecord>> response =
                medicalRecordsEndpointController.updateMedicalRecord(medicalRecord);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(medicalRecordService, times(1)).updateMedicalRecord(medicalRecord);
    }
}