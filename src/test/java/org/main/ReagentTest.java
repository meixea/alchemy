package org.main;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.parsers.AlchemyPropertiesFileParser;
import org.parsers.FileParser;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ReagentTest {

    @BeforeAll
    static void setup(){

        FileParser fileParser = new AlchemyPropertiesFileParser();

        try {
            AlchemyProperty.properties.addAll(fileParser.parse());
        }
        catch( IOException e ){
            System.out.println(e.getMessage());
        }

    }

    @Test
    void equals(){
        Reagent p1 = new Reagent("Реагент1");
        Reagent p2 = new Reagent("Реагент1");
        assertEquals(p1, p2);

        p2.addProperty(new AlchemyProperty("health", AlchemyType.POSITIVE, 600));
        assertEquals(p1, p2);

        String p3 = "Реагент1";
        assertEquals(p1, p3);
    }
    @Test
    void tostring(){

        Reagent p2 = new Reagent("Реагент1");
        assertEquals(p2.toString(), "Реагент1");

        p2.addProperty(new AlchemyProperty("health", AlchemyType.POSITIVE, 600));
        assertEquals(p2.toString(), "Реагент1#health");

        p2.addProperty(new AlchemyProperty("intellect", AlchemyType.POSITIVE, 600));
        assertEquals(p2.toString(), "Реагент1#health#intellect");

        p2.addProperty(new AlchemyProperty("stamina", AlchemyType.NEGATIVE, 630));
        assertEquals(p2.toString(), "Реагент1#health#intellect#stamina");

    }
    @Test
    void valueOf(){
        String data = "Сморчок#Опустошение запаса сил#Затяжной урон запасу сил#Повышение навыка: разрушение";
        Reagent test = Reagent.valueOf(data);
        assertEquals(test.toString(), data);
    }
}
