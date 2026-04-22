package com.globe.safetynet.controllers;


import com.globe.safetynet.dtos.ChildAlertDTO;
import com.globe.safetynet.services.ChildrenAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChildAlertController {

    private static final Logger logger = LoggerFactory.getLogger(ChildAlertController.class);

    private ChildrenAlertService childrenAlertService;

    public ChildAlertController(ChildrenAlertService childrenAlertService) {
        this.childrenAlertService = childrenAlertService;
    }

    @GetMapping("childalert")
    public ResponseEntity<?> getChildAlert(@RequestParam String address) {
        logger.info(" GET /ChildAlert?address=<address> called" + address);
        address = address.replace("\"", "");
        if (address.isEmpty()){
            logger.warn("Address is empty");
            return ResponseEntity.badRequest().build();
        }
        try{
            List<ChildAlertDTO> response = childrenAlertService.getChildrenByAddress(address);
            if (response == null){
                logger.warn("Child alert not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No data found for address {}" + address);
            }
            logger.info("Successfully retrieved child alert with address {}", address);
            return ResponseEntity.ok(response);
            //return ResponseEntity.ok(childrenAlertService.getChildrenByAddress(address));
        } catch (Exception e) {
            logger.error("Unexpected error while processing address {}{}", e, address);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error");

        }

    }
}
