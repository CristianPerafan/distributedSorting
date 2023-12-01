import Demo.RequestCanceledException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class WorkQueue extends Thread{
    //Attributes
    private  boolean done = false;
    private  java.util.LinkedList<FutureEntry> futures = new java.util.LinkedList<>();

    class FutureEntry
    {
        public String getOrder() {
            return order;
        }

        /*CompletableFuture: is a class in Java that is used to work with asynchronous tasks and promises.
                It allows you to execute operations asynchronously and manage the result when it is available.
                */
        String order;
        CompletableFuture<String> future;

    }

    @Override
    public synchronized void run()
    {


        while (!done)
        {
            if(futures.size()==0){
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if(!done && futures.size() != 0){
                FutureEntry entry = futures.getFirst();

                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if(!done){

                    futures.removeFirst();

                    String [] msg = entry.getOrder().split(":");
                    showClients(msg);

                    String order =msg[msg.length-1];

                    String out = evaluateOrder(order);

                    System.out.println(out);

                    entry.future.complete((String)out);

                }
            }
        }

        for (FutureEntry p : futures){
            p.future.completeExceptionally(new RequestCanceledException());
        }
    }

    public String evaluateOrder(String order){
        //Result
        StringBuilder out = new StringBuilder("");

        System.out.println(order);

        if(canConvertToInt(order)){

            int n = convertStringToInt(order);

            if (isPositiveInteger(n)) {
                StringBuilder primeFactors = primeFactorsMethod(n);
                out.append("[ ").append(primeFactors).append(" ]");
            } else {
                out = new StringBuilder("It is not a positive integer!!!");
            }

        }else if(order.startsWith("listifs")){
            StringBuilder logicalInterfaces = runCommandInConsole("ifconfig -a");
            out.append("[\n ").append(logicalInterfaces).append(" \n]");

        }else if(order.startsWith("listports")){
            String ip = order.substring(9);
            StringBuilder portsAndServices = runCommandInConsole("nmap "+ip);
            out.append("[\n ").append(portsAndServices).append(" \n]");

        } else if(order.startsWith("!")){
            String command = order.substring(1);
            StringBuilder commandResult = runCommandInConsole(command);

            if(String.valueOf(commandResult).equals("")){
                out.append("Invalid command!!!");
            }else{
                out.append("[\n ").append(commandResult).append(" \n]");
                out.append(String.valueOf(commandResult));
            }
        }
        else{
            out = new StringBuilder("Invalid message!!!");
        }
        return out.toString();
    }

    public static StringBuilder runCommandInConsole(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();
            reader.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return output;
    }

    public static int convertStringToInt(String input) {
        int result = 0;

        try {
            result = Integer.parseInt(input);

        }catch (NumberFormatException e){
            e.printStackTrace();
        }


        return result;
    }
    public static StringBuilder primeFactorsMethod(int n) {
        StringBuilder result = new StringBuilder();

        result.append(n).append("|");

        for (int i = 2; i <= n; i++) {
            if (isPrime(i) == 1) {
                int x = n;
                while (x % i == 0) {
                    result.append(i).append(" ");
                    x /= i;
                }
            }
        }

        return result;
    }

    public static int isPrime(int num) {
        if (num < 2) {
            return 0; // No es primo
        }

        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return 0; // No es primo
            }
        }

        return 1; // Es primo
    }
    public static boolean isPositiveInteger(int n) {
        return n > 0;
    }

    public static boolean canConvertToInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public synchronized void add(java.util.concurrent.CompletableFuture<String> future,String msg)
    {

        if(!done)
        {
            //
            // Add the work item.
            //
            FutureEntry entry = new FutureEntry();
            entry.order = msg;
            entry.future = future;


            if(futures.size() == 0)
            {
                notify();
            }


            futures.add(entry);
        }
        else
        {

            future.completeExceptionally(new RequestCanceledException());
        }
    }

    public  synchronized void destroy(){
        done = true;
        notify();
    }

    public void showClients(String [] msg){
        StringBuilder clients = new StringBuilder("");
        clients.append("[ username: ");

        for(int i = 0;i<msg.length;i++){
            if(i == 0){
                clients.append(msg[i]).append("/localhost:").append(msg[i+1]);
            }
        }
        clients.append("]");
        System.out.println(clients);
    }

}

