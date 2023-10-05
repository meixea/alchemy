package org.main;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.parsers.AlchemyPropertiesFileParser;
import org.parsers.Parser;
import org.parsers.ReagentsFileParser;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.main.Main.POTION_FILENAME;
import static org.main.Main.RESULT_FILENAME;

public class ImbaPotionTest {
    @BeforeAll
    static void setup(){

        Parser propParser = new AlchemyPropertiesFileParser();
        Parser regParser = new ReagentsFileParser();

        try {
            AlchemyProperty.properties.addAll(propParser.parse());
            Reagent.reagents.addAll(regParser.parse());
        }
        catch( IOException e ){
            System.out.println(e.getMessage());
        }
    }
    @Test
    void loadingTarget(){
        ImbaPotion potion = new ImbaPotion(POTION_FILENAME, RESULT_FILENAME);

        assertTrue(AlchemyProperty.properties.contains(potion.getTarget()));

    }
    @Test
    void getReagentsWithTarget(){

    }
}
