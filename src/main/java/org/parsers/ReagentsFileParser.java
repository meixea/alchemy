package org.parsers;

import org.main.Reagent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReagentsFileParser extends FileParser<Reagent> {
    static final String REAGENTS_FILENAME = "reagents.txt";
    public ReagentsFileParser(){
        super(REAGENTS_FILENAME);
    }
    @Override
    public List<Reagent> parse() throws IOException {

        List<Reagent> result = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){

            while(reader.ready()) {

                String line = reader.readLine();

                result.add(Reagent.valueOf(line));

            }

            System.out.printf("Scanning properties: %d finded\n", result.size());

        }

        return result;
    }
    @Override
    public void save(Collection<Reagent> data) throws IOException {

        try(PrintWriter writer = new PrintWriter(fileName)) {

            for (var reagent : data)
                writer.println(reagent);

        }
    }
}
