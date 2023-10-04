package org.main;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.parsers.AlchemyPropertiesFileParser;
import org.parsers.Parser;
import org.parsers.ReagentsFileParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PotionTest {
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
    void calculatePrice(){

        Potion potion1 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Яйцо ястреба")
        );
        assertEquals(1696, potion1.getPrice());

        Potion potion2 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Крыло синей бабочки")
        );
        assertEquals(1696 + 483, potion2.getPrice());

        Potion potion3 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Крыло синей бабочки"),
                Reagent.getReagent("Пепельная ползучая лоза")
        );
        assertEquals(1696 + 483 + 275, potion3.getPrice());

        Potion potion4 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Крыло синей бабочки"),
                Reagent.getReagent("Морской желудь")
        );
        assertEquals(1696 + 483, potion4.getPrice());

        List<Potion> potions = new ArrayList<>();
        potions.add(potion1);
        potions.add(potion4);
        potions.add(potion2);
        potions.add(potion3);
        Collections.sort(potions);
        assertEquals(potion3, potions.get(0));
        assertEquals(potion2, potions.get(1));
        assertEquals(potion4, potions.get(2));
        assertEquals(potion1, potions.get(3));
    }
    @Test
    void equals(){
        Potion p1 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Крыло синей бабочки")
        );
        Potion p2 = new Potion(
                Reagent.getReagent("Крыло синей бабочки"),
                Reagent.getReagent("Голубой горноцвет")
        );
        Potion p3 = new Potion(
                Reagent.getReagent("Крыло синей бабочки"),
                Reagent.getReagent("Морской желудь")
        );
        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(p3));
    }
    @Test
    void compareTo(){
        Potion p2 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Крыло синей бабочки")
        );
        Potion p3 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Лютый гриб"),
                Reagent.getReagent("Крыло синей бабочки")
        );
        assertEquals(2179, p2.getPrice());
        assertEquals(2626, p3.getPrice());
        assertTrue(p2.compareTo(p3) > 0);
    }
}
