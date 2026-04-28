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


@Service
public class FireAlertService {

    private static final Logger logger = LoggerFactory.getLogger(FireAlertService.class);
    private JsonRepositoryBase repository;
    private PersonMappingService personMappingService;

    public FireAlertService(JsonRepositoryBase repository, PersonMappingService personMappingService) {
        this.repository = repository;
        this.personMappingService = personMappingService;
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

        Map<String, MedicalRecordDTO> medicalRecordMap = personMappingService.buildMedicalRecordMap(data, persons);

        List<PersonFireAlertDTO> personFireAlertDTOs =
                personMappingService.extractPersonMedicalData(persons, medicalRecordMap)
                        .stream()
                        .map(d -> new PersonFireAlertDTO(
                                d.firstName(),
                                d.lastName(),
                                d.age(),
                                d.medications(),
                                d.allergies()))
                        .toList();

        return new FireAlertDTO(personFireAlertDTOs, fireStation);
    }

    public static String findStationNumberByAddress(Data data, String address) {
        return data.getFireStations().stream()
                .filter(fireStation -> address.equals(fireStation.getAddress()))
                .map(FireStation::getStation)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No station found for address: " + address));
    }




}//EofC
