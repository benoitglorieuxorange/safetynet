package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.PersonFloodDTO;
import com.globe.safetynet.services.FloodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PersonFloofController {

    private static final Logger logger = LoggerFactory.getLogger(PersonFloofController.class);

    private FloodService floodService;
    public PersonFloofController(FloodService floodService) {
        this.floodService = floodService;
    }

    @RequestMapping("flood")
    public ResponseEntity<?> getFlood(@RequestParam List<String> stationNumber) {

        List<String> allowed = List.of("1", "2", "3", "4", "5");

        List<String> normalized = stationNumber.stream()
                .flatMap(s -> Arrays.stream(s.replace(".", ",").split(",")))
                .map(String::trim)
                .collect(Collectors.toList());

        boolean isValid = normalized.stream().allMatch(allowed::contains);

        if (!isValid) {
            return ResponseEntity.badRequest()
                    .body("Use : 1, 2, 3, 4, 5 for FireStation Number");
        }

        logger.info("GET /flood?stationNumber={}", normalized);
        //return ResponseEntity.ok(floodService.getPersonByFireStationList(normalized));

        try{
            List<PersonFloodDTO> response = floodService.getPersonByFireStationList(normalized);
            if (response == null){
                logger.warn("Child alert not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No data found for address {}" + normalized);
            }
            logger.info("Successfully retrieved child alert with address {}", normalized);
            return ResponseEntity.ok(response);
            //return ResponseEntity.ok(childrenAlertService.getChildrenByAddress(normalized));
            } catch (Exception e) {
                logger.error("Unexpected error while processing address {}{}", e, normalized);
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Internal Server Error");
            }
    }
}
