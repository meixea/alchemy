package org.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReagentsBag {
    private static final String VALUES_DELIMITER = "#";

    private HashMap<Reagent, Integer> reagents;

    public ReagentsBag(){
        reagents = new HashMap<>();
    }
    private ReagentsBag(ReagentsBag other){
        reagents = new HashMap<>(other.reagents);
    }

    public void add(Reagent reagent, int amount){
        if(amount > 0)
            reagents.put(reagent, contains(reagent) + amount);
    }
    @Override
    public Object clone(){
        return new ReagentsBag(this);
    }
    public int contains(Reagent reagent){

        Integer result = reagents.get(reagent);

        if(result == null)
            return 0;

        return result;
    }
    public int getPotionsAvailable(Potion potion){
        return potion.getFormula().stream()
                .mapToInt( i -> contains(i) )
                .min()
                .orElse(0);
    }
    public List<Reagent> getReagentsList(){
        return new ArrayList<>(reagents.keySet());
    }
    public static ReagentsBag loadInitialReagents(String filename){

        ReagentsBag result = new ReagentsBag();

        try( BufferedReader reader = new BufferedReader(new FileReader(filename))){

            int skipped = 0;
            while(reader.ready() ){

                String[] parts = reader.readLine().split(VALUES_DELIMITER);

                if( parts.length != 2 )
                    continue;

                Reagent reagent = Reagent.getReagent(parts[0].strip());

                if(reagent == null) {
                    System.out.printf("Not found: %s\n", parts[0].strip());
                    skipped++;
                    continue;
                }

                if(result.contains(reagent) > 0){
                    System.out.printf("Duplicate ignored: %s\n", reagent.getName());
                    skipped++;
                    continue;
                }

                int amount = 0;
                try {
                    amount = Integer.valueOf(parts[1].strip());
                }
                catch(NumberFormatException e){
                    System.out.printf(
                            "Incorrect amount value for %s: %s\n",
                            reagent.getName(),
                            parts[1].strip());
                    skipped++;
                    continue;
                }
                result.add(reagent, amount);

            }

            System.out.printf("Loaded %d reagents. Skipped: %d\n", result.size(), skipped);

        }
        catch( IOException e ){
            System.out.println(e.getMessage());
        }

        return result;
    }
    public void remove(Reagent reagent, int amount){

        int old = contains(reagent);

        if(old < amount)
            throw new NotEnoughAmountException();

        else if(old == amount)
            reagents.remove(reagent);

        else
            reagents.put(reagent, old - amount);
    }
    public int removeAllFor(Potion potion){

        int amount = getPotionsAvailable(potion);

        potion.getFormula().stream()
                .forEach( reagent -> remove(reagent, amount) );

        return amount;
    }

    public int size(){
        return reagents.size();
    }
}
