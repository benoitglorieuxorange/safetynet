package com.globe.safetynet.services;

import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepositoryBase;
import com.globe.safetynet.utils.PersonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityEmailService {

    private static final Logger logger = LoggerFactory.getLogger(CommunityEmailService.class);
    private JsonRepositoryBase jsonRepositoryBase;

    public CommunityEmailService(JsonRepositoryBase jsonRepositoryBase) {
        this.jsonRepositoryBase = jsonRepositoryBase;
    }

    public List<?> getCommunityEmails(String city) {
        logger.info("Getting community emails");

        Data data = jsonRepositoryBase.getData();
        PersonUtils.validateData(data);

        List<Person> persons = data.getPersons();
        return persons.stream()
                .filter(person -> person.getCity().equals(city))
                .map(p->p.getEmail())
                .toList();
    }

}//EofC
