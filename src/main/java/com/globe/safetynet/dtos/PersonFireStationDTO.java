package com.globe.safetynet.dtos;

public record PersonFireStationDTO(
        String firstName,
        String lastName,
        String address,
        String phone
) {}