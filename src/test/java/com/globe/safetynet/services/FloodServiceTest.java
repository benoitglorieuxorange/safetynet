package com.globe.safetynet.services;

import com.globe.safetynet.dtos.MedicalRecordDTO;
import com.globe.safetynet.dtos.PersonFloodDTO;
import com.globe.safetynet.dtos.PersonMedicalData;
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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FloodServiceTest {

    @Mock
    private JsonRepositoryBase jsonRepositoryBase;

    @Mock
    private PersonMappingService personMappingService;

    @InjectMocks
    private FloodService floodService;

    private Person john;
    private Person jane;
    private MedicalRecord mrJohn;
    private MedicalRecord mrJane;
    private FireStation station1;
    private FireStation station2;
    private Data data;

    @BeforeEach
    void setUp() {
        john = new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com");
        jane = new Person("Jane", "Boyd", "29 15th St",     "Culver", "97451", "841-874-6513", "jane@email.com");

        mrJohn = new MedicalRecord("John", "Boyd", "03/06/1984",
                List.of("aznol:350mg"), List.of("nillacilan"));
        mrJane = new MedicalRecord("Jane", "Boyd", "06/08/1975",
                List.of(), List.of());

        station1 = new FireStation("1509 Culver St", "1");
        station2 = new FireStation("29 15th St",     "2");

        data = new Data(
                List.of(john, jane),
                List.of(station1, station2),
                List.of(mrJohn, mrJane)
        );
    }

    // -----------------------------------------------------------------------
    // Cas nominal : une station, un résultat
    // -----------------------------------------------------------------------
    @Test
    void getPersonByFireStationList_shouldReturnPersons_forSingleStation() {
        // GIVEN
        when(jsonRepositoryBase.getData()).thenReturn(data);

        MedicalRecordDTO dtoJohn = new MedicalRecordDTO(mrJohn, 40);
        Map<String, MedicalRecordDTO> medMap = Map.of("John BOYD", dtoJohn);

        PersonMedicalData pmd = new PersonMedicalData(
                "John", "Boyd", "1509 Culver St",
                "jaboyd@email.com", "841-874-6512", 40,
                List.of("aznol:350mg"), List.of("nillacilan"));

        when(personMappingService.buildMedicalRecordMap(eq(data), anyList())).thenReturn(medMap);
        when(personMappingService.extractPersonMedicalData(anyList(), eq(medMap))).thenReturn(List.of(pmd));

        // WHEN
        List<PersonFloodDTO> result = floodService.getPersonByFireStationList(List.of("1"));

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("John");
        assertThat(result.get(0).lastName()).isEqualTo("Boyd");
        assertThat(result.get(0).address()).isEqualTo("1509 Culver St");
        assertThat(result.get(0).phone()).isEqualTo("841-874-6512");
        assertThat(result.get(0).age()).isEqualTo(40);
        assertThat(result.get(0).medications()).containsExactly("aznol:350mg");
        assertThat(result.get(0).allergies()).containsExactly("nillacilan");
        verify(jsonRepositoryBase, times(1)).getData();
    }

    // -----------------------------------------------------------------------
    // Plusieurs stations → résultats agrégés et triés par adresse
    // -----------------------------------------------------------------------
    @Test
    void getPersonByFireStationList_shouldReturnSortedPersons_forMultipleStations() {
        // GIVEN
        when(jsonRepositoryBase.getData()).thenReturn(data);

        MedicalRecordDTO dtoJohn = new MedicalRecordDTO(mrJohn, 40);
        MedicalRecordDTO dtoJane = new MedicalRecordDTO(mrJane, 49);
        Map<String, MedicalRecordDTO> medMap1 = Map.of("John BOYD", dtoJohn);
        Map<String, MedicalRecordDTO> medMap2 = Map.of("Jane BOYD", dtoJane);

        PersonMedicalData pmdJohn = new PersonMedicalData(
                "John", "Boyd", "1509 Culver St",
                "jaboyd@email.com", "841-874-6512", 40,
                List.of("aznol:350mg"), List.of("nillacilan"));
        PersonMedicalData pmdJane = new PersonMedicalData(
                "Jane", "Boyd", "29 15th St",
                "jane@email.com", "841-874-6513", 49,
                List.of(), List.of());

        when(personMappingService.buildMedicalRecordMap(eq(data), anyList()))
                .thenReturn(medMap1)
                .thenReturn(medMap2);
        when(personMappingService.extractPersonMedicalData(anyList(), any()))
                .thenReturn(List.of(pmdJohn))
                .thenReturn(List.of(pmdJane));

        // WHEN
        List<PersonFloodDTO> result = floodService.getPersonByFireStationList(List.of("1", "2"));

        // THEN
        assertThat(result).hasSize(2);
        // Tri par adresse : "1509 Culver St" < "29 15th St"
        assertThat(result.get(0).address()).isEqualTo("1509 Culver St");
        assertThat(result.get(1).address()).isEqualTo("29 15th St");
    }

    // -----------------------------------------------------------------------
    // Station sans adresse associée → liste vide
    // -----------------------------------------------------------------------
    @Test
    void getPersonByFireStationList_shouldReturnEmptyList_whenStationHasNoAddress() {
        // GIVEN — station "9" n'existe pas dans les données
        when(jsonRepositoryBase.getData()).thenReturn(data);
        when(personMappingService.buildMedicalRecordMap(eq(data), eq(List.of()))).thenReturn(Map.of());
        when(personMappingService.extractPersonMedicalData(eq(List.of()), eq(Map.of()))).thenReturn(List.of());

        // WHEN
        List<PersonFloodDTO> result = floodService.getPersonByFireStationList(List.of("9"));

        // THEN
        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // Liste de stations vide → liste vide sans appel aux services internes
    // -----------------------------------------------------------------------
    @Test
    void getPersonByFireStationList_shouldReturnEmptyList_whenStationListIsEmpty() {
        // GIVEN
        when(jsonRepositoryBase.getData()).thenReturn(data);

        // WHEN
        List<PersonFloodDTO> result = floodService.getPersonByFireStationList(List.of());

        // THEN
        assertThat(result).isEmpty();
        verifyNoInteractions(personMappingService);
    }

    // -----------------------------------------------------------------------
    // getData() retourne null → IllegalArgumentException via validateData
    // -----------------------------------------------------------------------
    @Test
    void getPersonByFireStationList_shouldThrowException_whenDataIsNull() {
        // GIVEN
        when(jsonRepositoryBase.getData()).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> floodService.getPersonByFireStationList(List.of("1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Repository return null data");
    }

    // -----------------------------------------------------------------------
    // personMappingService retourne liste vide → résultat vide pour la station
    // -----------------------------------------------------------------------
    @Test
    void getPersonByFireStationList_shouldReturnEmptyList_whenNoMedicalDataExtracted() {
        // GIVEN
        when(jsonRepositoryBase.getData()).thenReturn(data);
        when(personMappingService.buildMedicalRecordMap(eq(data), anyList())).thenReturn(Map.of());
        when(personMappingService.extractPersonMedicalData(anyList(), eq(Map.of()))).thenReturn(List.of());

        // WHEN
        List<PersonFloodDTO> result = floodService.getPersonByFireStationList(List.of("1"));

        // THEN
        assertThat(result).isEmpty();
    }
}