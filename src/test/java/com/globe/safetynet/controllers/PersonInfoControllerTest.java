package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.PersonInfoDTO;
import com.globe.safetynet.services.PersonInfoService;
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
class PersonInfoControllerTest {

    @Mock
    private PersonInfoService personInfoService;

    @InjectMocks
    private PersonInfoController personInfoController;

    private PersonInfoDTO dto1;
    private PersonInfoDTO dto2;

    @BeforeEach
    void setUp() {
        dto1 = new PersonInfoDTO("John", "Boyd", "1509 Culver St", "john@email.com", 36,
                List.of("aznol:350mg"), List.of("nillacilan"));
        dto2 = new PersonInfoDTO("Jane", "Boyd", "1509 Culver St", "jane@email.com", 32,
                List.of(), List.of());
    }

    // -----------------------------------------------------------------------
    // Cas nominal : des résultats sont trouvés → 200
    // -----------------------------------------------------------------------
    @Test
    void getPersonInfoLastName_shouldReturn200_whenServiceReturnsData() {
        // GIVEN
        String lastName = "boyd";
        // Le controller capitalise : "Boyd"
        when(personInfoService.getPersonInfoByLastname("Boyd")).thenReturn(List.of(dto1, dto2));

        // WHEN
        ResponseEntity<?> response = personInfoController.getPersonInfoLastName(lastName);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(List.of(dto1, dto2));
        verify(personInfoService, times(1)).getPersonInfoByLastname("Boyd");
    }

    // -----------------------------------------------------------------------
    // La capitalisation fonctionne aussi quand le nom est déjà en majuscule
    // -----------------------------------------------------------------------
    @Test
    void getPersonInfoLastName_shouldCapitalizeFirstLetter_andReturn200() {
        // GIVEN
        String lastName = "DUPONT";   // charAt(0)='D' → "Dupont"
        when(personInfoService.getPersonInfoByLastname("DUPONT")).thenReturn(List.of(dto1));

        // WHEN
        ResponseEntity<?> response = personInfoController.getPersonInfoLastName(lastName);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Le service est appelé avec la version capitalisée
        verify(personInfoService).getPersonInfoByLastname("DUPONT");
    }

    // -----------------------------------------------------------------------
    // Le service retourne une liste vide → 404
    // -----------------------------------------------------------------------
    @Test
    void getPersonInfoLastName_shouldReturn404_whenServiceReturnsEmptyList() {
        // GIVEN
        String lastName = "unknown";
        when(personInfoService.getPersonInfoByLastname("Unknown")).thenReturn(List.of());

        // WHEN
        ResponseEntity<?> response = personInfoController.getPersonInfoLastName(lastName);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(personInfoService, times(1)).getPersonInfoByLastname("Unknown");
    }

    // -----------------------------------------------------------------------
    // Le service lève une exception → 500
    // -----------------------------------------------------------------------
    @Test
    void getPersonInfoLastName_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        String lastName = "error";
        when(personInfoService.getPersonInfoByLastname("Error"))
                .thenThrow(new RuntimeException("Database unavailable"));

        // WHEN
        ResponseEntity<?> response = personInfoController.getPersonInfoLastName(lastName);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Internal Server Error");
        verify(personInfoService, times(1)).getPersonInfoByLastname("Error");
    }

    // -----------------------------------------------------------------------
    // lastName d'un seul caractère : la capitalisation ne plante pas
    // -----------------------------------------------------------------------
    @Test
    void getPersonInfoLastName_shouldHandleSingleCharLastName() {
        // GIVEN — "a" → capitalize → "A", puis isEmpty() = false
        String lastName = "a";
        when(personInfoService.getPersonInfoByLastname("A")).thenReturn(List.of(dto1));

        // WHEN
        ResponseEntity<?> response = personInfoController.getPersonInfoLastName(lastName);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(personInfoService).getPersonInfoByLastname("A");
    }

}