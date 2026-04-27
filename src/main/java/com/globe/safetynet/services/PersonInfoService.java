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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PersonInfoService {
    private static final Logger logger = LoggerFactory.getLogger(PersonInfoService.class);

    private JsonRepositoryBase jsonRepositoryBase;

    public PersonInfoService(JsonRepositoryBase jsonRepositoryBase) {
        this.jsonRepositoryBase = jsonRepositoryBase;
    }

    public List<PersonInfoDTO> getPersonInfoByLastname(String LastName) {
        logger.info(" Getting Person Info by LastName {}", LastName);

        Data data = jsonRepositoryBase.getData();
        PersonUtils.validateData(data);

        List<Person> persons = PersonUtils.findPersonByLastName(data, LastName);

        Map<String, MedicalRecordDTO> medicalRecordMap = buildMedicalRecordMap(data, persons);

        List<PersonInfoDTO> personInfoDTOs = persons.stream()
                .map(person -> {
                    String fullName = PersonUtils.buildFullName(person);
                    MedicalRecordDTO dto = medicalRecordMap.get(fullName);
                    return new PersonInfoDTO(person.getFirstName(),person.getLastName(),person.getAddress(),person.getEmail(),dto.age(),dto.medicalRecord().getMedications(),dto.medicalRecord().getAllergies());
                })
                .toList();
        return personInfoDTOs;

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
}