package com.globe.safetynet.dtos;


import java.util.List;

public record PersonInfoDTO(
        String firstName,
        String lastName,
        String address,
        String email,
        int age,
        List<String> medications,
        List<String> allergies
) {}
