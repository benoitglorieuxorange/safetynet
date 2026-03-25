package com.globe.safetynet.services;

import com.globe.safetynet.dtos.FireStationResponseDTO;
import com.globe.safetynet.dtos.PersonFireStationDTO;
import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FireStationService {

    private final JsonRepository jsonRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public FireStationService(JsonRepository jsonRepository) {
        this.jsonRepository = jsonRepository;
    }

    public FireStationResponseDTO getPersonsByFireStationNumber(String stationNumber) {

        Data data = jsonRepository.getData();

        // Récupérer les adresses couvertes par cette station
        List<String> addresses = data.getFireStations()
                .stream()
                .filter(fs -> stationNumber.equals(fs.getStation()))
                .map(FireStation::getAddress)
                .toList();

        // Créer une map des dossiers médicaux par nom complet
        Map<String, MedicalRecord> medicalRecordMap = data.getMedicalRecords()
                .stream()
                .collect(Collectors.toMap(
                        mr -> mr.getFirstName() + " " + mr.getLastName(),
                        mr -> mr
                ));

        // Récupérer les personnes
        List<Person> personsAtStation = data.getPersons()
                .stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .toList();

        // Convertir en DTO
        List<PersonFireStationDTO> personDTOs = personsAtStation.stream()
                .map(person -> new PersonFireStationDTO(
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getPhone()
                ))
                .toList();

        // Compter adultes et enfants
        int adultCount = 0;
        int childCount = 0;

        for (Person person : personsAtStation) {
            String fullName = person.getFirstName() + " " + person.getLastName();
            MedicalRecord record = medicalRecordMap.get(fullName);

            if (record != null) {
                int age = calculateAge(record.getBirthdate());
                if (age > 18) {
                    adultCount++;
                } else {
                    childCount++;
                }
            }
        }

        return new FireStationResponseDTO(personDTOs, adultCount, childCount);
    }

    /**
     * Calcule l'âge à partir d'une date de naissance
     * @param birthdate Date au format MM/dd/yyyy
     * @return Âge en années
     */
    private int calculateAge(String birthdate) {
        LocalDate birthDate = LocalDate.parse(birthdate, DATE_FORMATTER);
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
