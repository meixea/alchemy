package org.parsers;

import org.main.AlchemyProperty;
import org.main.FormatUnknownException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AlchemyPropertiesFileParser extends FileParser<AlchemyProperty> {
    static final String PROPERTIES_FILENAME = "properties.txt";
    public AlchemyPropertiesFileParser(){
        super(PROPERTIES_FILENAME);
    }
    @Override
    public List<AlchemyProperty> parse() throws IOException {

        List<AlchemyProperty> result = new ArrayList<>();
        int skipped = 0;

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){

            while(reader.ready()) {

                String line = reader.readLine();

                try {
                    result.add(AlchemyProperty.valueOf(line));
                }
                catch(FormatUnknownException e){
                    skipped++;
                    System.out.printf("Skipped: %s\n", line);
                }

            }

            System.out.printf("Scanning reagents: %d finded, %d skipped\n", result.size(), skipped);

        }

        return result;
    }
    @Override
    public void save(Collection<AlchemyProperty> data) throws IOException {

        try(PrintWriter writer = new PrintWriter(fileName)) {

            for (var prop : data)
                writer.println(prop);

        }
    }
}
