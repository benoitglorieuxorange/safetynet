package com.globe.safetynet.services;

import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepositoryBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityEmailServiceTest {

    @Mock
    private JsonRepositoryBase jsonRepositoryBase;

    @InjectMocks
    private CommunityEmailService communityEmailService;

    private Person john;
    private Person jane;
    private Person stranger; // ville différente

    @BeforeEach
    void setUp() {
        john     = new Person("John", "Boyd",   "1509 Culver St", "Culver", "97451", "841-874-6512", "jaboyd@email.com");
        jane     = new Person("Jane", "Boyd",   "29 15th St",     "Culver", "97451", "841-874-6513", "jane@email.com");
        stranger = new Person("Tony", "Cooper", "112 Steppes Pl", "Culver", "97451", "841-874-6874", "tcoop@ymail.com");
        // on lui donne une ville différente via setter
        stranger = new Person("Tony", "Cooper", "112 Steppes Pl", "Springfield", "97451", "841-874-6874", "tcoop@ymail.com");
    }

    // -----------------------------------------------------------------------
    // Cas nominal : plusieurs personnes dans la ville → emails retournés
    // -----------------------------------------------------------------------
    @Test
    void getCommunityEmails_shouldReturnEmails_forMatchingCity() {
        // GIVEN
        Data data = new Data(List.of(john, jane, stranger), List.of(), List.of());
        when(jsonRepositoryBase.getData()).thenReturn(data);

        // WHEN
        List<?> result = communityEmailService.getCommunityEmails("Culver");

        // THEN
        assertThat((List<String>) (List<?>) result)
                .hasSize(2)
                .containsExactlyInAnyOrder("jaboyd@email.com", "jane@email.com");
        verify(jsonRepositoryBase, times(1)).getData();
    }

    // -----------------------------------------------------------------------
    // Aucune personne dans la ville → liste vide
    // -----------------------------------------------------------------------
    @Test
    void getCommunityEmails_shouldReturnEmptyList_whenNobodyInCity() {
        // GIVEN
        Data data = new Data(List.of(john, jane), List.of(), List.of());
        when(jsonRepositoryBase.getData()).thenReturn(data);

        // WHEN
        List<?> result = communityEmailService.getCommunityEmails("Paris");

        // THEN
        assertThat(result).isEmpty();
        verify(jsonRepositoryBase, times(1)).getData();
    }

    // -----------------------------------------------------------------------
    // Filtre strict : la casse doit correspondre exactement (equals, pas equalsIgnoreCase)
    // -----------------------------------------------------------------------
    @Test
    void getCommunityEmails_shouldBeCaseSensitive() {
        // GIVEN — ville stockée "Culver", on cherche "culver" (minuscule)
        Data data = new Data(List.of(john, jane), List.of(), List.of());
        when(jsonRepositoryBase.getData()).thenReturn(data);

        // WHEN
        List<?> result = communityEmailService.getCommunityEmails("culver");

        // THEN — equals() est case-sensitive → aucun résultat
        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // Liste de personnes vide → liste vide
    // -----------------------------------------------------------------------
    @Test
    void getCommunityEmails_shouldReturnEmptyList_whenNoPersonsInData() {
        // GIVEN
        Data data = new Data(List.of(), List.of(), List.of());
        when(jsonRepositoryBase.getData()).thenReturn(data);

        // WHEN
        List<?> result = communityEmailService.getCommunityEmails("Culver");

        // THEN
        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // getData() retourne null → IllegalArgumentException via validateData
    // -----------------------------------------------------------------------
    @Test
    void getCommunityEmails_shouldThrowException_whenDataIsNull() {
        // GIVEN
        when(jsonRepositoryBase.getData()).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> communityEmailService.getCommunityEmails("Culver"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Repository return null data");
    }

    // -----------------------------------------------------------------------
    // Seule la personne de la bonne ville est retournée (isolation du filtre)
    // -----------------------------------------------------------------------
    @Test
    void getCommunityEmails_shouldOnlyReturnEmailsFromMatchingCity() {
        // GIVEN
        Data data = new Data(List.of(john, stranger), List.of(), List.of());
        when(jsonRepositoryBase.getData()).thenReturn(data);

        // WHEN
        List<?> result = communityEmailService.getCommunityEmails("Culver");

        // THEN — seul john (Culver) est retourné, pas stranger (Springfield)
        assertThat((List<String>) (List<?>) result)
                .hasSize(1)
                .containsExactly("jaboyd@email.com");
    }
}