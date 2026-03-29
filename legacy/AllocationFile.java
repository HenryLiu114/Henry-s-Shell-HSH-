package legacy;

import java.util.HashMap;

public class AllocationFile {
    private String name;
    private HashMap<String, Integer> intAllos;
    private HashMap<String, Double> floAllos;
    private HashMap<String, String> strAllos;

    public AllocationFile(String name){
        this.name = name;
        intAllos = new HashMap<>();
        floAllos = new HashMap<>();
        strAllos = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Integer> getIntAllos() {
        return intAllos;
    }

    public void setIntAllos(HashMap<String, Integer> intAllos) {
        this.intAllos = intAllos;
    }

    public HashMap<String, Double> getFloAllos() {
        return floAllos;
    }

    public void setFloAllos(HashMap<String, Double> floAllos) {
        this.floAllos = floAllos;
    }

    public HashMap<String, String> getStrAllos() {
        return strAllos;
    }

    public void setStrAllos(HashMap<String, String> strAllos) {
        this.strAllos = strAllos;
    }

    
}
