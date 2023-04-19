package ch.uzh.ifi.hase.soprafs23.constant;

public enum CityCategory {
    EUROPE,
    ASIA,
    NORTH_AMERICA,
    AFRICA,
    SOUTH_AMERICA,
    OCEANIA,
    WORLD;
    public String toString(){
        switch(this){
            case EUROPE:
                return "europe";
            case ASIA:
                return "asia";
            case NORTH_AMERICA:
                return "north america";
            case AFRICA:
                return "Africa";
            case SOUTH_AMERICA:
                return "south america";
            case OCEANIA:
                return "oceania";
            case WORLD:
                return "world";
            default:
                return "Unknown";

        }
    }
}
