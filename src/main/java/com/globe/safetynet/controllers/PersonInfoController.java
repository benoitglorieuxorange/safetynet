package com.globe.safetynet.controllers;

import com.globe.safetynet.dtos.PersonInfoDTO;
import com.globe.safetynet.services.PersonInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
public class PersonInfoController {

    private static final Logger logger = LoggerFactory.getLogger(PersonInfoController.class);

    private PersonInfoService personInfoService;

    public PersonInfoController(PersonInfoService personInfoService) {
        this.personInfoService = personInfoService;
    }

    @GetMapping("personInfolastName")
    public ResponseEntity<?> getPersonInfoLastName(@RequestParam String lastName) {
        logger.info(" GET /ChildAlert?address=<address> called" + lastName);
        lastName = Character.toUpperCase(lastName.charAt(0)) + lastName.substring(1);
        if (lastName.isEmpty()){
            logger.warn("Address is empty");
            return ResponseEntity.badRequest().build();
        }
        try{
            List<PersonInfoDTO> response = personInfoService.getPersonInfoByLastname(lastName);
            if (response.isEmpty()){
                logger.warn("No person info found");
                return ResponseEntity.notFound().build();
            }
            logger.info("Successfully found data for GET /personInfolastName {}", lastName);
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            logger.error("Unexpected error while processing lastName {}{}", e, lastName);
            return status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Internal Server Error");
            }
    }
}
