package ch.uzh.ifi.hase.soprafs23.entity;

import javax.xml.stream.Location;

public class City {
    public String name;
    public String country;
    public String location;

    public City(String name, String country, String location) {
        this.name = name;
        this.country = country;
        this.location = location;
    }

    public String getName() {return name;}

    public String getCountry() {return country;}

    public String getLocation() {return location;}

    /**
     * Use flicker API to get picture online
     */
    public void generatePicture() {}
}
