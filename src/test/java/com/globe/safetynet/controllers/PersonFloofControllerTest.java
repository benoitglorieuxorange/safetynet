package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.PersonFloodDTO;
import com.globe.safetynet.services.FloodService;
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
class PersonFloofControllerTest {

    @Mock
    private FloodService floodService;

    @InjectMocks
    private PersonFloofController personFloofController;

    private PersonFloodDTO dto1;
    private PersonFloodDTO dto2;

    @BeforeEach
    void setUp() {
        dto1 = new PersonFloodDTO("John", "Boyd", "1509 Culver St", "841-874-6512", 36,
                List.of("aznol:350mg"), List.of("nillacilan"));
        dto2 = new PersonFloodDTO("Jane", "Boyd", "1509 Culver St", "841-874-6513", 32,
                List.of(), List.of());
    }

    // -----------------------------------------------------------------------
    // Cas nominal : une seule station valide → 200
    // -----------------------------------------------------------------------
    @Test
    void getFlood_shouldReturn200_whenSingleValidStation() {
        // GIVEN
        List<String> stations = List.of("1");
        when(floodService.getPersonByFireStationList(List.of("1"))).thenReturn(List.of(dto1, dto2));

        // WHEN
        ResponseEntity<?> response = personFloofController.getFlood(stations);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(List.of(dto1, dto2));
        verify(floodService, times(1)).getPersonByFireStationList(List.of("1"));
    }

    // -----------------------------------------------------------------------
    // Plusieurs stations valides → 200
    // -----------------------------------------------------------------------
    @Test
    void getFlood_shouldReturn200_whenMultipleValidStations() {
        // GIVEN
        List<String> stations = List.of("1", "3", "5");
        when(floodService.getPersonByFireStationList(List.of("1", "3", "5")))
                .thenReturn(List.of(dto1, dto2));

        // WHEN
        ResponseEntity<?> response = personFloofController.getFlood(stations);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(floodService).getPersonByFireStationList(List.of("1", "3", "5"));
    }

    // -----------------------------------------------------------------------
    // Numéro de station invalide → 400
    // -----------------------------------------------------------------------
    @Test
    void getFlood_shouldReturn400_whenStationNumberIsInvalid() {
        // GIVEN — "6" n'est pas dans la liste autorisée
        List<String> stations = List.of("6");

        // WHEN
        ResponseEntity<?> response = personFloofController.getFlood(stations);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Use : 1, 2, 3, 4, 5 for FireStation Number");
        verifyNoInteractions(floodService);
    }

    // -----------------------------------------------------------------------
    // Mix valide + invalide → 400
    // -----------------------------------------------------------------------
    @Test
    void getFlood_shouldReturn400_whenMixedValidAndInvalidStations() {
        // GIVEN
        List<String> stations = List.of("1", "99");

        // WHEN
        ResponseEntity<?> response = personFloofController.getFlood(stations);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(floodService);
    }

    // -----------------------------------------------------------------------
    // Station passée avec séparateur virgule dans la valeur ("1,2") → normalisée
    // -----------------------------------------------------------------------
    @Test
    void getFlood_shouldReturn200_whenStationsPassedAsCommaSeparatedString() {
        // GIVEN — Spring peut envoyer "1,2" comme élément unique de la liste
        List<String> stations = List.of("1,2");
        // Après normalisation : ["1", "2"]
        when(floodService.getPersonByFireStationList(List.of("1", "2")))
                .thenReturn(List.of(dto1));

        // WHEN
        ResponseEntity<?> response = personFloofController.getFlood(stations);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(floodService).getPersonByFireStationList(List.of("1", "2"));
    }

    // -----------------------------------------------------------------------
    // Station avec séparateur point ("1.2") → normalisée via replace(".", ",")
    // -----------------------------------------------------------------------
    @Test
    void getFlood_shouldReturn200_whenStationsPassedWithDotSeparator() {
        // GIVEN — "1.2" → remplacé en "1,2" → splitté en ["1", "2"]
        List<String> stations = List.of("1.2");
        when(floodService.getPersonByFireStationList(List.of("1", "2")))
                .thenReturn(List.of(dto1));

        // WHEN
        ResponseEntity<?> response = personFloofController.getFlood(stations);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(floodService).getPersonByFireStationList(List.of("1", "2"));
    }

    // -----------------------------------------------------------------------
    // Valeur avec espaces autour ("  1  ") → trimée correctement
    // -----------------------------------------------------------------------
    @Test
    void getFlood_shouldReturn200_whenStationNumberHasWhitespace() {
        // GIVEN
        List<String> stations = List.of("  1  ");
        when(floodService.getPersonByFireStationList(List.of("1")))
                .thenReturn(List.of(dto1));

        // WHEN
        ResponseEntity<?> response = personFloofController.getFlood(stations);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(floodService).getPersonByFireStationList(List.of("1"));
    }

    // -----------------------------------------------------------------------
    // Le service retourne null → 404
    // -----------------------------------------------------------------------
    @Test
    void getFlood_shouldReturn404_whenServiceReturnsNull() {
        // GIVEN
        List<String> stations = List.of("2");
        when(floodService.getPersonByFireStationList(List.of("2"))).thenReturn(null);

        // WHEN
        ResponseEntity<?> response = personFloofController.getFlood(stations);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(floodService).getPersonByFireStationList(List.of("2"));
    }

    // -----------------------------------------------------------------------
    // Le service lève une exception → 500
    // -----------------------------------------------------------------------
    @Test
    void getFlood_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        List<String> stations = List.of("3");
        when(floodService.getPersonByFireStationList(List.of("3")))
                .thenThrow(new RuntimeException("Database error"));

        // WHEN
        ResponseEntity<?> response = personFloofController.getFlood(stations);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Internal Server Error");
        verify(floodService).getPersonByFireStationList(List.of("3"));
    }

    // -----------------------------------------------------------------------
    // Service retourne liste vide (non null) → 200 avec body vide
    // -----------------------------------------------------------------------
    @Test
    void getFlood_shouldReturn200WithEmptyList_whenServiceReturnsEmptyList() {
        // GIVEN
        List<String> stations = List.of("4");
        when(floodService.getPersonByFireStationList(List.of("4"))).thenReturn(List.of());

        // WHEN
        ResponseEntity<?> response = personFloofController.getFlood(stations);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(List.of());
        verify(floodService).getPersonByFireStationList(List.of("4"));
    }
}