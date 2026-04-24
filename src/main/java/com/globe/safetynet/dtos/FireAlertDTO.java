package com.globe.safetynet.dtos;

import java.util.List;

public record FireAlertDTO(
        List<PersonFireAlertDTO> persons,
        String fireStationNumber
) {}