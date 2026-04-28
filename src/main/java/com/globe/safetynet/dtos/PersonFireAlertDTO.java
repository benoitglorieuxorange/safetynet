package com.globe.safetynet.dtos;

import java.util.List;

public record PersonFireAlertDTO(
        String firstName,
        String lastName,
        int age,
        List<String> medications,
        List<String> allergies
) {}