package ch.uzh.ifi.hase.soprafs23.entity;
<<<<<<< HEAD
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
=======

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;

@Entity
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private ArrayList<String> cityoptions;
    private String imageUrl;


    public City(String CityName, ArrayList<String> cityoptions, String imageUrl) {
        this.name = CityName;
        this.cityoptions=cityoptions;
        this.imageUrl=imageUrl;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getCityoptions() {
        return this.cityoptions;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    
    }



>>>>>>> said
