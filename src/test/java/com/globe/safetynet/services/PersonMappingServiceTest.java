package com.globe.safetynet.services;

import com.globe.safetynet.dtos.MedicalRecordDTO;
import com.globe.safetynet.dtos.PersonMedicalData;
import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.utils.PersonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("PersonMappingService")
class PersonMappingServiceTest {

    private PersonMappingService service;

    @BeforeEach
    void setUp() {
        service = new PersonMappingService();
    }


    private Person buildPerson(String firstName, String lastName, String address, String email, String phone) {
        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setAddress(address);
        p.setEmail(email);
        p.setPhone(phone);
        return p;
    }

    private MedicalRecord buildMedicalRecord(String firstName, String lastName, String birthdate,
                                             List<String> medications, List<String> allergies) {
        MedicalRecord mr = new MedicalRecord();
        mr.setFirstName(firstName);
        mr.setLastName(lastName);
        mr.setBirthdate(birthdate);
        mr.setMedications(medications);
        mr.setAllergies(allergies);
        return mr;
    }

    private Data buildData(List<MedicalRecord> medicalRecords) {
        Data data = new Data();
        data.setMedicalRecords(medicalRecords);
        return data;
    }

    
    // MedicalRecordMap


    @Test
    @DisplayName("buildMedicalRecordMap - retourne une entrée par personne dont le dossier existe")
    void buildMedicalRecordMap_returnsOneEntryPerMatchingPerson() {
        Person benoit = buildPerson("Benoit", "Glorieux", "1 Rue De La Street", "benoit@machin.com", "111-000");
        Person anne = buildPerson("Anne", "Glorieux", "2 Rue Du Chemin", "anne@machin.com", "222-000");
        MedicalRecord mrJohn = buildMedicalRecord("Benoit", "Glorieux", "01/02/1972", List.of("aspirin:100mg"), List.of("pollen"));
        MedicalRecord mrJane = buildMedicalRecord("Anne", "Glorieux", "02/03/1973", Collections.emptyList(), List.of("cats"));
        Data data = buildData(List.of(mrJohn, mrJane));

        Map<String, MedicalRecordDTO> result = service.buildMedicalRecordMap(data, List.of(benoit, anne));

        assertThat(result)
                .hasSize(2)
                .containsKeys(PersonUtils.buildFullName(benoit), PersonUtils.buildFullName(anne));
    }

