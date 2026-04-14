package com.globe.safetynet.services;

import com.globe.safetynet.dtos.FireStationResponseDTO;
import com.globe.safetynet.dtos.PersonFireStationDTO;
import com.globe.safetynet.entities.Data;

import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FireStationService {

        private static final Logger logger = LoggerFactory.getLogger(FireStationService.class);
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        private final JsonRepository jsonRepository;

        public FireStationService(JsonRepository jsonRepository) {
                this.jsonRepository = jsonRepository;
        }

        public FireStationResponseDTO getPersonsByFireStationNumber(String stationNumber) {
                logger.info("Searching Person cover by the station number: {}", stationNumber);

                Data data = jsonRepository.getData();
                validateData(data);

                List<String> addresses = findAddressesByStationNumber(data, stationNumber);
                        if(addresses.isEmpty()){
                                logger.warn("No address found for station number: {}", stationNumber);
                                return null;
                        }


                List<Person> persons = findPersonsByAddresses(data, addresses);
                if (persons.isEmpty()) {
                        logger.warn("No person found for stations {}", stationNumber);
                        return null;
                }

                Map<String, MedicalRecord> medicalRecordMap = buildMedicalRecordMap(data);
                List<PersonFireStationDTO> personDTO = buildPersonDTOs(persons);
                AgeCount ageCount = countPersonByAge(persons, medicalRecordMap);
                logger.info("Station {}: {} persons, {} adults, {} childrens",
                        stationNumber, persons.size(), ageCount.adultCount(), ageCount.childCount());
                return new FireStationResponseDTO(personDTO, ageCount.adultCount, ageCount.childCount());
        }

        private Map<String, MedicalRecord> buildMedicalRecordMap(Data data) {
                return data.getMedicalRecords().stream()
                        .collect(Collectors.toMap(
                                this::buildFullName,
                                medicalRecord -> medicalRecord
                        ));
        }

        private List<PersonFireStationDTO> buildPersonDTOs(List<Person> persons) {
                return persons.stream()
                        .map(person -> new PersonFireStationDTO(
                                person.getFirstName(),
                                person.getLastName(),
                                person.getAddress(),
                                person.getPhone()
                        ))
                        .toList();
        }

        private AgeCount countPersonByAge(List<Person> persons, Map<String, MedicalRecord> medicalRecordMap) {
                int adultCount = 0;
                int childCount = 0;
                for (Person person : persons) {
                        MedicalRecord medicalRecord = medicalRecordMap.get(buildFullName(person));
                        if (medicalRecord == null) {
                                logger.warn("Missing medical record", person.getFirstName(),  person.getLastName());
                        }
                        int age = calculateAge(medicalRecord.getBirthdate());
                        if (age >= 18){
                                adultCount++;
                        }else if (age < 18){
                                childCount++;
                        }
                }
                return new AgeCount(adultCount, childCount);
        }

        private String buildFullName(Person person) {
                return person.getFirstName() + " " + person.getLastName().toUpperCase();
        }

        private String buildFullName(MedicalRecord medicalRecord) {
                return medicalRecord.getFirstName() + " " + medicalRecord.getLastName().toUpperCase();
        }

        private int calculateAge(String birthdate) {
                LocalDate birthDate = LocalDate.parse(birthdate, DATE_FORMATTER);
                return Period.between(birthDate, LocalDate.now()).getYears();
        }

        private List<String> findAddressesByStationNumber(Data data, String stationNumber) {
                return data.getFireStations().stream()
                        .filter(fs -> stationNumber.equals(fs.getStation()))
                        .map(FireStation::getAddress)
                        .toList();
        }

        private void validateData(Data data) {
                if (data == null) {
                        logger.error("Repository return null data");
                        throw new IllegalArgumentException("Repository return null data");
                }
        }

        private List<Person> findPersonsByAddresses(Data data, List<String> addresses) {
                return data.getPersons().stream()
                        .filter(person -> addresses.contains(person.getAddress()))
                        .toList();
        }

        private record AgeCount(int adultCount, int childCount) {
        }
} //EoC
