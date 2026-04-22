package com.globe.safetynet.services;

import com.globe.safetynet.dtos.PhoneAlertDTO;
import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepositoryBase;
import com.globe.safetynet.utils.PersonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneAlertService {

    private static final Logger logger = LoggerFactory.getLogger(PhoneAlertService.class);
    private JsonRepositoryBase jsonRepositoryBase;

    public PhoneAlertService(JsonRepositoryBase jsonRepositoryBase) {
        this.jsonRepositoryBase = jsonRepositoryBase;
    }

    public List<PhoneAlertDTO> getPhoneNumberAlerts(String station){

        logger.info("Searching phoneNumber list cover by FireStation number : {}", station);
        Data data = jsonRepositoryBase.getData();
        PersonUtils.validateData(data);

        List<String> addresses = PersonUtils.findAddressesByStationNumber(data, station);
            if(addresses.isEmpty()){
                logger.warn("No phone number list found for station {}", station);
                return null;
            }
        List<Person> persons = PersonUtils.findPersonByAddress(data, addresses);
            if(persons.isEmpty()){
                logger.warn("No phone number list found for station {}", station);
                return null;
            }
        List<PhoneAlertDTO> phoneDTO = buildPhoneAlertDTO(persons);
            return phoneDTO;

    }

    private List<PhoneAlertDTO> buildPhoneAlertDTO(List<Person> persons) {
        return persons.stream()
                .map(person -> new PhoneAlertDTO(
                        person.getPhone()
                ))
                .toList();
    }

}
