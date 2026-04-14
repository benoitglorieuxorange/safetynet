package com.globe.safetynet.services;

import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.repository.JsonRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FireStationEndPointService {

    private JsonRepository jsonRepository;

    public FireStationEndPointService(JsonRepository jsonRepository) {
        this.jsonRepository = jsonRepository;
    }

    public FireStation addFireStation(FireStation fireStation) {
        return jsonRepository.addFireStation(fireStation);
    }

    public Optional<FireStation> deleteFireStation(@NonNull FireStation fireStation) {
        return jsonRepository.deleteFireStation(fireStation.getAddress(), fireStation.getStation());
    }


}
