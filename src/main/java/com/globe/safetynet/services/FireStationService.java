package com.globe.safetynet.services;

import com.globe.safetynet.entities.Firestation;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.repository.JsonRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FireStationService {

    private final JsonRepository jsonRepository;

    public FireStationService(JsonRepository jsonRepository) {
        this.jsonRepository = jsonRepository;
    }

    public List<Person>  getPersonByeFireStationNumber(String stationNumber) {

        List<String> addresses = jsonRepository.getData()
                .getFirestations()
                .stream()
                .filter(fs -> fs.getStations().equals(stationNumber))
                .map(Firestation::getAddress)
                .toList();

        return jsonRepository.getData()
                .getPersons()
                .stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .toList();

    }
}
