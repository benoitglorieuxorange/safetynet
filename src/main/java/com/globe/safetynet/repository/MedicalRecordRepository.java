package com.globe.safetynet.repository;

import com.globe.safetynet.entities.MedicalRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MedicalRecordRepository {

    private final JsonRepositoryBase jsonRepository;

    public MedicalRecordRepository(JsonRepositoryBase jsonRepository) {
        this.jsonRepository = jsonRepository;
    }

    private List<MedicalRecord> getMedicalRecords() {
        return jsonRepository.getData().getMedicalRecords();
    }

    public MedicalRecord add(MedicalRecord record) {
        if (record == null) throw new IllegalArgumentException("MedicalRecord cannot be null");
        getMedicalRecords().add(record);
        jsonRepository.saveData();
        return record;
    }

    public Optional<MedicalRecord> update(String firstName, String lastName, MedicalRecord updated) {
        Optional<MedicalRecord> result = getMedicalRecords().stream()
                .filter(r -> r.getFirstName().equalsIgnoreCase(firstName)
                        && r.getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .map(r -> {
                    if (updated.getBirthdate()   != null) r.setBirthdate(updated.getBirthdate());
                    if (updated.getMedications() != null) r.setMedications(updated.getMedications());
                    if (updated.getAllergies()   != null) r.setAllergies(updated.getAllergies());
                    return r;
                });
        result.ifPresent(r -> jsonRepository.saveData());
        return result;
    }

    public Optional<MedicalRecord> delete(String firstName, String lastName) {
        Optional<MedicalRecord> toDelete = getMedicalRecords().stream()
                .filter(r -> r.getFirstName().equalsIgnoreCase(firstName)
                        && r.getLastName().equalsIgnoreCase(lastName))
                .findFirst();
        toDelete.ifPresent(r -> {
            getMedicalRecords().remove(r);
            jsonRepository.saveData();
        });
        return toDelete;
    }
}
