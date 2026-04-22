package com.globe.safetynet.repository;

import com.globe.safetynet.entities.Data;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Repository
public class JsonRepositoryBase {

    private Data data;
    private final String dataFilePath = "src/main/resources/data.json";

    @PostConstruct
    public void loadData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("data.json").getInputStream();
            this.data = mapper.readValue(inputStream, Data.class);

            if (data != null) {
                System.out.println("Data load with success :");
                System.out.println("  - Person : " + (data.getPersons() != null ? data.getPersons().size() : 0));
                System.out.println("  - FireStations : " + (data.getFireStations() != null ? data.getFireStations().size() : 0));
                System.out.println("  - MedicalRecords : " + data.getMedicalRecords().size());
            } else {
                System.err.println("Data are null !");
            }
        } catch (IOException e) {
            System.err.println("JSON file cannot be read: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Data getData() {
        if (data == null) throw new IllegalStateException("Data cannot be load");
        return data;
    }

    protected void saveData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(dataFilePath), data);
        } catch (Exception e) {
            System.err.println("Error during Json file Save  : " + e.getMessage());
            e.printStackTrace();
        }
    }


}
