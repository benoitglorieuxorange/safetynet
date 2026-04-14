package com.globe.safetynet.controllers;

import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.services.FireStationEndPointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/firestation")
public class FireStationEndPointController {

    //injection dépendances
    private FireStationEndPointService fireStationEndPointService;

    public  FireStationEndPointController(FireStationEndPointService fireStationEndPointService) {
    this.fireStationEndPointService = fireStationEndPointService;
    }

    @PostMapping
    public ResponseEntity<FireStation> addFireStation(@RequestBody FireStation fireStation) {
        return  ResponseEntity.ok(fireStationEndPointService.addFireStation(fireStation));
    }

    @DeleteMapping
    public ResponseEntity<Optional<FireStation>> deleteFireStation(@RequestBody FireStation fireStation) {
        return ResponseEntity.ok(fireStationEndPointService.deleteFireStation(fireStation));
    }


}
