package com.globe.safetynet.integration;

import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonRepositoryIntegrationTest {

    @Test
    void deletePerson_ShouldPersistDeletionInTestJsonFile() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // 1. Charger le fichier modèle depuis src/test/resources via le classpath
        ClassPathResource resource = new ClassPathResource("data.json");
        assertTrue(resource.exists(), "Le fichier data.json doit exister dans src/test/resources");

        // 2. Copier ce fichier dans un fichier temporaire
        File tempFile = File.createTempFile("data-test-", ".json");
        tempFile.deleteOnExit();

        try (InputStream inputStream = resource.getInputStream()) {
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        // 3. Lire les données depuis le fichier temporaire
        Data testData = mapper.readValue(tempFile, Data.class);

        assertNotNull(testData);
        assertNotNull(testData.getPersons());

        boolean personExistsBefore = testData.getPersons().stream()
                .anyMatch(person ->
                        person.getFirstName().equalsIgnoreCase("Benoit") &&
                                person.getLastName().equalsIgnoreCase("GLORIEUX"));

        assertTrue(personExistsBefore, "La personne Benoit GLORIEUX doit exister avant la suppression");

        // 4. Préparer le repository
        JsonRepository repository = new JsonRepository();
        ReflectionTestUtils.setField(repository, "data", testData);
        ReflectionTestUtils.setField(repository, "dataFilePath", tempFile.getAbsolutePath());

        // Act
        Optional<Person> result = repository.deletePerson("benoit", "glorieux");

        // Assert
        assertTrue(result.isPresent(), "La personne doit être trouvée et supprimée");

        Data reloadedData = mapper.readValue(tempFile, Data.class);

        boolean personExistsAfter = reloadedData.getPersons().stream()
                .anyMatch(person ->
                        person.getFirstName().equalsIgnoreCase("Benoit") &&
                                person.getLastName().equalsIgnoreCase("GLORIEUX"));

        assertFalse(personExistsAfter, "La personne ne doit plus être présente dans le fichier après suppression");
    }
}
