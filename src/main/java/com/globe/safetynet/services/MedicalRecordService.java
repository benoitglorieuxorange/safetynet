package com.globe.safetynet.services;


import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MedicalRecordService {

    private MedicalRecordRepository medicalRecordRepository;
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;

    }

    public MedicalRecord addMedicalRecord(MedicalRecord medicalRecord) {
        return medicalRecordRepository.add(medicalRecord);
    }

    public Optional<MedicalRecord> updateMedicalRecord(MedicalRecord medicalRecord) {
        return medicalRecordRepository.update(medicalRecord.getFirstName(), medicalRecord.getLastName(), medicalRecord);
    }

    public Optional<MedicalRecord>deleteMedicalRecord(MedicalRecord medicalRecord) {
        return medicalRecordRepository.delete(medicalRecord.getFirstName(), medicalRecord.getLastName());
    }


}
