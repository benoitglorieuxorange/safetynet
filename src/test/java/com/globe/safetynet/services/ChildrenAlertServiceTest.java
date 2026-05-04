package com.globe.safetynet.services;

import com.globe.safetynet.dtos.ChildAlertDTO;
import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepositoryBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChildrenAlertServiceTest {

    @Mock
    private JsonRepositoryBase jsonRepository;

    @InjectMocks
    private ChildrenAlertService childrenAlertService;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // Helpers pour générer des dates dynamiques selon l'âge voulu
    private String birthdateForAge(int age) {
        return LocalDate.now().minusYears(age).format(FMT);
    }

    private Person john;   // adulte
    private Person tom;    // enfant
    private Person lily;   // enfant
    private MedicalRecord mrJohn;
    private MedicalRecord mrTom;
    private MedicalRecord mrLily;

    @BeforeEach
    void setUp() {
        john = new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com");
        tom  = new Person("Tom",  "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6513", "tom@email.com");
        lily = new Person("Lily", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6514", "lily@email.com");

        mrJohn = new MedicalRecord("John", "Boyd", birthdateForAge(36), List.of("aznol:350mg"), List.of("nillacilan"));
        mrTom  = new MedicalRecord("Tom",  "Boyd", birthdateForAge(10), List.of(), List.of());
        mrLily = new MedicalRecord("Lily", "Boyd", birthdateForAge(8),  List.of(), List.of());
    }

    // -----------------------------------------------------------------------
    // Cas nominal : enfants et adultes présents à l'adresse
    // -----------------------------------------------------------------------
    @Test
    void getChildrenByAddress_shouldReturnChildren_withHouseholdMembers() {
        // GIVEN
        Data data = new Data(List.of(john, tom, lily), List.of(), List.of(mrJohn, mrTom, mrLily));
        when(jsonRepository.getData()).thenReturn(data);

        // WHEN
        List<ChildAlertDTO> result = childrenAlertService.getChildrenByAddress("1509 Culver St");

        // THEN
        assertThat(result).hasSize(2);

        ChildAlertDTO tomDto = result.stream().filter(c -> c.firstName().equals("Tom")).findFirst().orElseThrow();
        assertThat(tomDto.age()).isEqualTo(10);
        assertThat(tomDto.householdMembers()).hasSize(1);
        assertThat(tomDto.householdMembers().get(0).firstName()).isEqualTo("John");

        ChildAlertDTO lilyDto = result.stream().filter(c -> c.firstName().equals("Lily")).findFirst().orElseThrow();
        assertThat(lilyDto.age()).isEqualTo(8);
        assertThat(lilyDto.householdMembers()).hasSize(1);
        assertThat(lilyDto.householdMembers().get(0).firstName()).isEqualTo("John");

        verify(jsonRepository, times(1)).getData();
    }

    // -----------------------------------------------------------------------
    // Aucun résident à l'adresse → liste vide
    // -----------------------------------------------------------------------
    @Test
    void getChildrenByAddress_shouldReturnEmptyList_whenNoResidentsAtAddress() {
        // GIVEN
        Data data = new Data(List.of(john), List.of(), List.of(mrJohn));
        when(jsonRepository.getData()).thenReturn(data);

        // WHEN
        List<ChildAlertDTO> result = childrenAlertService.getChildrenByAddress("unknown address");

        // THEN
        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // Résidents présents mais aucun enfant → liste vide
    // -----------------------------------------------------------------------
    @Test
    void getChildrenByAddress_shouldReturnEmptyList_whenNoChildrenAtAddress() {
        // GIVEN — john est adulte (36 ans)
        Data data = new Data(List.of(john), List.of(), List.of(mrJohn));
        when(jsonRepository.getData()).thenReturn(data);

        // WHEN
        List<ChildAlertDTO> result = childrenAlertService.getChildrenByAddress("1509 Culver St");

        // THEN
        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // Personne à exactement 18 ans → considérée comme enfant (age <= 18)
    // -----------------------------------------------------------------------
    @Test
    void getChildrenByAddress_shouldIncludePerson_whenAgeIsExactly18() {
        // GIVEN
        Person teen = new Person("Teen", "Boyd", "1509 Culver St", "Culver", "97451", "000-000-0000", "teen@email.com");
        MedicalRecord mrTeen = new MedicalRecord("Teen", "Boyd", birthdateForAge(18), List.of(), List.of());
        Data data = new Data(List.of(teen), List.of(), List.of(mrTeen));
        when(jsonRepository.getData()).thenReturn(data);

        // WHEN
        List<ChildAlertDTO> result = childrenAlertService.getChildrenByAddress("1509 Culver St");

        // THEN — 18 ans <= 18 → inclus comme enfant
        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("Teen");
        assertThat(result.get(0).age()).isEqualTo(18);
        assertThat(result.get(0).householdMembers()).isEmpty();
    }

    // -----------------------------------------------------------------------
    // Personne à 19 ans → adulte, exclue des enfants
    // -----------------------------------------------------------------------
    @Test
    void getChildrenByAddress_shouldExcludePerson_whenAgeIs19() {
        // GIVEN
        Person adult = new Person("Adult", "Boyd", "1509 Culver St", "Culver", "97451", "000-000-0000", "adult@email.com");
        MedicalRecord mrAdult = new MedicalRecord("Adult", "Boyd", birthdateForAge(19), List.of(), List.of());
        Data data = new Data(List.of(adult), List.of(), List.of(mrAdult));
        when(jsonRepository.getData()).thenReturn(data);

        // WHEN
        List<ChildAlertDTO> result = childrenAlertService.getChildrenByAddress("1509 Culver St");

        // THEN
        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // Résident sans dossier médical → ignoré (pas considéré enfant)
    // -----------------------------------------------------------------------
    @Test
    void getChildrenByAddress_shouldIgnorePerson_whenNoMedicalRecord() {
        // GIVEN — tom n'a pas de dossier médical
        Data data = new Data(List.of(tom), List.of(), List.of());
        when(jsonRepository.getData()).thenReturn(data);

        // WHEN
        List<ChildAlertDTO> result = childrenAlertService.getChildrenByAddress("1509 Culver St");

        // THEN — tom ignoré (pas de medical record) → aucun enfant trouvé
        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // getData() retourne null → IllegalArgumentException via validateData
    // -----------------------------------------------------------------------
    @Test
    void getChildrenByAddress_shouldThrowException_whenDataIsNull() {
        // GIVEN
        when(jsonRepository.getData()).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> childrenAlertService.getChildrenByAddress("1509 Culver St"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Repository return null data");
    }

    // -----------------------------------------------------------------------
    // Plusieurs enfants, plusieurs adultes : householdMembers = adultes uniquement
    // -----------------------------------------------------------------------
    @Test
    void getChildrenByAddress_shouldListOnlyAdults_asHouseholdMembers() {
        // GIVEN
        Data data = new Data(List.of(john, tom, lily), List.of(), List.of(mrJohn, mrTom, mrLily));
        when(jsonRepository.getData()).thenReturn(data);

        // WHEN
        List<ChildAlertDTO> result = childrenAlertService.getChildrenByAddress("1509 Culver St");

        // THEN — household members ne contient que john (adulte), pas tom ni lily (enfants)
        result.forEach(child ->
                assertThat(child.householdMembers())
                        .extracting("firstName")
                        .doesNotContain("Tom", "Lily")
                        .contains("John")
        );
    }
}