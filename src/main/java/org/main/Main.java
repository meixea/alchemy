package org.main;

import org.parsers.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    private static final String REAGENTS_FILENAME = "initial_reagents_test.txt";
    private static final String POTION_FILENAME = "initial_potion_test.txt";
    private static final String RESULT_FILENAME = "result.txt";
    private static ThreadPoolExecutor pool;
    public static void main(String[] args) {

//        System.setOut(new PrintStream(System.out, true, Charset.forName("KOI8-R")));

        loadDatabase();

        System.out.printf("%d - find maximum gain potions\n", CalcMode.MAX_GAIN.ordinal());
        System.out.printf("%d - find powerful potion\n", CalcMode.IMBA_POTION.ordinal());
        System.out.printf("%d - exit\n", CalcMode.EXIT.ordinal());

        while( true ) {

            CalcMode mode = getCalculationMode();

            switch (mode) {
                case MAX_GAIN:
                    calculateMaxGain(ReagentsBag.loadInitialReagents(REAGENTS_FILENAME));
                    break;
                case EXIT:
                    return;
            }
        }
    }
    public static CalcMode getCalculationMode(){

        CalcMode[] modes = CalcMode.values();

        int mode = -1;

        Scanner console = new Scanner(System.in);

        while( mode < 0 || mode >= modes.length ) {
            System.out.print("Choose mode: ");
            try {
                mode = Integer.valueOf(console.nextLine());
            }
            catch(NumberFormatException e){
            }
        }

        System.out.println("----------------------");

        return modes[mode];
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
            System.out.println("CalculateMaxGain: " + e.getMessage());
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

            int count;
            try {
                while (!isInterrupted()) {
                    sleep(2000);
                    count =  pool.getActiveCount();
                    if( count > 0 )
                        System.out.printf("Working: %d\n", count);
                }
            }
            catch(InterruptedException e){}
        }
    }
}
