import Demo.CallbackReceiverPrx;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

//
// Copyright (c) ZeroC, Inc. All rights reserved.
//


public final class CallbackSenderI implements Demo.CallbackSender
{

    static Map<String,CallbackReceiverPrx> clients = new HashMap<>();

    static MergeSort<ComparableClass> mergeSort = new MergeSort<ComparableClass>();

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
        String out = "";

        String [] msgArray = msg.split("-");
        String order = msgArray[msgArray.length-1];
        String hostname = msgArray[msgArray.length-2];
        if(order.startsWith("dist_sorter")){
            String [] orderArray = order.split(":");
            String file = orderArray[orderArray.length-1];
            initializeSorting(file);
        }


        return out;
    }

    public static void initializeSorting(String filename){

        long startTime = System.currentTimeMillis();

        String filePath = "C:/Users/Cristian Perafan/Downloads/ejemplo.dat.txt";

        List<ComparableClass> dataList = readFile(filePath);

        dataList = mergeSort.mergeSort(dataList);

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("File reading time: " + elapsedTime + " milliseconds");

    }

    private static List<ComparableClass> readFile(String filePath){


        List<ComparableClass> dataList = new ArrayList<>();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while((line = reader.readLine()) != null){
                dataList.add(new ComparableClass(line));
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return dataList;
    }





    public static CompletableFuture<Void> sendPrimeFactor(int n, String hostname) {
        return CompletableFuture.runAsync(() -> {

            CallbackReceiverPrx prx = clients.get(hostname);
            prx.receiveMessage("");
        });
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
