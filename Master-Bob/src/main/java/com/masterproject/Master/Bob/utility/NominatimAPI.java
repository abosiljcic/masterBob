package com.masterproject.Master.Bob.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class NominatimAPI {
    @Bean
    public RestTemplate restTemplate ()
    {
        return new RestTemplate();
    }

    public String callNominatimAPI(String address, String postCode)
    {
        String url = "https://nominatim.openstreetmap.org/search?q=" + address + ", Belgrade, " + postCode + "&format=json&addressdetails=1&countrycodes=RS";
        ResponseEntity<String> response = restTemplate().getForEntity(url, String.class);
        return response.getBody();
    }

    public double[][] parseJson (String json) throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        double[][] coordinates = new double[rootNode.size()][2];
        for (int i = 0; i < rootNode.size(); i++) {
            JsonNode node = rootNode.get(i);
            coordinates[i][0] = node.get("lat").asDouble();
            coordinates[i][1] = node.get("lon").asDouble();
        }

        return coordinates;
    }
}
