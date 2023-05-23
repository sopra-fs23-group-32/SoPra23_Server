import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class gasg {

    public static List<String> getRandomCities(List<String> countries, int minPopulation) throws Exception {
        Random random = new Random();
        List<String> selectedCities = new ArrayList<>();
        String username = "davidwho"; // replace with your GeoNames username

        while (selectedCities.size() < 4) {
            // Choose random country
            String country = countries.get(random.nextInt(countries.size()));

            // Get list of cities in the country with population greater than minPopulation
            String apiURL = String.format(
                    "http://api.geonames.org/citiesJSON?north=-90&south=90&east=-180&west=180&lang=en&country=%s&maxRows=1000&username=%s",
                    country, username);

            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            JSONArray cities = new JSONObject(content.toString()).getJSONArray("geonames");

            if (cities.length() > 0) {
                // Choose random city
                JSONObject city;
                do {
                    city = cities.getJSONObject(random.nextInt(cities.length()));
                } while (city.getInt("population") < minPopulation);

                selectedCities.add(city.getString("name"));
            } else {
                System.out.println("No cities found in " + country + " with population over " + minPopulation);
            }
        }

        return selectedCities;
    }

    public static void main(String[] args) {
        List<String> countries = Arrays.asList("Italy");
        int minPopulation = 500000;

        try {
            List<String> cities = getRandomCities(countries, minPopulation);
            System.out.println(cities);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
