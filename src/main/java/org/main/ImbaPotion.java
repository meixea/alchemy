package org.main;

import org.combinators.Combinator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class ImbaPotion {

    private final String TYPES_DELIMITER = "------------- BLACK LIST --------------";
    private List<AlchemyProperty> whiteList = new ArrayList<>();
    private List<Reagent> blackList = new ArrayList<>();
    private final String resultFilename;

    private List<FutureTask> tasks = new ArrayList<>();
    public ImbaPotion(String initialFilename, String resultFilename){

        loadFilters(initialFilename);

        this.resultFilename = resultFilename;
    }
    private void loadFilters(String fileName){

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            String line;
            while( true ) {

                line = reader.readLine();
                if( TYPES_DELIMITER.equals(line) )
                    break;

                whiteList.add(AlchemyProperty.valueOf(line));
            }

            while( reader.ready() ){
                line = reader.readLine();
                Reagent reagent = Reagent.getReagent(line);
                if(reagent != null)
                    blackList.add(reagent);
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }

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
                            bag.get(index[1])
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
    List<Reagent> getReagentsWithTarget(){

        List<Reagent> result = new ArrayList<>();

        AlchemyProperty mainProperty = whiteList.get(0);
        Reagent.reagents.stream()
                .filter( reagent -> !(blackList.contains(reagent)) )
                .filter( reagent -> reagent.hasProperty(mainProperty) )
                .forEach( reagent -> result.add(reagent));

//        for(Reagent reagent : blackList )
//            result.remove(reagent);

        return result;
    }

    public List<Reagent> getBlackList() {
        return blackList;
    }
    public List<AlchemyProperty> getWhiteList() {
        return whiteList;
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
    private class Calculable implements Callable<List<Potion>> {

        Reagent r1;
        Reagent r2;
        private AlchemyType potionType;
        public Calculable(Reagent r1, Reagent r2){
            this.r1 = r1;
            this.r2 = r2;
            this.potionType = whiteList.get(0).getType();
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
                    .filter( reagent -> !blackList.contains(reagent) )
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

            for(AlchemyProperty property : whiteList)
                if( !commons.contains(property) )
                    return false;

            return true;
        }
    }
}
