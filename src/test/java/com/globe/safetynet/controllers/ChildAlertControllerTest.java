package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.ChildAlertDTO;
import com.globe.safetynet.dtos.HouseholdMemberDTO;
import com.globe.safetynet.services.ChildrenAlertService;
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
class ChildAlertControllerTest {

    @Mock
    private ChildrenAlertService childrenAlertService;

    @InjectMocks
    private ChildAlertController childAlertController;

    private ChildAlertDTO child1;
    private ChildAlertDTO child2;

    @BeforeEach
    void setUp() {
        HouseholdMemberDTO member1 = new HouseholdMemberDTO("John", "Boyd");
        HouseholdMemberDTO member2 = new HouseholdMemberDTO("Jane", "Boyd");
        child1 = new ChildAlertDTO("Tom", "Boyd", 10, List.of(member1, member2));
        child2 = new ChildAlertDTO("Lily", "Boyd", 8, List.of(member1, member2));
    }

    // -----------------------------------------------------------------------
    // Cas nominal : adresse valide avec enfants → 200
    // -----------------------------------------------------------------------
    @Test
    void getChildAlert_shouldReturn200_whenAddressHasChildren() {
        // GIVEN
        String address = "1509 Culver St";
        when(childrenAlertService.getChildrenByAddress(address)).thenReturn(List.of(child1, child2));

        // WHEN
        ResponseEntity<?> response = childAlertController.getChildAlert(address);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(List.of(child1, child2));
        verify(childrenAlertService, times(1)).getChildrenByAddress(address);
    }

    // -----------------------------------------------------------------------
    // Les guillemets dans l'adresse sont supprimés avant l'appel au service
    // -----------------------------------------------------------------------
    @Test
    void getChildAlert_shouldStripQuotes_andReturn200() {
        // GIVEN — les guillemets sont retirés : "\"1509 Culver St\"" → "1509 Culver St"
        String addressWithQuotes = "\"1509 Culver St\"";
        String addressClean = "1509 Culver St";
        when(childrenAlertService.getChildrenByAddress(addressClean)).thenReturn(List.of(child1));

        // WHEN
        ResponseEntity<?> response = childAlertController.getChildAlert(addressWithQuotes);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(childrenAlertService, times(1)).getChildrenByAddress(addressClean);
    }

    // -----------------------------------------------------------------------
    // Service retourne liste vide (non null) → 200 avec body vide
    // -----------------------------------------------------------------------
    @Test
    void getChildAlert_shouldReturn200WithEmptyList_whenServiceReturnsEmptyList() {
        // GIVEN
        String address = "1509 Culver St";
        when(childrenAlertService.getChildrenByAddress(address)).thenReturn(List.of());

        // WHEN
        ResponseEntity<?> response = childAlertController.getChildAlert(address);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(List.of());
        verify(childrenAlertService, times(1)).getChildrenByAddress(address);
    }

    // -----------------------------------------------------------------------
    // Service retourne null → 404
    // -----------------------------------------------------------------------
    @Test
    void getChildAlert_shouldReturn404_whenServiceReturnsNull() {
        // GIVEN
        String address = "unknown address";
        when(childrenAlertService.getChildrenByAddress(address)).thenReturn(null);

        // WHEN
        ResponseEntity<?> response = childAlertController.getChildAlert(address);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(childrenAlertService, times(1)).getChildrenByAddress(address);
    }

    // -----------------------------------------------------------------------
    // Service lève une exception → 500
    // -----------------------------------------------------------------------
    @Test
    void getChildAlert_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        String address = "1509 Culver St";
        when(childrenAlertService.getChildrenByAddress(address))
                .thenThrow(new RuntimeException("Database error"));

        // WHEN
        ResponseEntity<?> response = childAlertController.getChildAlert(address);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Internal Server Error");
        verify(childrenAlertService, times(1)).getChildrenByAddress(address);
    }

    // -----------------------------------------------------------------------
    // Adresse vide après suppression des guillemets → 400
    // -----------------------------------------------------------------------
    @Test
    void getChildAlert_shouldReturn400_whenAddressIsEmpty() {
        // GIVEN — chaîne vide → replace ne change rien → isEmpty() = true → 400
        String address = "";

        // WHEN
        ResponseEntity<?> response = childAlertController.getChildAlert(address);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(childrenAlertService);
    }

    // -----------------------------------------------------------------------
    // Adresse contenant uniquement des guillemets → vide après replace → 400
    // -----------------------------------------------------------------------
    @Test
    void getChildAlert_shouldReturn400_whenAddressContainsOnlyQuotes() {
        // GIVEN — "\"\"" → replace → "" → isEmpty() = true → 400
        String address = "\"\"";

        // WHEN
        ResponseEntity<?> response = childAlertController.getChildAlert(address);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(childrenAlertService);
    }
}