package org.main;

import org.parsers.Parser;

import org.junit.jupiter.api.Test;
import org.parsers.AlchemyPropertiesFileParser;
import org.parsers.FileParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class AlchemyPropertyTest {
    @Test
    public void equals(){
        AlchemyProperty p1 = new AlchemyProperty("Здоровье", AlchemyType.POSITIVE, 100);
        AlchemyProperty p2 = new AlchemyProperty("Здоровье", AlchemyType.valueOf("POSITIVE"), 100);
        assertEquals(p1, p2);

        p2 = new AlchemyProperty("Здоро2вье", AlchemyType.NEGATIVE, 100);
        assertNotEquals(p1, p2);

        p2 = new AlchemyProperty("Здоровье", AlchemyType.NEGATIVE, 200);
        assertNotEquals(p1, p2);
    }
    @Test void valueOf(){

        AlchemyProperty expected = new AlchemyProperty("Здоровье", AlchemyType.POSITIVE, 100);

        AlchemyProperty actual;

        try {
            actual = AlchemyProperty.valueOf(expected.toString());
            assertEquals(expected, actual);
            assertEquals(expected.getPrice(), actual.getPrice());
        }
        catch(FormatUnknownException e){
            throw new RuntimeException(e);
        }


        expected = new AlchemyProperty("Здоровье", AlchemyType.NEGATIVE, 1400);

        try{
            actual = AlchemyProperty.valueOf(expected.toString());
            assertEquals(expected, actual);
            assertEquals(expected.getPrice(), actual.getPrice());
        }
        catch(FormatUnknownException e){
            throw new RuntimeException(e);
        }

        expected.setPrice(1000);
        assertEquals(expected, actual);
        assertNotEquals(expected.getPrice(), actual.getPrice());

        expected = new AlchemyProperty("Здоро2ье", AlchemyType.NEGATIVE, 1400);

        assertNotEquals(expected, actual);
        assertEquals(expected.getPrice(), actual.getPrice());

        assertThrows(FormatUnknownException.class, () ->{
            AlchemyProperty.valueOf("POSITIVE");
        });

        assertThrows(FormatUnknownException.class, () ->{
            AlchemyProperty.valueOf("POSITIVE: SDFLKSDFJ  fdslj   : 10l00\n");
        });
    }
    @Test
    void findProperty(){

        Parser fileParser = new AlchemyPropertiesFileParser();

        try {
            AlchemyProperty.properties.addAll(fileParser.parse());
        }
        catch( IOException e ){
            System.out.println(e.getMessage());
        }

        assertEquals(null, AlchemyProperty.findProperty("sdlkjf;lsejk"));

        AlchemyProperty expected = new AlchemyProperty(
                "Повышение переносимого веса",
                AlchemyType.POSITIVE, 0);

        assertEquals(expected, AlchemyProperty.findProperty("Повышение переносимого веса"));
        assertEquals(expected, AlchemyProperty.findProperty("повышение переносимого веса"));
    }
}
