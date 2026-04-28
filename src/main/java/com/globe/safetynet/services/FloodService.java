package com.globe.safetynet.services;

import com.globe.safetynet.dtos.MedicalRecordDTO;
import com.globe.safetynet.dtos.PersonFloodDTO;
import com.globe.safetynet.dtos.PersonInfoDTO;
import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepositoryBase;
import com.globe.safetynet.utils.PersonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class FloodService {

    private final static Logger logger =  LoggerFactory.getLogger(FloodService.class);

    private JsonRepositoryBase jsonRepositoryBase;
    private PersonMappingService personMappingService;

    public FloodService(JsonRepositoryBase jsonRepositoryBase,  PersonMappingService personMappingService) {
        this.jsonRepositoryBase = jsonRepositoryBase;
        this.personMappingService = personMappingService;
    }

    public List<PersonFloodDTO> getPersonByFireStationList(List<String> numberList) {
        Data data = jsonRepositoryBase.getData();
        PersonUtils.validateData(data);

        return numberList.stream()
                .flatMap(stationNumber -> getPersonsForStation(data, stationNumber))
                .sorted(Comparator.comparing(PersonFloodDTO::address))
                .toList();
    }

    private Stream<PersonFloodDTO> getPersonsForStation(Data data, String stationNumber) {
        List<String> addresses = PersonUtils.findAddressesByStationNumber(data, stationNumber);
        List<Person> persons = PersonUtils.findPersonByAddress(data, addresses);
        Map<String, MedicalRecordDTO> medicalRecordMap = personMappingService.buildMedicalRecordMap(data, persons);

        return personMappingService.extractPersonMedicalData(persons, medicalRecordMap)
                .stream()
                .map(d -> new PersonFloodDTO(
                        d.firstName(),
                        d.lastName(),
                        d.address(),
                        d.phone(),
                        d.age(),
                        d.medications(),
                        d.allergies()));
    }

}//EofC
