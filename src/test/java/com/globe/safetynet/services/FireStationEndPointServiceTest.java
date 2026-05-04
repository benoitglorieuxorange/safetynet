package com.globe.safetynet.services;

import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.repository.FireStationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FireStationEndPointServiceTest {

    @Mock
    private FireStationRepository fireStationRepository;

    @InjectMocks
    private FireStationEndPointService fireStationEndPointService;

    private FireStation fireStation;

    @BeforeEach
    void setUp() {
        fireStation = new FireStation("1509 Culver St", "3");
    }

    // -----------------------------------------------------------------------
    // addFireStation
    // -----------------------------------------------------------------------

    @Test
    void addFireStation_shouldReturnSavedFireStation_whenRepositorySucceeds() {
        // GIVEN
        when(fireStationRepository.add(fireStation)).thenReturn(fireStation);

        // WHEN
        FireStation result = fireStationEndPointService.addFireStation(fireStation);

        // THEN
        assertThat(result).isEqualTo(fireStation);
        verify(fireStationRepository, times(1)).add(fireStation);
    }

    @Test
    void addFireStation_shouldReturnNull_whenRepositoryReturnsNull() {
        // GIVEN
        when(fireStationRepository.add(fireStation)).thenReturn(null);

        // WHEN
        FireStation result = fireStationEndPointService.addFireStation(fireStation);

        // THEN
        assertThat(result).isNull();
        verify(fireStationRepository, times(1)).add(fireStation);
    }

    @Test
    void addFireStation_shouldPropagateException_whenRepositoryThrows() {
        // GIVEN
        when(fireStationRepository.add(fireStation))
                .thenThrow(new IllegalArgumentException("FireStation number cannot be null"));

        // WHEN / THEN
        assertThatThrownBy(() -> fireStationEndPointService.addFireStation(fireStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("FireStation number cannot be null");
        verify(fireStationRepository, times(1)).add(fireStation);
    }

    // -----------------------------------------------------------------------
    // deleteFireStation
    // -----------------------------------------------------------------------

    @Test
    void deleteFireStation_shouldReturnDeletedFireStation_whenFound() {
        // GIVEN
        when(fireStationRepository.delete("1509 Culver St", "3"))
                .thenReturn(Optional.of(fireStation));

        // WHEN
        Optional<FireStation> result = fireStationEndPointService.deleteFireStation(fireStation);

        // THEN
        assertThat(result).isPresent().contains(fireStation);
        verify(fireStationRepository, times(1)).delete("1509 Culver St", "3");
    }

    @Test
    void deleteFireStation_shouldReturnEmptyOptional_whenNotFound() {
        // GIVEN
        when(fireStationRepository.delete("1509 Culver St", "3"))
                .thenReturn(Optional.empty());

        // WHEN
        Optional<FireStation> result = fireStationEndPointService.deleteFireStation(fireStation);

        // THEN
        assertThat(result).isEmpty();
        verify(fireStationRepository, times(1)).delete("1509 Culver St", "3");
    }

    @Test
    void deleteFireStation_shouldUseAddressAndStation_fromEntity() {
        // GIVEN — vérifie que le service transmet bien address et station du record
        FireStation other = new FireStation("29 15th St", "2");
        when(fireStationRepository.delete("29 15th St", "2")).thenReturn(Optional.of(other));

        // WHEN
        Optional<FireStation> result = fireStationEndPointService.deleteFireStation(other);

        // THEN
        assertThat(result).isPresent().contains(other);
        verify(fireStationRepository, times(1)).delete("29 15th St", "2");
        verify(fireStationRepository, never()).delete("1509 Culver St", "3");
    }

    // -----------------------------------------------------------------------
    // updateFireStation
    // -----------------------------------------------------------------------

    @Test
    void updateFireStation_shouldReturnUpdatedFireStation_whenFound() {
        // GIVEN
        when(fireStationRepository.update("1509 Culver St", "3"))
                .thenReturn(Optional.of(fireStation));

        // WHEN
        Optional<FireStation> result = fireStationEndPointService.updateFireStation(fireStation);

        // THEN
        assertThat(result).isPresent().contains(fireStation);
        verify(fireStationRepository, times(1)).update("1509 Culver St", "3");
    }

    @Test
    void updateFireStation_shouldReturnEmptyOptional_whenNotFound() {
        // GIVEN
        when(fireStationRepository.update("1509 Culver St", "3"))
                .thenReturn(Optional.empty());

        // WHEN
        Optional<FireStation> result = fireStationEndPointService.updateFireStation(fireStation);

        // THEN
        assertThat(result).isEmpty();
        verify(fireStationRepository, times(1)).update("1509 Culver St", "3");
    }

    @Test
    void updateFireStation_shouldUseAddressAndStation_fromEntity() {
        // GIVEN — vérifie que le service transmet bien address et station du record
        FireStation other = new FireStation("29 15th St", "2");
        when(fireStationRepository.update("29 15th St", "2")).thenReturn(Optional.of(other));

        // WHEN
        Optional<FireStation> result = fireStationEndPointService.updateFireStation(other);

        // THEN
        assertThat(result).isPresent().contains(other);
        verify(fireStationRepository, times(1)).update("29 15th St", "2");
        verify(fireStationRepository, never()).update("1509 Culver St", "3");
    }
}