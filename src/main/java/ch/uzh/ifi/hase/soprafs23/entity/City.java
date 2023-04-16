package ch.uzh.ifi.hase.soprafs23.entity;

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



