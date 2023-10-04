package org.main;

import java.util.*;

public class Reagent {
    private static final String VALUES_DELIMITER = "#";
    public static final Set<Reagent> reagents = new TreeSet<>(
            Comparator.comparing(Reagent::getName)
    );
    private final String name;

    private List<AlchemyProperty> properties = new ArrayList<>();

    public Reagent(String name){
        this.name = name;
    }

    public void addProperty(AlchemyProperty prop){
        properties.add(prop);
    }


    @Override
    public boolean equals(Object o){
        if( this == o )
            return true;

        String otherName;

        if(o instanceof String)
            otherName = (String) o;
        else if(o instanceof Reagent)
            otherName = ((Reagent)o).getName();
        else
            return false;

        return Objects.equals(this.getName().toLowerCase(), otherName.toLowerCase());
    }
    @Override
    public int hashCode(){
        if( name == null )
            return 0;

        return name.hashCode();
    }
    public boolean hasProperty(AlchemyProperty property){
        return properties.contains(property);
    }
    public boolean hasProperty(String property){
        return properties.contains(AlchemyProperty.getProperty(property));
    }
    public String getName() {
        return name;
    }
    public List<AlchemyProperty> getProperties(){
        return properties;
    }
    public static Reagent getReagent(String name){
        return reagents.stream()
                .filter( i -> i.getName().equals(name))
                .findAny()
                .orElse(null);
    }
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder(getName());

        for(var prop : properties)
            result.append(VALUES_DELIMITER + prop.getName());

        return result.toString();
    }
    public static Reagent valueOf(String string){

        String[] parts = string.split(VALUES_DELIMITER);

        Reagent result = new Reagent(parts[0]);

        for(int i = 1; i < parts.length; i++)
            for(var prop : AlchemyProperty.properties)
                if(prop.getName().equals(parts[i])){
                    result.addProperty(prop);
                    break;
                }

        return result;
    }
}
