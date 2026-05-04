package com.globe.safetynet.repository;

import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.FireStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FireStationRepositoryTest {

    @Mock
    private JsonRepositoryBase jsonRepository;

    @Mock
    private Data data;

    @InjectMocks
    private FireStationRepository fireStationRepository;

    private List<FireStation> fireStations;

    @BeforeEach
    void setUp() {
        fireStations = new ArrayList<>();
        lenient().when(jsonRepository.getData()).thenReturn(data);
        lenient().when(data.getFireStations()).thenReturn(fireStations);
    }

// Add

    @Test
    void add_ShouldReturnFireStation_WhenFireStationIsValid() {
        FireStation fireStation = new FireStation();
        fireStation.setStation("1");
        fireStation.setAddress("1 rue de la street");

        FireStation result = fireStationRepository.add(fireStation);

        assertEquals(fireStation, result);
        assertEquals(1, fireStations.size());
        assertTrue(fireStations.contains(fireStation));
        verify(jsonRepository).saveData();
    }

    @Test
    void add_ShouldThrow_WhenFireStationIsNull() {
        assertThatThrownBy(() -> fireStationRepository.add(null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(jsonRepository, never()).saveData();
    }

// Update

    @Test
    void update_ShouldUpdateStation_WhenAddressFound() {
        FireStation fs = new FireStation();
        fs.setAddress("1 rue de la street");
        fs.setStation("1");
        fireStations.add(fs);

        Optional<FireStation> result = fireStationRepository.update("1 rue de la street", "2");

        assertThat(result).isPresent();
        assertEquals("2", result.get().getStation());
        verify(jsonRepository).saveData();
    }

    @Test
    void update_ShouldBeCaseInsensitive_OnAddress() {
        FireStation fs = new FireStation();
        fs.setAddress("1 rue de la street");
        fs.setStation("1");
        fireStations.add(fs);

        Optional<FireStation> result = fireStationRepository.update("1 RUE DE LA STREET", "3");

        assertThat(result).isPresent();
        assertEquals("3", result.get().getStation());
    }

    @Test
    void update_ShouldReturnEmpty_WhenAddressNotFound() {
        Optional<FireStation> result = fireStationRepository.update("adresse inconnue", "2");

        assertThat(result).isEmpty();
        verify(jsonRepository, never()).saveData();
    }

    // Delete

    @Test
    void delete_ShouldRemoveFireStation_WhenMatchFound() {
        FireStation fs = new FireStation();
        fs.setAddress("1 rue de la street");
        fs.setStation("1");
        fireStations.add(fs);

        Optional<FireStation> result = fireStationRepository.delete("1 rue de la street", "1");

        assertThat(result).isPresent();
        assertTrue(fireStations.isEmpty());
        verify(jsonRepository).saveData();
    }

    @Test
    void delete_ShouldReturnEmpty_WhenAddressNotFound() {
        Optional<FireStation> result = fireStationRepository.delete("adresse inconnue", "1");

        assertThat(result).isEmpty();
        verify(jsonRepository, never()).saveData();
    }

    @Test
    void delete_ShouldReturnEmpty_WhenStationNotMatch() {
        FireStation fs = new FireStation();
        fs.setAddress("1 rue de la street");
        fs.setStation("1");
        fireStations.add(fs);

        Optional<FireStation> result = fireStationRepository.delete("1 rue de la street", "99");

        assertThat(result).isEmpty();
        assertEquals(1, fireStations.size());
        verify(jsonRepository, never()).saveData();
    }
}