package org.main;

import org.combinators.Combinator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class ImbaPotion {
    private AlchemyProperty target;
    private final String resultFilename;

    private List<FutureTask> tasks = new ArrayList<>();
    public ImbaPotion(String initialFilename, String resultFilename){

        try(BufferedReader reader = new BufferedReader(new FileReader(initialFilename))) {
            target = AlchemyProperty.valueOf(reader.readLine());
        }
        catch(IOException e){
            e.printStackTrace();
        }

        this.resultFilename = resultFilename;
    }
    public void calculate(){

        List<Reagent> bag = getReagentsWithTarget();

        tasks.clear();

        ExecutorService pool = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Integer.MAX_VALUE,
                2,
                TimeUnit.DAYS,
                new ArrayBlockingQueue<>(1000)
        );

        new Combinator(bag.size(), 2).stream()
                .forEach( index -> {

                    Calculable task = new Calculable(
                            bag.get(index[0]),
                            bag.get(index[1]),
                            target.getType()
                    );

                    FutureTask<List<Potion>> futureTask = new FutureTask<>(task);
                    pool.execute(futureTask);
                    tasks.add(futureTask);
                });

        List<Potion> resultPotions = new ArrayList<>();

        tasks.stream()
                .forEach(task -> {
                    try{
                        resultPotions.addAll((List<Potion>) task.get());
                    }
                    catch(InterruptedException | ExecutionException e){
                        e.printStackTrace();
                        System.exit(1);
                    }
                });

        pool.shutdown();

        resultPotions.sort( (p1, p2) -> {

            int effectsSizing = p2.getCommonProperties().size() - p1.getCommonProperties().size();
            if(effectsSizing != 0)
                return effectsSizing;

            return p1.size() - p2.size();
        } );

        savePotions(resultPotions);
    }
    private List<Reagent> getReagentsWithTarget(){

        List<Reagent> result = new ArrayList<>();

        Reagent.reagents.stream()
                .filter( reagent -> reagent.hasProperty(target) )
                .forEach( reagent -> result.add(reagent));

        return result;
    }

    public AlchemyProperty getTarget() {
        return target;
    }
    private void savePotions(List<Potion> potions){
        try(PrintWriter writer = new PrintWriter(resultFilename)){
            potions.stream()
                    .forEach(potion -> {
                        writer.println("-------------------------------");
                        for(AlchemyProperty prop : potion.getCommonProperties())
                            writer.println(prop);
                        writer.println("======");
                        for(Reagent reg : potion.getFormula())
                            writer.println(reg.getName());
                        writer.println("-------------------------------");
                    });
        }
        catch( FileNotFoundException e ){
            e.printStackTrace();
        }
    }
    private static class Calculable implements Callable<List<Potion>> {

        Reagent r1;
        Reagent r2;
        private AlchemyType potionType;
        public Calculable(Reagent r1, Reagent r2, AlchemyType potionType){
            this.r1 = r1;
            this.r2 = r2;
            this.potionType = potionType;
        }
        @Override
        public List<Potion> call(){

            List<Potion> result = new ArrayList<>();

            Potion basePotion = new Potion(r1, r2);
            if(checkPotion(basePotion, 1))
                result.add(basePotion);

            int baseSize = basePotion.getCommonProperties().size();

            Reagent.reagents.stream()
                    .filter( reagent -> (!reagent.equals(r1) && !reagent.equals(r2)) )
                    .map(reagent -> new Potion(r1, r2, reagent) )
                    .filter(potion -> checkPotion(potion, baseSize) )
                    .forEach(potion -> result.add(potion));

            return result;
        }
        private boolean checkPotion(Potion potion, int baseSize){

            Set<AlchemyProperty> commons = potion.getCommonProperties();

            if(commons.size() <= baseSize)
                return false;

            for(AlchemyProperty prop : commons)
                if( prop.getType() != potionType)
                    return false;

            return true;
        }
    }
}
