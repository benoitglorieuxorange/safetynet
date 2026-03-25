package com.globe.safetynet.repository;

import com.globe.safetynet.entities.Data;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Repository
public class JsonRepository {

    private Data data;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void loadData() throws IOException {
        ClassPathResource resource = new ClassPathResource("data.json");
        data = objectMapper.readValue(resource.getInputStream(), Data.class);
    }
    public Data getData() {
            return data;
    }
}