package org.main;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.parsers.AlchemyPropertiesFileParser;
import org.parsers.Parser;
import org.parsers.ReagentsFileParser;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ReagentsBagTest {
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
    void loadBag(){

        ReagentsBag bag = ReagentsBag.loadInitialReagents("initial_reagents_bag_test.txt");
        assertEquals(7, bag.size());

        assertEquals(25, bag.contains(Reagent.getReagent("Крыло синей бабочки")));

        Reagent reg = Reagent.getReagent("Лютый гриб");
        bag.add(reg, 50);
        assertEquals(62, bag.contains(reg));

        bag.remove(reg, 11);
        assertEquals(51, bag.contains(reg));

        assertThrows(NotEnoughAmountException.class, () -> bag.remove(reg, 100));

        bag.remove(reg, 51);
        assertEquals(6, bag.size());

        ReagentsBag bag2 = (ReagentsBag) bag.clone();

        bag2.add(reg, 5);
        assertNotEquals(bag.size(), bag2.size());
    }
    @Test
    void getPotionsAvailable(){

        ReagentsBag bag = ReagentsBag.loadInitialReagents("initial_reagents_bag_test.txt");

        Potion potion1 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Крыло синей бабочки")
        );
        assertEquals(8, bag.getPotionsAvailable(potion1));

        Potion potion2 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Живица сприггана")
        );
        assertEquals(0, bag.getPotionsAvailable(potion2));

        Potion potion3 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Живица сприггана"),
                Reagent.getReagent("Крыло синей бабочки")
        );
        assertEquals(0, bag.getPotionsAvailable(potion3));

        Potion potion4 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Маленькие рога"),
                Reagent.getReagent("Крыло синей бабочки")
        );
        assertEquals(4, bag.getPotionsAvailable(potion4));
    }
    @Test
    void removeAllFor(){

        ReagentsBag bag = ReagentsBag.loadInitialReagents("initial_reagents_bag_test.txt");

        Potion potion1 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Крыло синей бабочки")
        );

        assertEquals(8, bag.removeAllFor(potion1));

        assertEquals(0, bag.contains(Reagent.getReagent("Голубой горноцвет")));
        assertEquals(17, bag.contains(Reagent.getReagent("Крыло синей бабочки")));
    }
}
