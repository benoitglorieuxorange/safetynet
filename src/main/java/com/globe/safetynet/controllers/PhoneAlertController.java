package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.PhoneAlertDTO;
import com.globe.safetynet.services.PhoneAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PhoneAlertController {

    private static final Logger logger = LoggerFactory.getLogger(PhoneAlertController.class);

    private PhoneAlertService phoneAlertService;
    public PhoneAlertController(PhoneAlertService phoneAlertService) {
        this.phoneAlertService = phoneAlertService;
    }

    @GetMapping("phoneAlert")
    public ResponseEntity<List<PhoneAlertDTO>> getAllPhoneAlerts(@RequestParam String firestation_number) {
        logger.info(" Get /phoneAlert?firestation_number={}", firestation_number);
        if  (firestation_number == null) {
            logger.warn("FireStation number is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try{
            List<PhoneAlertDTO> response = phoneAlertService.getPhoneNumberAlerts(firestation_number);
            if (response == null) {
                logger.warn("Response is null");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            logger.info(" Successfully find phone number for FireStation Number: {}", firestation_number);
            return ResponseEntity.ok(response);
        }catch(Exception e){
            logger.error("Unexpected error while processing FireStation Number {}{}", e, firestation_number);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}//EofC
