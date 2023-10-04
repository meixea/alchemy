package org.main;

import org.parsers.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    private static final String REAGENTS_FILENAME = "initial_reagents_test.txt";
    private static final String POTION_FILENAME = "initial_potion_test.txt";
    private static final String RESULT_FILENAME = "result.txt";
    private static ThreadPoolExecutor pool;
    public static void main(String[] args) {

        loadDatabase();

        CalcMode mode = getCalculationMode();

        switch(mode) {
            case MAX_GAIN:
                calculateMaxGain(ReagentsBag.loadInitialReagents(REAGENTS_FILENAME));
                break;
        }

    }
    public static CalcMode getCalculationMode(){

        HashMap<String, CalcMode> modes = new HashMap<>();
        modes.put("1", CalcMode.MAX_GAIN);
        modes.put("2", CalcMode.IMBA_POTION);

        System.out.println("1 - find maximum gain potions");
        System.out.println("2 - find powerful potion");

        String mode = null;

        try(Scanner console = new Scanner(System.in)){
            while( !modes.containsKey(mode) ) {
                System.out.print("Choose mode: ");
                mode = console.nextLine();
            }
        }

        System.out.println("----------------------");

        return modes.get(mode);
    }

    public static void loadDatabase(){

        try{

            Parser parser = new AlchemyPropertiesFileParser();
            AlchemyProperty.properties.addAll(parser.parse());

            parser = new ReagentsFileParser();
            Reagent.reagents.addAll(parser.parse());

        }
        catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("----------------------");

    }
    public static void calculateMaxGain(ReagentsBag reagentsBag){

        pool = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Integer.MAX_VALUE,
                2,
                TimeUnit.DAYS,
//                new ArrayBlockingQueue<>(20)
                new SynchronousQueue<>()
        );

        new ControllerDaemon().start();

        FutureTask work = new FutureTask<>(new CalculableMaxPrice(pool, reagentsBag, new AlchemyQueue()));

        try {
            pool.execute(work);
            AlchemyQueue result = (AlchemyQueue) work.get();
            result.saveTo(RESULT_FILENAME);
        }
        catch(InterruptedException | ExecutionException e){
            System.out.println("Exception: " + e.getMessage());
        }
        finally {
            pool.shutdown();
        }
    }
    private static class ControllerDaemon extends Thread {
        public ControllerDaemon(){
            super();
            setDaemon(true);
        }
        @Override
        public void run(){
            System.out.println("Start monitoring");
            try {
                while (!isInterrupted()) {
                    sleep(200);
                    System.out.printf("Working: %d\n", pool.getActiveCount());
                }
            }
            catch(InterruptedException e){}
            System.out.println("Finish monitoring");
        }
    }
}
