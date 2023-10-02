package org.main;

import org.parsers.*;

import java.io.IOException;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {

        loadDatabase();

        for(var i : Reagent.reagents)
            if( i.properties.size() != 4 )
                System.out.println("Failed: " + i.getName() + i.properties.size());

    }
    public static void loadDatabase(){

        try{

            Parser parser = new AlchemyPropertiesFileParser();
            AlchemyProperty.properties.addAll(parser.parse());

            parser = new ReagentsFileParser();
            Reagent.reagents.addAll(parser.parse());

        }
        catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }

    }
}
