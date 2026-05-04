package com.globe.safetynet.controllers;

import com.globe.safetynet.services.CommunityEmailService;
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

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class CommunityEmailControllerTest {

    @Mock
    private CommunityEmailService communityEmailService;

    @InjectMocks
    private CommunityEmailController communityEmailController;

    // -----------------------------------------------------------------------
    // Cas nominal : city valide avec données → 200
    // -----------------------------------------------------------------------
    @Test
    void getCommunityEmail_shouldReturn200_whenCityHasEmails() {
        // GIVEN
        String city = "culver";
        // Le controller capitalise : "Culver"
        List emails = List.of("jaboyd@email.com", "drk@email.com");
        when(communityEmailService.getCommunityEmails("Culver")).thenReturn(emails);

        // WHEN
        ResponseEntity<?> response = communityEmailController.getCommunityEmail(city);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(emails);
        verify(communityEmailService, times(1)).getCommunityEmails("Culver");
    }

    // -----------------------------------------------------------------------
    // La capitalisation fonctionne quand le nom est déjà en majuscule
    // -----------------------------------------------------------------------
    @Test
    void getCommunityEmail_shouldCapitalizeFirstLetter_andReturn200() {
        // GIVEN — "CULVER" → charAt(0)='C' → "CULVER" (inchangé)
        String city = "CULVER";
        List emails = List.of("jaboyd@email.com");
        when(communityEmailService.getCommunityEmails("CULVER")).thenReturn(emails);

        // WHEN
        ResponseEntity<?> response = communityEmailController.getCommunityEmail(city);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(communityEmailService).getCommunityEmails("CULVER");
    }

    // -----------------------------------------------------------------------
    // Service retourne liste vide → 404
    // -----------------------------------------------------------------------
    @Test
    void getCommunityEmail_shouldReturn404_whenServiceReturnsEmptyList() {
        // GIVEN
        String city = "unknown";
        when(communityEmailService.getCommunityEmails("Unknown")).thenReturn(List.of());

        // WHEN
        ResponseEntity<?> response = communityEmailController.getCommunityEmail(city);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(communityEmailService, times(1)).getCommunityEmails("Unknown");
    }

    // -----------------------------------------------------------------------
    // Service lève une exception → 500
    // -----------------------------------------------------------------------
    @Test
    void getCommunityEmail_shouldReturn500_whenServiceThrowsException() {
        // GIVEN
        String city = "culver";
        when(communityEmailService.getCommunityEmails("Culver"))
                .thenThrow(new RuntimeException("Database error"));

        // WHEN
        ResponseEntity<?> response = communityEmailController.getCommunityEmail(city);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Internal Server Error");
        verify(communityEmailService, times(1)).getCommunityEmails("Culver");
    }

    // -----------------------------------------------------------------------
    // city d'un seul caractère : la capitalisation ne plante pas
    // -----------------------------------------------------------------------
    @Test
    void getCommunityEmail_shouldHandleSingleCharCity() {
        // GIVEN — "a" → capitalize → "A", isEmpty() = false
        String city = "a";
        List emails = List.of("test@email.com");
        when(communityEmailService.getCommunityEmails("A")).thenReturn(emails);

        // WHEN
        ResponseEntity<?> response = communityEmailController.getCommunityEmail(city);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(communityEmailService).getCommunityEmails("A");
    }

}