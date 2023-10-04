package org.main;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedHashMap;

public class AlchemyQueue extends LinkedHashMap<Potion, Integer> {

    private ReagentsBag unused = null;
    public int getPrice(){
        return entrySet().stream()
                .mapToInt( entry -> entry.getKey().getPrice() * entry.getValue() )
                .sum();
    }
    private void savePotion(PrintStream writer, Potion potion, int amount){
        writer.println("-----------------------------------------------");
        for(AlchemyProperty prop : potion.getCommonProperties())
            writer.println(prop);

        writer.printf("------ x%d ------\n", amount);

        for(Reagent reagent : potion.getFormula())
            writer.println(reagent.getName());
        writer.println("-----------------------------------------------");
    }
    public void saveTo(String filename){
        try( PrintStream writer = new PrintStream(filename) ){

            forEach( (potion, amount) -> savePotion(writer, potion, amount));

            writer.printf("---------Total price: %d\n", getPrice());
            writer.println("-----------------------------------------------");
            if(unused != null)
                saveUnused(writer);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    private void saveUnused(PrintStream writer){
        writer.println("--------------- UNUSED REAGENTS ---------------");
        writer.println("-----------------------------------------------");
        unused.getReagentsList().stream()
                .sorted( (r1, r2) -> unused.contains(r2) - unused.contains(r1) )
                .forEach( reagent -> writer.printf(
                        "%-50sx%d\n",
                        reagent.getName(),
                        unused.contains(reagent)
                ));
    }

    public ReagentsBag getUnused() {
        return unused;
    }

    public void setUnused(ReagentsBag unused){
        this.unused = unused;
    }
}
