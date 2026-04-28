package com.globe.safetynet.services;

import com.globe.safetynet.dtos.MedicalRecordDTO;
import com.globe.safetynet.dtos.PersonMedicalData;
import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.utils.PersonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PersonMappingService {

    private static final Logger logger = LoggerFactory.getLogger(PersonMappingService.class);

    public Map<String, MedicalRecordDTO> buildMedicalRecordMap(Data data, List<Person> persons) {
        logger.debug("Building medical record map for {} persons", persons.size());

        Set<String> personFullNames = persons.stream()
                .map(PersonUtils::buildFullName)
                .collect(Collectors.toSet());

        Map<String, MedicalRecordDTO> result = data.getMedicalRecords().stream()
                .filter(mr -> personFullNames.contains(PersonUtils.buildFullName(mr)))
                .collect(Collectors.toMap(
                        PersonUtils::buildFullName,
                        mr -> new MedicalRecordDTO(
                                mr,
                                PersonUtils.calculateAge(mr.getBirthdate())
                        )
                ));

        logger.debug("Medical record map built with {} entries", result.size());
        return result;
    }

    public List<PersonMedicalData> extractPersonMedicalData(
            List<Person> persons,
            Map<String, MedicalRecordDTO> medicalRecordMap) {

        logger.debug("Extracting medical data for {} persons", persons.size());

        List<PersonMedicalData> result = persons.stream()
                .map(person -> {
                    String fullName = PersonUtils.buildFullName(person);
                    MedicalRecordDTO dto = medicalRecordMap.get(fullName);

                    if (dto == null) {
                        logger.warn("No medical record found for person: {}", fullName);
                        return null;
                    }

                    return new PersonMedicalData(
                            person.getFirstName(),
                            person.getLastName(),
                            person.getAddress(),
                            person.getEmail(),
                            dto.age(),
                            dto.medicalRecord().getMedications(),
                            dto.medicalRecord().getAllergies()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        logger.debug("Extracted {} PersonMedicalData", result.size());
        return result;
    }
}