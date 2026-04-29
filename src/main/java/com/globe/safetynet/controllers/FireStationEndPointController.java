package com.globe.safetynet.controllers;

import com.globe.safetynet.entities.FireStation;
import com.globe.safetynet.services.FireStationEndPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/firestation")
public class FireStationEndPointController {

    private static final Logger logger = LoggerFactory.getLogger(FireStationEndPointController.class);

    private FireStationEndPointService fireStationEndPointService;

    public  FireStationEndPointController(FireStationEndPointService fireStationEndPointService) {
        this.fireStationEndPointService = fireStationEndPointService;
    }

    @PostMapping
    public ResponseEntity<FireStation> addFireStation(@RequestBody FireStation fireStation) {
        logger.info(" ADD /fireStation {}", fireStation);
        try{
            FireStation response = fireStationEndPointService.addFireStation(fireStation);
            if (response == null) {
                logger.warn(" Response is null with addFireStation");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            logger.info("Successfully added fireStation {}", fireStation);
            return ResponseEntity.ok(fireStation);
        }catch (Exception e) {
            logger.error("Unexpected error while processing FireStation Number {}{}", e, fireStation);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

        }
    }

    @DeleteMapping
    public ResponseEntity<Optional<FireStation>> deleteFireStation(@RequestBody FireStation fireStation) {
        logger.info(" DELETE /fireStation {}", fireStation);
        try{
            Optional<FireStation> deleted = fireStationEndPointService.deleteFireStation(fireStation);
            if (deleted == null) {
                logger.warn("Response is null with deleteFireStation Number {}", fireStation);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            logger.info("Successfully delete fireStation {}", fireStation);
            return ResponseEntity.ok(Optional.of(fireStation));
        }catch (Exception e) {
            logger.error("Unexpected error while processing deleting FireStation Number {}{}", e, fireStation);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PutMapping
    public ResponseEntity<Optional<FireStation>> updateFireStation(@RequestBody FireStation fireStation) {
        logger.info(" PUT /fireStation {}", fireStation);
        try{
            Optional<FireStation> updated = fireStationEndPointService.updateFireStation(fireStation);
            if (updated == null) {
                logger.warn("Response is null with updated FireStation Number {}", fireStation);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            logger.info("Successfully update fireStation {}", fireStation);
            return ResponseEntity.ok(Optional.of(fireStation));
        }catch (Exception e) {
            logger.error("Unexpected error while processing updating FireStation Number {}{}", e, fireStation);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
