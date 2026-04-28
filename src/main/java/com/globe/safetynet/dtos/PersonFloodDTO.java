package com.globe.safetynet.dtos;

import java.util.List;

public record PersonFloodDTO(
        String firstName,
        String lastName,
        String address,
        String phone,
        int age,
        List<String> medications,
        List<String> allergies
) {}
