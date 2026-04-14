package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.FireStationResponseDTO;
import com.globe.safetynet.services.FireStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;



@RestController
public class FireStationController {

    //private static final Logger logger = (Logger) LoggerFactory.getLogger(FireStationController.class);
    private static final Logger logger = LoggerFactory.getLogger(FireStationController.class);


    private final FireStationService fireStationService;
    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }

//    @GetMapping("/firestation")
//    public ResponseEntity<FireStationResponseDTO> getPersonByFireStation(@RequestParam String stationNumber) {
//        logger.info("Requête reçue dans le controller /firestation");
//
//        return new ResponseEntity<>(fireStationService.getPersonsByFireStationNumber(stationNumber), HttpStatus.OK);
//
//    }

//    @GetMapping("/firestation/{stationNumber}")
//    public ResponseEntity<FireStationResponseDTO> getPersonByFireStation(@PathVariable String stationNumber) {
//        logger.info("Requête reçue dans le controller /firestation");
//
//        return new ResponseEntity<>(fireStationService.getPersonsByFireStationNumber(stationNumber), HttpStatus.OK);
//
//    }
    @GetMapping("/firestation")
    public ResponseEntity<?> getPersonByFireStation(@RequestParam int stationNumber) {
        logger.info(" GET /firestation?stationNumber=<stationNumber>" + stationNumber + ">");

        if (stationNumber < 1 || stationNumber > 4) {
            logger.warn("Invalid stationNumber: {}",  stationNumber);

            return ResponseEntity
                    .badRequest()
                    .body("station Number must be between 1 and 4 inclusive.");
        }
        try {
            FireStationResponseDTO response = fireStationService.getPersonsByFireStationNumber(String.valueOf(stationNumber));
            if (response == null) {
                logger.warn("No data found for station {}", stationNumber);
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("No data found for station " + stationNumber);
            }
            logger.info("Successful response station {}" + stationNumber);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Unexpected error while processing station {}" + stationNumber, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error");
        }
    }


} // endOfClass
