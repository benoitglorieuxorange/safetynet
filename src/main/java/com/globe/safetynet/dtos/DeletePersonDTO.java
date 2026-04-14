package com.globe.safetynet.dtos;

import com.globe.safetynet.entities.Person;

public record DeletePersonDTO(String message, Person deletePerson) {
}
