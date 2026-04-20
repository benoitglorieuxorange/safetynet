package com.globe.safetynet.repository;

import com.globe.safetynet.entities.FireStation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FireStationRepository {

    private final JsonRepositoryBase jsonRepository;

    public FireStationRepository(JsonRepositoryBase jsonRepository) {
        this.jsonRepository = jsonRepository;
    }

    private List<FireStation> getFireStations() {
        return jsonRepository.getData().getFireStations();
    }

    public FireStation add(FireStation fireStation) {
        if (fireStation == null) throw new IllegalArgumentException("La FireStation ne peut pas être null");
        getFireStations().add(fireStation);
        jsonRepository.saveData();
        return fireStation;
    }

    public Optional<FireStation> update(String address, String newStation) {
        Optional<FireStation> result = getFireStations().stream()
                .filter(fs -> fs.getAddress().equalsIgnoreCase(address))
                .findFirst()
                .map(fs -> {
                    fs.setStation(newStation);
                    return fs;
                });
        result.ifPresent(fs -> jsonRepository.saveData());
        return result;
    }

    public Optional<FireStation> delete(String address, String station) {
        Optional<FireStation> toDelete = getFireStations().stream()
                .filter(fs -> fs.getAddress().equalsIgnoreCase(address)
                        && fs.getStation().equals(station))
                .findFirst();
        toDelete.ifPresent(fs -> {
            getFireStations().remove(fs);
            jsonRepository.saveData();
        });
        return toDelete;
    }
}