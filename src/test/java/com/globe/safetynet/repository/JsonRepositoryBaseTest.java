package com.globe.safetynet.repository;

import com.globe.safetynet.entities.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class JsonRepositoryBaseTest {

    private JsonRepositoryBase jsonRepositoryBase;

    @BeforeEach
    void setUp() {
        jsonRepositoryBase = new JsonRepositoryBase();
    }

    // Load Data

    @Test
    void loadData_ShouldLoadDataSuccessfully() {

        jsonRepositoryBase.loadData();

        Data result = jsonRepositoryBase.getData();

        assertNotNull(result);
        assertNotNull(result.getPersons());
        assertEquals(result.getPersons().size(), 2);
        assertNotNull(result.getFireStations());
        assertEquals(result.getFireStations().size(), 3);
        assertNotNull(result.getMedicalRecords());
        assertEquals(result.getMedicalRecords().size(), 4);
    }

    @Test
    void loadData_ShouldLoadPersonDataSuccessfully() {
        jsonRepositoryBase.loadData();

        assertNotNull(jsonRepositoryBase.getData().getPersons());

    }

    @Test
    void loadData_ShouldLoadFireStationDataSuccessfully() {
        jsonRepositoryBase.loadData();
        assertNotNull(jsonRepositoryBase.getData().getFireStations());
    }

    @Test
    void loadData_ShouldLoadMedicalRecordDataSuccessfully() {
        jsonRepositoryBase.loadData();
        assertNotNull(jsonRepositoryBase.getData().getMedicalRecords());
    }

    //GetData

    @Test
    void getData() {
        jsonRepositoryBase.loadData();

        assertNotNull(jsonRepositoryBase.getData());

    }

    @Test
    void getData_shouldThrowIllegalStateException_whenDataIsNull() {
        // loadData() jamais appelé → data reste null
        assertThrows(IllegalStateException.class, () -> jsonRepositoryBase.getData());
    }


    @Test
    void getData_shouldThrowIllegalStateException_whenDataIsEmpty() {

        assertThrows(IllegalStateException.class, () -> jsonRepositoryBase.getData());

    }

    // Save Data

    @Test
    void saveData_shouldSaveWithoutException_whenDataIsLoaded() {
        // loadData() doit être appelé avant saveData()
        jsonRepositoryBase.loadData();

        assertDoesNotThrow(() -> jsonRepositoryBase.saveData());
    }

    @Test
    void saveData_shouldWriteFileOnDisk_whenDataIsLoaded() {
        jsonRepositoryBase.loadData();
        jsonRepositoryBase.saveData();

        File file = new File("src/main/resources/data.json");
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }



    @Test
    void saveData_shouldWriteFile() {
        jsonRepositoryBase.loadData();
        jsonRepositoryBase.saveData();

        File file = new File("src/test/resources/datasave.json");
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

}