package com.globe.safetynet.controllers;


import com.globe.safetynet.dtos.FireAlertDTO;
import com.globe.safetynet.services.FireAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FireAlertController {

    private static final Logger logger = LoggerFactory.getLogger(FireAlertController.class);
    private FireAlertService fireAlertService;

    public FireAlertController(FireAlertService fireAlertService) {
        this.fireAlertService = fireAlertService;
    }

    @GetMapping("fire")
    public ResponseEntity<FireAlertDTO> getFireAlert(@RequestParam String address) {
        logger.info(" Get / fireAlert?address={}", address);
        if (address == null) {
            logger.warn(" Address is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try{
            //return ResponseEntity.ok(fireAlertService.getPersonByAddress(address));
            //List<String> response = fireAlertService.getPersonByAddress(address);
            FireAlertDTO response = fireAlertService.getPersonByAddress(address);
            if(response == null){
                logger.warn(" Response is null");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(response);
        }catch (Exception e){
            ogger.error("Error fetching fire alert for address {}: {}", address, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}

