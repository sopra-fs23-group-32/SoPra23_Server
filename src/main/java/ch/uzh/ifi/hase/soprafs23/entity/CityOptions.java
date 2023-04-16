package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class CityOptions {
    private static final Map<String, String[]> CATEGORIES = new HashMap<>();

    static {
        CATEGORIES.put("Europe", new String[]{"AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR", "DE", "GR",
                "HU", "IS", "IE"});
    }

    private static final int MAX_POPULATION = 200000;

    private static final Random random = new Random();

}