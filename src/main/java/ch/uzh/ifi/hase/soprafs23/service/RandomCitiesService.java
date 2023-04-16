package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.City;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RandomCitiesService {
    private final String BASE_URL = "https://wft-geo-db.p.rapidapi.com/v1/geo/adminDivisions?countryIds=%s&minPopulation=%d";
    private final String countryCode_Europe="AT,BE,BG,HR,CY,CZ,DK,EE,FI,FR,DE,GR,HU,IS,IE,IT,LV,LI,LT,LU,MT,MD,MC,NL,NO,PL,PT,RO,RU,SM,RS,SK,SI,ES,SE,CH,UA,GB";
    private City city;
    public City getRandomCities(String category, int populationThreshold) {
        String countryCode = "";
        switch (category) {
            case "Europe":
                countryCode = countryCode_Europe;
                break;
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }
        String url = String.format(BASE_URL, countryCode, populationThreshold);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-rapidapi-host", "wft-geo-db.p.rapidapi.com")
                .header("x-rapidapi-key", "16c96f92cemsh80ce2c13dcc7223p1dad81jsncad86ccad70b")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(responseBody);
            JsonNode citiesJson = responseJson.get("data");

            ArrayList<String> cities = new ArrayList<String>();
            String right_city;
            for (int i = 0; i < 5; i++) {
                JsonNode cityJson = citiesJson.get(i);
                String cityName = cityJson.get("name").asText();
                cities.add(cityName);
            }
            right_city=cities.get(0);
            City city=new City(right_city,cities,"ma");
            return city;
        } catch (Exception e) {
            System.err.println("Error fetching cities: " + e.getMessage());
            City city2=new City(null,null,null);
            return city2;
        }

    }
}
