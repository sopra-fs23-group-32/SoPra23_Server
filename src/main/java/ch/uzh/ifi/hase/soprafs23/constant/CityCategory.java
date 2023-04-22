package ch.uzh.ifi.hase.soprafs23.constant;

public enum CityCategory {
    EUROPE("Europe"),
    ASIA("Asia"),
    NORTH_AMERICA("North America"),
    SOUTH_AMERICA("South America"),
    AFRICA("Africa"),
    OCEANIA("Oceania"),
    WORLD("World");

    private final String name;

    CityCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
