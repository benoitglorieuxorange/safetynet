package com.globe.safetynet.dtos;

import java.util.List;

public record ChildAlertDTO(
        String firstName,
        String lastName,
        int age,
        List<HouseholdMemberDTO> householdMembers
) {}
