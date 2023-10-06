package org.main;

import java.util.*;

public class AlchemyProperty implements Comparable<AlchemyProperty> {

    public static final Set<AlchemyProperty> properties = new TreeSet<>();

    private static String VALUES_DELIMITER = "#";

    static final int NAME_FIELD_LENGTH = 40;
    private String name;
    private AlchemyType type;
    private int price;
    public AlchemyProperty(String name, AlchemyType type, int price){
        this.name = name;
        this.type = type;
        this.price = price;
    }
    @Override
    public int compareTo(AlchemyProperty other){
        int price = other.getPrice() - this.getPrice();
        if( price != 0 )
            return price;

        return this.getName().compareTo(other.getName());
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }
    public AlchemyType getType() {
        return type;
    }
    @Override
    public int hashCode(){
        return name.hashCode();
    }
    @Override
    public boolean equals(Object o){
        if( this == o )
            return true;
        if( this.getClass() != o.getClass() )
            return false;

        AlchemyProperty other = (AlchemyProperty) o;

        return
                this.type == other.type &&
                Objects.equals(this.name, other.name);
    }
    public static AlchemyProperty valueOf(String value) throws FormatUnknownException {

        if(value == null || value.equals(""))
            throw new FormatUnknownException();

        String[] parts = value.split(VALUES_DELIMITER);

        if(parts.length != 3)
            throw new FormatUnknownException();

        AlchemyType type;
        try {
            type = AlchemyType.valueOf(parts[0]);
        }
        catch(IllegalArgumentException e){
            throw new FormatUnknownException();
        }

        String name = parts[1].strip();

        int price;
        try {
            price = Integer.valueOf(parts[2].strip());
        }
        catch(NumberFormatException e){
            throw new FormatUnknownException();
        }

        return new AlchemyProperty(name, type, price);
    }
    public static AlchemyProperty findProperty(String name){

        for(var prop : properties)
            if(prop.getName().equalsIgnoreCase(name))
                return prop;

        return null;
    }
    @Override
    public String toString(){
        String format = String.format("%%s%1$s %%-%2$ds%1$s %%d", VALUES_DELIMITER, NAME_FIELD_LENGTH);
        return String.format(format, getType(), getName(), getPrice());
    }
    public static AlchemyProperty getProperty(String name){
        return properties.stream()
                .filter(i -> i.getName().equals(name))
                .findAny()
                .orElse(null);
    }
}
