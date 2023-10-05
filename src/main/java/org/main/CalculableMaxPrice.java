package org.main;

import org.combinators.Combinator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

public class CalculableMaxPrice implements Callable<AlchemyQueue> {

    private final ExecutorService pool;
    private final ReagentsBag bag;
    private final AlchemyQueue previous;
    public CalculableMaxPrice(ExecutorService pool, ReagentsBag bag, AlchemyQueue previousQueue){
        this.pool = pool;
        this.bag = bag;
        this.previous = previousQueue;
    }
    @Override
    public AlchemyQueue call(){

        Potion potion2 = getMaxPricePotion(bag, 2);

        if (potion2.getPrice() == 0) {
            previous.setUnused(bag);
            return previous;
        }

        List<FutureTask> variants = new ArrayList<>();

        variants.add(getNewVariant(potion2));

        Potion potion3 = getMaxPricePotion(bag, 3);
        if(potion2.compareTo(potion3) > 0)
            variants.add(getNewVariant(potion3));

        AlchemyQueue bestVariant = variants.stream()
                .map(variant -> {
                    try {
                        return ((AlchemyQueue) variant.get());
                    }
                    catch(InterruptedException | ExecutionException e){
                        System.out.println("Exception: " + e.getMessage());
                    }
                    return new AlchemyQueue();
                })
                .max(Comparator.comparingInt(AlchemyQueue::getPrice))
                .get();

        previous.setUnused(bestVariant.getUnused());

        bestVariant.forEach( (potion, amount) -> previous.put(potion, amount));

        return previous;
    }
    private Potion getMaxPricePotion(ReagentsBag bag, int numberReagents){

        return new PotionCombinator(bag, numberReagents).stream()
                .max(Comparator.comparingInt(Potion::getPrice))
                .orElse(new Potion());

    }
    private FutureTask<AlchemyQueue> getNewVariant(Potion potion){

        ReagentsBag newBag = (ReagentsBag) bag.clone();

        AlchemyQueue result = new AlchemyQueue();
        result.put(potion, newBag.removeAllFor(potion));

        FutureTask<AlchemyQueue> newTask = new FutureTask<>(new CalculableMaxPrice(pool, newBag, result));
        pool.execute(newTask);

        return newTask;
    }

}
