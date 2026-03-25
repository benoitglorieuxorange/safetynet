package com.globe.safetynet.repository;

import com.globe.safetynet.entities.Data;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

@Repository
public class JsonRepository {

    private Data data;

    @PostConstruct
    public void loadData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("data.json").getInputStream();

            this.data = mapper.readValue(inputStream, Data.class);

            // Vérification et logs
            if (data != null) {
                System.out.println(" Données chargées avec succès :");
                System.out.println("   - Personnes : " +
                        (data.getPersons() != null ? data.getPersons().size() : 0));
                System.out.println("   - FireStations : " +
                        (data.getFireStations() != null ? data.getFireStations().size() : 0));
            } else {
                System.err.println("❌ Les données sont null après chargement !");
            }

        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement du fichier JSON : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Data getData() {
        if (data == null) {
            throw new IllegalStateException("Les données n'ont pas été chargées");
        }
        return data;
    }
}