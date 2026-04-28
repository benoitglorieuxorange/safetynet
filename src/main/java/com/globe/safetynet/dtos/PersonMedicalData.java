package com.globe.safetynet.dtos;

import java.util.List;

public record PersonMedicalData(
        String firstName,
        String lastName,
        String address,
        String email,
        String phone,
        int age,
        List<String> medications,
        List<String> allergies
){}
