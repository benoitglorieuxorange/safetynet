package com.globe.safetynet.services;

import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.repository.FireStationRepository;
import com.globe.safetynet.repository.JsonRepositoryBase;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FireStationEndPointService {

    private FireStationRepository fireStationRepository;

    public FireStationEndPointService(FireStationRepository fireStationRepository) {
        this.fireStationRepository = fireStationRepository;
    }


    public FireStation addFireStation(FireStation fireStation) {
        return fireStationRepository.add(fireStation);
    }

    public Optional<FireStation> deleteFireStation(@NonNull FireStation fireStation) {
        return fireStationRepository.delete(fireStation.getAddress(), fireStation.getStation());
    }

    public Optional<FireStation> updateFireStation(@NonNull FireStation fireStation) {
        return fireStationRepository.update(fireStation.getAddress(), fireStation.getStation());
    }


}
