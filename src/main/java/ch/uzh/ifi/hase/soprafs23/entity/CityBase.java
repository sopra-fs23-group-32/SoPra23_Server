package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.CityCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Zilong Deng
 */
public class CityBase {

    private final CityCategory category;
    private final List<City> cityList = new ArrayList<>();

    public CityBase(CityCategory category) {
        this.category = category;
        this.loadCityList();
    }

    /**
     * Load city list of corresponding category from database
     */
    private void loadCityList() {
        // for temp. use
        if(category == CityCategory.EUROPE){
            City paris = new City("Paris", "France", "N48 E2", new ArrayList<>(Arrays.asList("Eiffel Tower", "Arc de Triomphe", "Luxembourg Gardens")));
            City berlin = new City("Berlin", "Germany", "N52 E13", new ArrayList<>(Arrays.asList("Brandenburg Gate", "Tempelhofer Feld", "Altes Museum")));
            City zurich = new City("Zurich", "Switzerland", "N47 E8", new ArrayList<>(Arrays.asList("Lindenhof", "Grossmünster", "Fraumünster Church")));
            City london = new City("London", "UnitedKingdom", "N51 E0", new ArrayList<>(Arrays.asList("London Eye", "Tower of London", "Tower Bridge")));
            City geneve = new City("Geneve", "Switzerland", "N46 E6", new ArrayList<>(Arrays.asList("Geneva Water Fountain", "Palais des Nations", "Brunswick Monument")));
            cityList.add(paris);cityList.add(berlin);cityList.add(london);
            cityList.add(zurich);cityList.add(geneve);
        }
    }

    public int getCityListLength() {
        if(cityList.size() == 0){
            System.out.println("City list uninitialized!");
        }
        return cityList.size();
    }

    /**
     * Draw four random cities from the list
     */
    public List<City> drawCities() {
        List<City> drawnCities = new ArrayList<>();
        Random random = new Random();
        for(int i=0; i<4; i++) {
            int intRand = random.nextInt(cityList.size() - 1);
            City selectedCity = cityList.get(intRand);
            if(drawnCities.contains(selectedCity)) {
                i--;
                continue;
            }
            drawnCities.add(selectedCity);
            cityList.remove(selectedCity);
        }
        for(int i=0; i<4; i++) {
            cityList.add(drawnCities.get(i));
        }
        return drawnCities;
    }

    public City getCity(int index) {
        return cityList.get(index);
    }

    public CityCategory getCategory() {
        return category;
    }
}
