package com.globe.safetynet.dtos;

import java.util.List;

public record FireStationResponseDTO(
        List<PersonFireStationDTO> persons,
        int adultCount,
        int childCount
) {}
