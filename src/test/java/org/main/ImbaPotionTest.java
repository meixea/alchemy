package org.main;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.parsers.AlchemyPropertiesFileParser;
import org.parsers.FileParser;
import org.parsers.Parser;
import org.parsers.ReagentsFileParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    void loadFilters(){
        ImbaPotion potion = new ImbaPotion("test_imba_potion.txt", RESULT_FILENAME);

        List<AlchemyProperty> whiteList = potion.getWhiteList();
        List<Reagent> blackList = potion.getBlackList();
        assertEquals(2, whiteList.size());
        assertEquals(1, blackList.size());

        potion = new ImbaPotion("test_imba_potion2.txt", RESULT_FILENAME);
        whiteList = potion.getWhiteList();
        blackList = potion.getBlackList();
        assertEquals(1, whiteList.size());
        assertEquals(3, blackList.size());

    }
    @Test
    void getReagentsWithTarget(){

        ImbaPotion potion = new ImbaPotion("test_imba_potion2.txt", RESULT_FILENAME);

        List<Reagent> blackList = potion.getBlackList();
        for(Reagent reagent : blackList)
            assertTrue(Reagent.reagents.contains(reagent));

        List<Reagent> filteredList = potion.getReagentsWithTarget();
        for(Reagent reagent : blackList)
            assertFalse(filteredList.contains(reagent));

    }
    @Disabled
    void special(){

        FileParser parser = new AlchemyPropertiesFileParser();

        List<AlchemyProperty> props = new ArrayList<>(AlchemyProperty.properties);
        props.sort( Comparator.comparing(AlchemyProperty::getName) );
        try {
            parser.save(props);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
