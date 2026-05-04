package com.globe.safetynet.services;


import com.globe.safetynet.dtos.MedicalRecordDTO;
import com.globe.safetynet.dtos.PersonInfoDTO;
import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepositoryBase;
import com.globe.safetynet.utils.PersonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class PersonInfoService {
    private static final Logger logger = LoggerFactory.getLogger(PersonInfoService.class);

    private JsonRepositoryBase jsonRepositoryBase;
    private final PersonMappingService  personMappingService;

    public PersonInfoService(JsonRepositoryBase jsonRepositoryBase, PersonMappingService personMappingService) {
        this.jsonRepositoryBase = jsonRepositoryBase;
        this.personMappingService = personMappingService;
    }

    public List<PersonInfoDTO> getPersonInfoByLastname(String LastName) {
        logger.info(" Getting Person Info by LastName {}", LastName);

        Data data = jsonRepositoryBase.getData();
        PersonUtils.validateData(data);

        List<Person> persons = PersonUtils.findPersonByLastName(data, LastName);

        Map<String, MedicalRecordDTO> medicalRecordMap = personMappingService.buildMedicalRecordMap(data, persons);


        return personMappingService.extractPersonMedicalData(persons, medicalRecordMap)
                .stream()
                .map(d -> new PersonInfoDTO(
                        d.firstName(),
                        d.lastName(),
                        d.address(),
                        d.email(),
                        d.age(),
                        d.medications(),
                        d.allergies()))
                .toList();
    }

}//EofC