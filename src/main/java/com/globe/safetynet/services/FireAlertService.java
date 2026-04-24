package com.globe.safetynet.services;

import com.globe.safetynet.dtos.*;
import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepositoryBase;
import com.globe.safetynet.utils.PersonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FireAlertService {

    private static final Logger logger = LoggerFactory.getLogger(FireAlertService.class);
    private JsonRepositoryBase repository;
    public FireAlertService(JsonRepositoryBase repository) {
        this.repository = repository;
    }

    public FireAlertDTO getPersonByAddress(String address) {
        logger.info("Searching Person cover by adress " + address);

        Data data = repository.getData();
        PersonUtils.validateData(data);

        List<Person> persons = PersonUtils.findPersonByAddress(data, address);
        if (persons.isEmpty()){
            logger.warn("No Person found with address " + address);
            return null;
        }


        String fireStation = findStationNumberByAddress(data, address);

        Map<String, MedicalRecordDTO> medicalRecordMap = buildMedicalRecordMap(data, persons);

        List<PersonFireAlertDTO> personFireAlertDTOs = persons.stream()
                .map(person -> {
                    String fullName = PersonUtils.buildFullName(person);
                    MedicalRecordDTO dto = medicalRecordMap.get(fullName);
                    return new PersonFireAlertDTO(
                            person.getFirstName(),
                            person.getLastName(),
                            dto.age(),
                            dto.medicalRecord().getMedications(),
                            dto.medicalRecord().getAllergies()
                    );
                })
                .toList();
        return new FireAlertDTO(personFireAlertDTOs, fireStation);
    }

    private Map<String, MedicalRecordDTO> buildMedicalRecordMap(Data data, List<Person> persons) {
        Set<String> personFullNames = persons.stream()
                .map(PersonUtils::buildFullName)
                .collect(Collectors.toSet());

        return data.getMedicalRecords().stream()
                .filter(mr -> personFullNames.contains(PersonUtils.buildFullName(mr)))
                .collect(Collectors.toMap(
                        PersonUtils::buildFullName,
                        mr -> new MedicalRecordDTO(
                                mr,
                                PersonUtils.calculateAge(mr.getBirthdate())
                        )
                ));
    }



    public static String findStationNumberByAddress(Data data, String address) {
        return data.getFireStations().stream()
                .filter(fireStation -> address.equals(fireStation.getAddress()))
                .map(FireStation::getStation)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No station found for address: " + address));
    }




}//EofC
