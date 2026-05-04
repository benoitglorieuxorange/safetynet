package com.globe.safetynet.repository;

import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.MedicalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordRepositoryTest {

    @Mock
    private JsonRepositoryBase jsonRepository;

    @InjectMocks
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private Data data;

    private List<MedicalRecord> medicalRecords;

    // ------------------------------------------------------------------ setup

    @BeforeEach
    void setUp() {
        medicalRecords = new ArrayList<>();
        lenient().when(jsonRepository.getData()).thenReturn(data);
        lenient().when(data.getMedicalRecords()).thenReturn(medicalRecords);
    }


    private MedicalRecord buildRecord(String firstName, String lastName) {
        MedicalRecord r = new MedicalRecord();
        r.setFirstName(firstName);
        r.setLastName(lastName);
        r.setBirthdate("01/01/1990");
        r.setMedications(new ArrayList<>(List.of("aspirin:100mg")));
        r.setAllergies(new ArrayList<>(List.of("peanut")));
        return r;
    }

    // ================================================================== add()

    @Test
    void add_shouldAddRecordToListAndSave() {
        MedicalRecord record = buildRecord("John", "Doe");

        MedicalRecord result = medicalRecordRepository.add(record);

        assertThat(medicalRecords).hasSize(1).contains(record);
        assertThat(result).isSameAs(record);
        verify(jsonRepository, times(1)).saveData();
    }

    @Test
    void add_shouldThrowWhenRecordIsNull() {
        assertThatThrownBy(() -> medicalRecordRepository.add(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("MedicalRecord cannot be null");

        verify(jsonRepository, never()).saveData();
    }

    @Test
    void add_shouldReturnTheAddedRecord() {
        MedicalRecord record = buildRecord("Jane", "Smith");

        MedicalRecord result = medicalRecordRepository.add(record);

        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
    }

    // ================================================================ update()

    @Test
    void update_shouldUpdateFieldsAndSaveWhenRecordExists() {
        MedicalRecord existing = buildRecord("John", "Doe");
        medicalRecords.add(existing);

        MedicalRecord patch = new MedicalRecord();
        patch.setBirthdate("15/06/1985");
        patch.setMedications(List.of("doliprane:500mg"));
        patch.setAllergies(List.of("shellfish"));

        Optional<MedicalRecord> result = medicalRecordRepository.update("John", "Doe", patch);

        assertThat(result).isPresent();
        assertThat(result.get().getBirthdate()).isEqualTo("15/06/1985");
        assertThat(result.get().getMedications()).containsExactly("doliprane:500mg");
        assertThat(result.get().getAllergies()).containsExactly("shellfish");
        verify(jsonRepository, times(1)).saveData();
    }

    @Test
    void update_shouldBeCaseInsensitiveOnName() {
        medicalRecords.add(buildRecord("John", "Doe"));

        MedicalRecord patch = new MedicalRecord();
        patch.setBirthdate("20/03/2000");

        Optional<MedicalRecord> result = medicalRecordRepository.update("JOHN", "DOE", patch);

        assertThat(result).isPresent();
        assertThat(result.get().getBirthdate()).isEqualTo("20/03/2000");
    }

    @Test
    void update_shouldReturnEmptyAndNotSaveWhenRecordNotFound() {
        medicalRecords.add(buildRecord("John", "Doe"));

        MedicalRecord patch = new MedicalRecord();
        patch.setBirthdate("01/01/2000");

        Optional<MedicalRecord> result = medicalRecordRepository.update("Unknown", "Person", patch);

        assertThat(result).isEmpty();
        verify(jsonRepository, never()).saveData();
    }

    @Test
    void update_shouldNotOverwriteFieldsWhenPatchFieldIsNull() {
        MedicalRecord existing = buildRecord("John", "Doe");
        medicalRecords.add(existing);

        // patch with only birthdate — medications & allergies stay null
        MedicalRecord patch = new MedicalRecord();
        patch.setBirthdate("31/12/1999");
        patch.setMedications(null);
        patch.setAllergies(null);

        medicalRecordRepository.update("John", "Doe", patch);

        assertThat(existing.getBirthdate()).isEqualTo("31/12/1999");
        assertThat(existing.getMedications()).containsExactly("aspirin:100mg"); // unchanged
        assertThat(existing.getAllergies()).containsExactly("peanut");           // unchanged
    }

    @Test
    void update_shouldHandleMultipleRecordsAndUpdateCorrectOne() {
        MedicalRecord john = buildRecord("John", "Doe");
        MedicalRecord jane = buildRecord("Jane", "Smith");
        medicalRecords.addAll(Arrays.asList(john, jane));

        MedicalRecord patch = new MedicalRecord();
        patch.setBirthdate("10/10/1995");

        medicalRecordRepository.update("Jane", "Smith", patch);

        assertThat(jane.getBirthdate()).isEqualTo("10/10/1995");
        assertThat(john.getBirthdate()).isEqualTo("01/01/1990"); // untouched
    }

    // ================================================================ delete()

    @Test
    void delete_shouldRemoveRecordAndSaveWhenFound() {
        MedicalRecord record = buildRecord("John", "Doe");
        medicalRecords.add(record);

        Optional<MedicalRecord> result = medicalRecordRepository.delete("John", "Doe");

        assertThat(result).isPresent().contains(record);
        assertThat(medicalRecords).isEmpty();
        verify(jsonRepository, times(1)).saveData();
    }

    @Test
    void delete_shouldBeCaseInsensitiveOnName() {
        medicalRecords.add(buildRecord("John", "Doe"));

        Optional<MedicalRecord> result = medicalRecordRepository.delete("john", "doe");

        assertThat(result).isPresent();
        assertThat(medicalRecords).isEmpty();
    }

    @Test
    void delete_shouldReturnEmptyAndNotSaveWhenRecordNotFound() {
        medicalRecords.add(buildRecord("John", "Doe"));

        Optional<MedicalRecord> result = medicalRecordRepository.delete("Unknown", "Person");

        assertThat(result).isEmpty();
        assertThat(medicalRecords).hasSize(1); // list unchanged
        verify(jsonRepository, never()).saveData();
    }

    @Test
    void delete_shouldOnlyRemoveMatchingRecord() {
        MedicalRecord john = buildRecord("John", "Doe");
        MedicalRecord jane = buildRecord("Jane", "Smith");
        medicalRecords.addAll(Arrays.asList(john, jane));

        medicalRecordRepository.delete("John", "Doe");

        assertThat(medicalRecords).hasSize(1).contains(jane);
    }

    @Test
    void delete_shouldReturnEmptyWhenListIsEmpty() {
        Optional<MedicalRecord> result = medicalRecordRepository.delete("John", "Doe");

        assertThat(result).isEmpty();
        verify(jsonRepository, never()).saveData();
    }
}