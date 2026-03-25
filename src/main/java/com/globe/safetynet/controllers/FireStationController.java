package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.FireStationResponseDTO;
import com.globe.safetynet.dtos.PersonFireStationDTO;
import com.globe.safetynet.entities.Person;
import com.globe.safetynet.services.FireStationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FireStationController {

    private final FireStationService fireStationService;
    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }

    @GetMapping("/firestation")
    public ResponseEntity<FireStationResponseDTO> getPersonByFireStation(@RequestParam String stationNumber) {

        return new ResponseEntity<>(fireStationService.getPersonsByFireStationNumber(stationNumber), HttpStatus.OK);
    }

}
