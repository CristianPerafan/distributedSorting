import Demo.CallbackReceiverPrx;
import com.zeroc.Ice.Current;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

//
// Copyright (c) ZeroC, Inc. All rights reserved.
//


public final class CallbackSenderI implements Demo.CallbackSender
{

    static Map<String,CallbackReceiverPrx> clients = new HashMap<>();

    @Override
    public void sendMessage(CallbackReceiverPrx proxy, String msg,com.zeroc.Ice.Current current)
    {

        System.out.println("initiating callback");

        try
        {
            String out = evaluateOrder(msg,proxy);
            proxy.receiveMessage(out);


        }
        catch(com.zeroc.Ice.LocalException ex)
        {
            ex.printStackTrace();
        }

    }

    public String evaluateOrder(String msg,CallbackReceiverPrx proxy){
        StringBuilder out = new StringBuilder("");

        String [] msgArray = msg.split("-");
        String order = msgArray[msgArray.length-1];
        String hostname = msgArray[msgArray.length-2];
        if(order.startsWith("dist_sorter")){
            String [] orderArray = order.split(":");
            String file = orderArray[orderArray.length-1];
            System.out.println(file);
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
    public static boolean isPositiveInteger(int n) {
        return n > 0;
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

    public static CompletableFuture<Void> sendPrimeFactor(int n, String hostname) {
        return CompletableFuture.runAsync(() -> {
            StringBuilder primeFactors = primeFactorsMethod(n);

            CallbackReceiverPrx prx = clients.get(hostname);
            prx.receiveMessage(primeFactors.toString());
        });
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
    public Boolean validateClientIsRegistered(String hostname){
        return clients.containsKey(hostname);
    }

    public String broadcastMessage(String order,String hostname){
        String out = "";

        String[] orderArray = order.split(" ");

        for (CallbackReceiverPrx prx : clients.values()) {
            prx.receiveMessage("(BROADCAST) from "+hostname+" :"+orderArray[orderArray.length-1]);
        }

        return out;
    }

    public  String sendMessage(String order,String hostname){
        String out = "";

        String[] orderArray = order.split(" ");


        if(clients.containsKey(orderArray[1])){

            CallbackReceiverPrx prx = clients.get(orderArray[1]);
            prx.receiveMessage("from "+hostname+" :"+orderArray[orderArray.length-1]);
            out = "[Message send to "+orderArray[1]+"]";

        }
        else{
            out = "[Error sending message]";
        }


        return out;
    }

    public static boolean canConvertToInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String listClients() {
        StringBuilder hostnameString = new StringBuilder();

        for (String hostname : clients.keySet()) {
            hostnameString.append(hostname).append(", ");
        }

        if (hostnameString.length() > 0) {
            hostnameString.delete(hostnameString.length() - 2, hostnameString.length());
        }

        return hostnameString.toString();
    }

    @Override
    public void shutdown(com.zeroc.Ice.Current current)
    {
        System.out.println("Shutting down...");
        try
        {
            current.adapter.getCommunicator().shutdown();
        }
        catch(com.zeroc.Ice.LocalException ex)
        {
            ex.printStackTrace();
        }
    }
}
