package com.globe.safetynet.services;

import com.globe.safetynet.dtos.FireStationResponseDTO;
import com.globe.safetynet.dtos.PersonFireStationDTO;
import com.globe.safetynet.entities.Data;

import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepositoryBase;
import com.globe.safetynet.utils.PersonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.globe.safetynet.utils.PersonUtils.*;

@Service
public class FireStationService {

        private static final Logger logger = LoggerFactory.getLogger(FireStationService.class);

        private final JsonRepositoryBase jsonRepository;
        public FireStationService(JsonRepositoryBase jsonRepository) {
                this.jsonRepository = jsonRepository;
        }

        public FireStationResponseDTO getPersonsByFireStationNumber(String stationNumber) {
                logger.info("Searching Person cover by the station number: {}", stationNumber);

                Data data = jsonRepository.getData();
                validateData(data);

                List<String> addresses = PersonUtils.findAddressesByStationNumber(data, stationNumber);
                        if(addresses.isEmpty()){
                                logger.warn("No address found for station number: {}", stationNumber);
                                return null;
                        }
                List<Person> persons = findPersonByAddress(data, addresses);
                if (persons.isEmpty()) {
                        logger.warn("No person found for stations {}", stationNumber);
                        return null;
                }

                Map<String, MedicalRecord> medicalRecordMap = buildMedicalRecordMap(data);
                List<PersonFireStationDTO> personDTO = buildPersonDTOs(persons);
                AgeCount ageCount = countPersonByAge(persons, medicalRecordMap);
                logger.info("Station {}: {} persons, {} adults, {} childrens",
                        stationNumber, persons.size(), ageCount.adultCount(), ageCount.childCount());
                return new FireStationResponseDTO(personDTO, ageCount.adultCount(), ageCount.childCount());
        }

        private Map<String, MedicalRecord> buildMedicalRecordMap(Data data) {
                return data.getMedicalRecords().stream()
                        .collect(Collectors.toMap(
                                PersonUtils::buildFullName,
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
                        }else {
                                childCount++;
                        }
                }
                return new AgeCount(adultCount, childCount);
        }
} //EoC
