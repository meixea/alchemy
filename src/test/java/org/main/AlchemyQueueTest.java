package org.main;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.parsers.AlchemyPropertiesFileParser;
import org.parsers.Parser;
import org.parsers.ReagentsFileParser;

import java.io.IOException;

public class AlchemyQueueTest {
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
    void saveTo(){

        Potion potion1 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Крыло синей бабочки")
        );

        Potion potion4 = new Potion(
                Reagent.getReagent("Голубой горноцвет"),
                Reagent.getReagent("Крыло синей бабочки"),
                Reagent.getReagent("Морской желудь")
        );

        Potion potion2 = new Potion(
                Reagent.getReagent("Желе пепельного прыгуна"),
                Reagent.getReagent("Вересковое сердце"),
                Reagent.getReagent("Прах вампира")
        );

        AlchemyQueue queue = new AlchemyQueue();
        queue.put(potion4, 20);
        queue.put(potion2, 35);
        queue.put(potion1, 7);

        queue.saveTo("alchemy_results_test.txt");
    }
}
