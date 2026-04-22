package com.globe.safetynet.utils;

import com.globe.safetynet.entities.Data;
import com.globe.safetynet.entities.MedicalRecord;
import com.globe.safetynet.entities.Person;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PersonUtils {
    private static final Logger logger =  LoggerFactory.getLogger(PersonUtils.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private PersonUtils(){}

    public static String buildFullName(Person person) {
        return person.getFirstName() + " " + person.getLastName().toUpperCase();
    }

    public static String buildFullName(MedicalRecord record) {
        return record.getFirstName() + " " + record.getLastName().toUpperCase();
    }

    public static int calculateAge(String birthdate){
        LocalDate birthDate = LocalDate.parse(birthdate, DATE_FORMATTER);
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public static void validateData(Data data){
        if (data == null){
            logger.error("Repository return null data");
            throw new IllegalArgumentException("Repository return null data");
        }
    }

    public static List<Person> findPersonByAddress(Data data, List<String> addresses){
        return data.getPersons().stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .toList();
    }

    public record AgeCount(int adultCount, int childCount){}

}