    @Test
    @DisplayName("buildMedicalRecordMap - le DTO contient l'âge calculé et les données du dossier médical")
    void buildMedicalRecordMap_dtoContainsAgeAndMedicalRecord() {
        Person benoit = buildPerson("Benoit", "Glorieux", "1 Rue De La Street", "benoit@machin.com", "111-000");
        MedicalRecord mrJohn = buildMedicalRecord("Benoit", "Glorieux", "01/01/1990", List.of("aspirin:100mg"), List.of("pollen"));
        Data data = buildData(List.of(mrJohn));

        Map<String, MedicalRecordDTO> result = service.buildMedicalRecordMap(data, List.of(benoit));

        MedicalRecordDTO dto = result.get(PersonUtils.buildFullName(benoit));
        assertThat(dto).isNotNull();
        assertThat(dto.medicalRecord()).isEqualTo(mrJohn);
        assertThat(dto.age()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("buildMedicalRecordMap - filtre les dossiers médicaux sans personne correspondante")
    void buildMedicalRecordMap_filtersOutRecordsWithNoMatchingPerson() {
        Person benoit = buildPerson("Benoit", "Glorieux", "1 Rue De La Street", "benoit@machin.com", "111-000");
        MedicalRecord mrJohn = buildMedicalRecord("Benoit", "Glorieux", "01/01/1990", List.of("aspirin:100mg"), List.of("pollen"));
        MedicalRecord mrJane = buildMedicalRecord("Anne", "Glorieux", "06/15/1995", Collections.emptyList(), List.of("cats"));
        Data data = buildData(List.of(mrJohn, mrJane));

        Map<String, MedicalRecordDTO> result = service.buildMedicalRecordMap(data, List.of(benoit));

        assertThat(result)
                .hasSize(1)
                .containsKey(PersonUtils.buildFullName(benoit))
                .doesNotContainKey("Anne Glorieux");
    }

    @Test
    @DisplayName("buildMedicalRecordMap - retourne une map vide si aucune personne ne correspond à un dossier")
    void buildMedicalRecordMap_returnsEmptyMapWhenNoPersonMatchesAnyRecord() {
        Person unknown = buildPerson("Ghost", "Rider", "3 Dark Alley", "ghost@machin.com", "000-000");
        MedicalRecord mrJohn = buildMedicalRecord("Benoit", "Glorieux", "01/01/1990", List.of("aspirin:100mg"), List.of("pollen"));
        Data data = buildData(List.of(mrJohn));

        Map<String, MedicalRecordDTO> result = service.buildMedicalRecordMap(data, List.of(unknown));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("buildMedicalRecordMap - retourne une map vide si la liste de personnes est vide")
    void buildMedicalRecordMap_returnsEmptyMapWhenPersonsListIsEmpty() {
        MedicalRecord mrJohn = buildMedicalRecord("Benoit", "Glorieux", "01/01/1990", List.of("aspirin:100mg"), List.of("pollen"));
        Data data = buildData(List.of(mrJohn));

        Map<String, MedicalRecordDTO> result = service.buildMedicalRecordMap(data, Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("buildMedicalRecordMap - retourne une map vide si data ne contient aucun dossier médical")
    void buildMedicalRecordMap_returnsEmptyMapWhenNoMedicalRecordsInData() {
        Person benoit = buildPerson("Benoit", "Glorieux", "1 Rue De La Street", "benoit@machin.com", "111-000");
        Data data = buildData(Collections.emptyList());

        Map<String, MedicalRecordDTO> result = service.buildMedicalRecordMap(data, List.of(benoit));

        assertThat(result).isEmpty();
    }

    // =========================================================
    // extractPersonMedicalData
    // =========================================================

    @Test
    @DisplayName("extractPersonMedicalData - retourne un PersonMedicalData par personne avec dossier")
    void extractPersonMedicalData_returnsOneResultPerPersonWithRecord() {
        Person benoit = buildPerson("Benoit", "Glorieux", "1 Rue De La Street", "benoit@machin.com", "111-000");
        Person anne = buildPerson("Anne", "Glorieux", "2 Rue Du Chemin", "anne@machin.com", "222-000");
        MedicalRecord mrJohn = buildMedicalRecord("Benoit", "Glorieux", "01/01/1990", List.of("aspirin:100mg"), List.of("pollen"));
        MedicalRecord mrJane = buildMedicalRecord("Anne", "Glorieux", "06/15/1995", Collections.emptyList(), List.of("cats"));
        Map<String, MedicalRecordDTO> map = service.buildMedicalRecordMap(buildData(List.of(mrJohn, mrJane)), List.of(benoit, anne));

        List<PersonMedicalData> result = service.extractPersonMedicalData(List.of(benoit, anne), map);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("extractPersonMedicalData - les champs correspondent aux données de la personne et du dossier")
    void extractPersonMedicalData_fieldsMatchPersonAndRecord() {
        Person benoit = buildPerson("Benoit", "Glorieux", "1 Rue De La Street", "benoit@machin.com", "111-000");
        MedicalRecord mrJohn = buildMedicalRecord("Benoit", "Glorieux", "01/01/1990", List.of("aspirin:100mg"), List.of("pollen"));
        Map<String, MedicalRecordDTO> map = service.buildMedicalRecordMap(buildData(List.of(mrJohn)), List.of(benoit));

        List<PersonMedicalData> result = service.extractPersonMedicalData(List.of(benoit), map);

        assertThat(result).hasSize(1);
        PersonMedicalData pmd = result.get(0);
        assertThat(pmd.firstName()).isEqualTo("Benoit");
        assertThat(pmd.lastName()).isEqualTo("Glorieux");
        assertThat(pmd.address()).isEqualTo("1 Rue De La Street");
        assertThat(pmd.email()).isEqualTo("benoit@machin.com");
        assertThat(pmd.phone()).isEqualTo("111-000");
        assertThat(pmd.medications()).containsExactly("aspirin:100mg");
        assertThat(pmd.allergies()).containsExactly("pollen");
        assertThat(pmd.age()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("extractPersonMedicalData - exclut les personnes sans dossier médical dans la map")
    void extractPersonMedicalData_excludesPersonsWithNoMedicalRecord() {
        Person benoit = buildPerson("Benoit", "Glorieux", "1 Rue De La Street", "benoit@machin.com", "111-000");
        Person unknown = buildPerson("Ghost", "Rider", "3 Dark Alley", "ghost@machin.com", "000-000");
        MedicalRecord mrJohn = buildMedicalRecord("Benoit", "Glorieux", "01/01/1990", List.of("aspirin:100mg"), List.of("pollen"));
        Map<String, MedicalRecordDTO> map = service.buildMedicalRecordMap(buildData(List.of(mrJohn)), List.of(benoit));

        List<PersonMedicalData> result = service.extractPersonMedicalData(List.of(benoit, unknown), map);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("Benoit");
    }

    @Test
    @DisplayName("extractPersonMedicalData - retourne une liste vide si la liste de personnes est vide")
    void extractPersonMedicalData_returnsEmptyListWhenPersonsIsEmpty() {
        List<PersonMedicalData> result = service.extractPersonMedicalData(Collections.emptyList(), Collections.emptyMap());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("extractPersonMedicalData - retourne une liste vide si la map est vide")
    void extractPersonMedicalData_returnsEmptyListWhenMapIsEmpty() {
        Person benoit = buildPerson("Benoit", "Glorieux", "1 Rue De La Street", "benoit@machin.com", "111-000");

        List<PersonMedicalData> result = service.extractPersonMedicalData(List.of(benoit), Collections.emptyMap());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("extractPersonMedicalData - gère une personne aux listes de médicaments et allergies vides")
    void extractPersonMedicalData_handlesPersonWithEmptyMedicationsAndAllergies() {
        Person anne = buildPerson("Anne", "Glorieux", "2 Rue Du Chemin", "anne@machin.com", "222-000");
        MedicalRecord mrJane = buildMedicalRecord("Anne", "Glorieux", "06/15/1995", Collections.emptyList(), List.of("cats"));
        Map<String, MedicalRecordDTO> map = service.buildMedicalRecordMap(buildData(List.of(mrJane)), List.of(anne));

        List<PersonMedicalData> result = service.extractPersonMedicalData(List.of(anne), map);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).medications()).isEmpty();
        assertThat(result.get(0).allergies()).containsExactly("cats");
    }
}