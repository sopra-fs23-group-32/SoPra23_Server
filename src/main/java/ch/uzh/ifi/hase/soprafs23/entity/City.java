package ch.uzh.ifi.hase.soprafs23.entity;
//useless comment
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class City {
    public String name;
    public String country;
    public String location;
    private List<String> attractions;

    public City(String name, String country, String location, List<String> attractions) {
        this.name = name;
        this.country = country;
        this.location = location;
        this.attractions = attractions;
    }

    public String getName() {return name;}

    public String getCountry() {return country;}

    public String getLocation() {return location;}
    public List<String> getAttractions() {return attractions;}

    /**
     * Use flicker API to get the city image url
     */
    public URL generatePicture() throws URISyntaxException, IOException {
        Random rand = new Random(attractions.size());
        String attraction = attractions.get(rand.nextInt());

        FlickrImageCollector imageCollecter = new FlickrImageCollector(attraction);
        return imageCollecter.getImageUrl();
    }
}
