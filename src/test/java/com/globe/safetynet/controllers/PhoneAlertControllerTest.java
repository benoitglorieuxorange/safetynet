package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.PhoneAlertDTO;
import com.globe.safetynet.services.PhoneAlertService;
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
class PhoneAlertControllerTest {

    @Mock
    private PhoneAlertService phoneAlertService;

    @InjectMocks
    private PhoneAlertController phoneAlertController;

    private PhoneAlertDTO dto1;
    private PhoneAlertDTO dto2;

    @BeforeEach
    void setUp() {
        dto1 = new PhoneAlertDTO("555-0001");
        dto2 = new PhoneAlertDTO("555-0002");
    }

    // -----------------------------------------------------------------------
    // Cas nominal : la station existe et retourne des numéros
    // -----------------------------------------------------------------------
    @Test
    void getAllPhoneAlerts_shouldReturn200_whenServiceReturnsData() {
        // GIVEN
        String stationNumber = "1";
        List<PhoneAlertDTO> phones = List.of(dto1, dto2);
        when(phoneAlertService.getPhoneNumberAlerts(stationNumber)).thenReturn(phones);

        // WHEN
        ResponseEntity<List<PhoneAlertDTO>> response =
                phoneAlertController.getAllPhoneAlerts(stationNumber);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2).containsExactly(dto1, dto2);
        verify(phoneAlertService, times(1)).getPhoneNumberAlerts(stationNumber);
    }

    // -----------------------------------------------------------------------
    // Le service retourne null → 404
    // -----------------------------------------------------------------------
    @Test
    void getAllPhoneAlerts_shouldReturn404_whenServiceReturnsNull() {
        // GIVEN
        String stationNumber = "99";
        when(phoneAlertService.getPhoneNumberAlerts(stationNumber)).thenReturn(null);

        // WHEN
        ResponseEntity<List<PhoneAlertDTO>> response =
                phoneAlertController.getAllPhoneAlerts(stationNumber);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(phoneAlertService, times(1)).getPhoneNumberAlerts(stationNumber);
    }

    // -----------------------------------------------------------------------
    // Le service lève une exception → 500
    // -----------------------------------------------------------------------
    @Test
    void getAllPhoneAlerts_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        String stationNumber = "2";
        when(phoneAlertService.getPhoneNumberAlerts(stationNumber))
                .thenThrow(new RuntimeException("Database unavailable"));

        // WHEN
        ResponseEntity<List<PhoneAlertDTO>> response =
                phoneAlertController.getAllPhoneAlerts(stationNumber);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
        verify(phoneAlertService, times(1)).getPhoneNumberAlerts(stationNumber);
    }

    // -----------------------------------------------------------------------
    // Liste vide retournée par le service → 200 avec body vide
    // (comportement actuel : le contrôleur renvoie 200 si la liste n'est pas null)
    // -----------------------------------------------------------------------
    @Test
    void getAllPhoneAlerts_shouldReturn200WithEmptyList_whenServiceReturnsEmptyList() {
        // GIVEN
        String stationNumber = "3";
        when(phoneAlertService.getPhoneNumberAlerts(stationNumber)).thenReturn(List.of());

        // WHEN
        ResponseEntity<List<PhoneAlertDTO>> response =
                phoneAlertController.getAllPhoneAlerts(stationNumber);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isEmpty();
        verify(phoneAlertService, times(1)).getPhoneNumberAlerts(stationNumber);
    }

    // -----------------------------------------------------------------------
    // Vérification que le service n'est jamais appelé avec null
    // Note : Spring @RequestParam empêche null en pratique (400 automatique),
    // mais le contrôleur contient quand même la garde → on la couvre.
    // -----------------------------------------------------------------------
    @Test
    void getAllPhoneAlerts_shouldNotCallService_whenFirestationNumberIsNull() {
        // WHEN
        // On appelle directement la méthode Java en passant null
        // (contourne Spring MVC, cible uniquement la logique du controller)
        ResponseEntity<List<PhoneAlertDTO>> response =
                phoneAlertController.getAllPhoneAlerts(null);

        // THEN — la garde `if (firestation_number == null)` retourne 400
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
        verifyNoInteractions(phoneAlertService);
    }
}