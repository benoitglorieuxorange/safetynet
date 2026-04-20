package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.DeletePersonDTO;
import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.services.MedicalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/medicalrecord")
public class MedicalRecordsEndpointController {

    private MedicalRecordService medicalRecordService;

    public MedicalRecordsEndpointController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    public ResponseEntity<MedicalRecord> addMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        return ResponseEntity.ok(medicalRecordService.addMedicalRecord(medicalRecord));
    }

    @DeleteMapping
    public ResponseEntity<Optional<MedicalRecord>> deleteMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        return ResponseEntity.ok(medicalRecordService.deleteMedicalRecord(medicalRecord));
    }

    @PutMapping
    public ResponseEntity<Optional<MedicalRecord>> updateMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        return ResponseEntity.ok(medicalRecordService.updateMedicalRecord(medicalRecord));
    }

}
