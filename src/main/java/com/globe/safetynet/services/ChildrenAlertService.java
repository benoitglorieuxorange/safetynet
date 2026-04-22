package com.globe.safetynet.services;

import com.globe.safetynet.dtos.ChildAlertDTO;
import com.globe.safetynet.dtos.HouseholdMemberDTO;
import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepositoryBase;
import com.globe.safetynet.utils.PersonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChildrenAlertService {

    private static final Logger logger = LoggerFactory.getLogger(ChildrenAlertService.class);

    private final JsonRepositoryBase jsonRepository;
    public ChildrenAlertService(JsonRepositoryBase jsonRepository) {
        this.jsonRepository = jsonRepository;
    }

    public List<ChildAlertDTO> getChildrenByAddress(String address) {
        logger.info("Searching children by address: {}",  address);

        Data data = jsonRepository.getData();
        PersonUtils.validateData(data);

        List<String> addresses = new ArrayList<>();
        addresses.add(address);

        List<Person> residents = PersonUtils.findPersonByAddress(data, addresses);
        if (residents.isEmpty()) {
            logger.warn("No residents found for address {}", addresses);
            return Collections.emptyList();
        }

        Map<String, MedicalRecord> medicalRecordMap = buildMedicalRecordMap(data);

        List<Person> children = residents.stream()
                .filter(person -> isChild(person, medicalRecordMap))
                .toList();

        if (children.isEmpty()) {
            logger.warn("No children found for address {}", address);
            return Collections.emptyList();
        }

        List<HouseholdMemberDTO> otherMembers = residents.stream()
                .filter(person -> !isChild(person, medicalRecordMap))
                .map(person -> new HouseholdMemberDTO(person.getFirstName(), person.getLastName()))
                .toList();

        List<ChildAlertDTO> result = children.stream()
                .map(child -> {
                    MedicalRecord medicalRecord = medicalRecordMap.get(PersonUtils.buildFullName(child));
                    int age = PersonUtils.calculateAge(medicalRecord.getBirthdate());
                    return new ChildAlertDTO(child.getFirstName(), child.getLastName(), age, otherMembers);
                })
                .toList();
        logger.info("Found {} children for address {}", result.size(), address);
        return result;
    }

    private boolean isChild(Person person, Map<String, MedicalRecord> medicalRecordMap) {
        MedicalRecord medicalRecord = medicalRecordMap.get(PersonUtils.buildFullName(person));
        if (medicalRecord == null) {
            logger.warn("No medical record found for person {} {}", person.getFirstName(), person.getLastName());
            return false;
        }
        return PersonUtils.calculateAge(medicalRecord.getBirthdate()) <= 18;
    }

    private Map<String, MedicalRecord> buildMedicalRecordMap(Data data) {
        return data.getMedicalRecords().stream()
                .collect(Collectors.toMap(
                        PersonUtils::buildFullName,
                        medicalRecord -> medicalRecord,
                        (existing, replacement) -> { throw new IllegalStateException("Doublon : " + existing); }
                ));
    }
}
