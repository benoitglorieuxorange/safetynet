package com.globe.safetynet.controllers;

import com.globe.safetynet.services.CommunityEmailService;
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
public class CommunityEmailController {

    private static final Logger logger = LoggerFactory.getLogger(CommunityEmailController.class);

    private CommunityEmailService communityEmailService;
    public CommunityEmailController(CommunityEmailService communityEmailService) {
        this.communityEmailService = communityEmailService;
    }

    @GetMapping("communityEmail")
    public ResponseEntity<?> getCommunityEmail(@RequestParam String city) {
        logger.info(" /Get /communityEmail?city={}", city);
        city = Character.toUpperCase(city.charAt(0)) + city.substring(1);
        if (city.isEmpty()){
            logger.warn(" /Get /communityEmail?city is empty");
        }
        try{
            List<?> response  = communityEmailService.getCommunityEmails(city);
            if  (response.isEmpty()){
                logger.warn("City is empty");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No data found for city: {}" + city);
            }
            logger.info(" Successfully found data for Get /communityEmail?city={}", city);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e) {
            logger.error("Unexpected error while processing city {}{}", e, city);
            return status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error");
        }
        //return ResponseEntity.ok(communityEmailService.getCommunityEmails(city));
    }

}
