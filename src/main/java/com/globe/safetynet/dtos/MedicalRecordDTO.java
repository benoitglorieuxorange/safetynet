package com.globe.safetynet.dtos;

import com.globe.safetynet.entities.MedicalRecord;

public record MedicalRecordDTO(MedicalRecord medicalRecord, int age) {
}
