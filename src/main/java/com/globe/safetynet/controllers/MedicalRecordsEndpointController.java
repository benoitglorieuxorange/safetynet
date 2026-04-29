package com.globe.safetynet.controllers;

import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.services.MedicalRecordService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/medicalrecord")
public class MedicalRecordsEndpointController {

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordsEndpointController.class);
    private MedicalRecordService medicalRecordService;

    public MedicalRecordsEndpointController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    public ResponseEntity<Optional<MedicalRecord>> addMedicalRecord(@Valid @RequestBody MedicalRecord medicalRecord) {
        logger.debug("Adding medical record: {}", medicalRecord);
        try{
            Optional<MedicalRecord> response = Optional.ofNullable(medicalRecordService.addMedicalRecord(medicalRecord));
            if (response == null){
                logger.warn("Medical record not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            logger.info("Successfully add Medical record {}", medicalRecord);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Unexpected error while add medical Record {}{}", e, medicalRecord);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Optional<MedicalRecord>> deleteMedicalRecord(@Valid @RequestBody MedicalRecord medicalRecord) {
        logger.debug("Deleting medical record: {}", medicalRecord);
        try{
            Optional<MedicalRecord> response = medicalRecordService.deleteMedicalRecord(medicalRecord);
            if (response == null){
                logger.warn("Medical record not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            logger.info("Successfully delete Medical record {}", medicalRecord);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Unexpected error while  delete medical Record {}{}", e, medicalRecord);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PutMapping
    public ResponseEntity<Optional<MedicalRecord>> updateMedicalRecord(@Valid @RequestBody MedicalRecord medicalRecord) {
        logger.debug("Updating medical record: {}", medicalRecord);
        try{
            Optional<MedicalRecord> response = medicalRecordService.updateMedicalRecord(medicalRecord);
            if (response == null){
                logger.warn("Medical record not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            logger.info("Successfully update Medical record {}", medicalRecord);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Unexpected error while processing update medical Record {}{}", e, medicalRecord);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
