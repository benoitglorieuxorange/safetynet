package com.globe.safetynet.services;

import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.repository.MedicalRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

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
    // addMedicalRecord
    // -----------------------------------------------------------------------

    @Test
    void addMedicalRecord_shouldReturnSavedRecord_whenRepositorySucceeds() {
        // GIVEN
        when(medicalRecordRepository.add(medicalRecord)).thenReturn(medicalRecord);

        // WHEN
        MedicalRecord result = medicalRecordService.addMedicalRecord(medicalRecord);

        // THEN
        assertThat(result).isEqualTo(medicalRecord);
        verify(medicalRecordRepository, times(1)).add(medicalRecord);
    }

    @Test
    void addMedicalRecord_shouldReturnNull_whenRepositoryReturnsNull() {
        // GIVEN
        when(medicalRecordRepository.add(medicalRecord)).thenReturn(null);

        // WHEN
        MedicalRecord result = medicalRecordService.addMedicalRecord(medicalRecord);

        // THEN
        assertThat(result).isNull();
        verify(medicalRecordRepository, times(1)).add(medicalRecord);
    }

    @Test
    void addMedicalRecord_shouldPropagateException_whenRepositoryThrows() {
        // GIVEN
        when(medicalRecordRepository.add(medicalRecord))
                .thenThrow(new IllegalArgumentException("MedicalRecord cannot be null"));

        // WHEN / THEN
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> medicalRecordService.addMedicalRecord(medicalRecord)
        );
        verify(medicalRecordRepository, times(1)).add(medicalRecord);
    }

    // -----------------------------------------------------------------------
    // updateMedicalRecord
    // -----------------------------------------------------------------------

    @Test
    void updateMedicalRecord_shouldReturnUpdatedRecord_whenRecordExists() {
        // GIVEN
        when(medicalRecordRepository.update("John", "Boyd", medicalRecord))
                .thenReturn(Optional.of(medicalRecord));

        // WHEN
        Optional<MedicalRecord> result = medicalRecordService.updateMedicalRecord(medicalRecord);

        // THEN
        assertThat(result).isPresent().contains(medicalRecord);
        verify(medicalRecordRepository, times(1)).update("John", "Boyd", medicalRecord);
    }

    @Test
    void updateMedicalRecord_shouldReturnEmptyOptional_whenRecordNotFound() {
        // GIVEN
        when(medicalRecordRepository.update("John", "Boyd", medicalRecord))
                .thenReturn(Optional.empty());

        // WHEN
        Optional<MedicalRecord> result = medicalRecordService.updateMedicalRecord(medicalRecord);

        // THEN
        assertThat(result).isEmpty();
        verify(medicalRecordRepository, times(1)).update("John", "Boyd", medicalRecord);
    }

    @Test
    void updateMedicalRecord_shouldOnlyUpdateNonNullFields() {
        // GIVEN — record avec seulement birthdate modifiée, medications et allergies null
        MedicalRecord partialUpdate = new MedicalRecord("John", "Boyd", "01/01/1990", null, null);
        MedicalRecord updatedRecord = new MedicalRecord("John", "Boyd", "01/01/1990",
                List.of("aznol:350mg"), List.of("nillacilan"));
        when(medicalRecordRepository.update("John", "Boyd", partialUpdate))
                .thenReturn(Optional.of(updatedRecord));

        // WHEN
        Optional<MedicalRecord> result = medicalRecordService.updateMedicalRecord(partialUpdate);

        // THEN
        assertThat(result).isPresent();
        assertThat(result.get().getBirthdate()).isEqualTo("01/01/1990");
        assertThat(result.get().getMedications()).containsExactly("aznol:350mg");
        verify(medicalRecordRepository, times(1)).update("John", "Boyd", partialUpdate);
    }

    // -----------------------------------------------------------------------
    // deleteMedicalRecord
    // -----------------------------------------------------------------------

    @Test
    void deleteMedicalRecord_shouldReturnDeletedRecord_whenRecordExists() {
        // GIVEN
        when(medicalRecordRepository.delete("John", "Boyd"))
                .thenReturn(Optional.of(medicalRecord));

        // WHEN
        Optional<MedicalRecord> result = medicalRecordService.deleteMedicalRecord(medicalRecord);

        // THEN
        assertThat(result).isPresent().contains(medicalRecord);
        verify(medicalRecordRepository, times(1)).delete("John", "Boyd");
    }

    @Test
    void deleteMedicalRecord_shouldReturnEmptyOptional_whenRecordNotFound() {
        // GIVEN
        when(medicalRecordRepository.delete("John", "Boyd"))
                .thenReturn(Optional.empty());

        // WHEN
        Optional<MedicalRecord> result = medicalRecordService.deleteMedicalRecord(medicalRecord);

        // THEN
        assertThat(result).isEmpty();
        verify(medicalRecordRepository, times(1)).delete("John", "Boyd");
    }

    @Test
    void deleteMedicalRecord_shouldUseFirstNameAndLastName_fromEntity() {
        // GIVEN — vérifie que le service transmet bien firstName et lastName du record
        MedicalRecord other = new MedicalRecord("Jane", "Doe", "01/01/2000", List.of(), List.of());
        when(medicalRecordRepository.delete("Jane", "Doe")).thenReturn(Optional.of(other));

        // WHEN
        Optional<MedicalRecord> result = medicalRecordService.deleteMedicalRecord(other);

        // THEN
        assertThat(result).isPresent().contains(other);
        verify(medicalRecordRepository, times(1)).delete("Jane", "Doe");
        verify(medicalRecordRepository, never()).delete("John", "Boyd");
    }
}