import Demo.CallbackReceiverPrx;
import com.zeroc.Ice.Current;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

//
// Copyright (c) ZeroC, Inc. All rights reserved.
//


public final class CallbackSenderI implements Demo.CallbackSender

{

    static Map<String,CallbackReceiverPrx> clients = new HashMap<>();

    static Map<String, CallbackReceiverPrx> workers = new HashMap<>();



    public String evaluateOrder(String msg,CallbackReceiverPrx proxy){
        String out = "";

        String [] msgArray = msg.split("-");
        String order = msgArray[msgArray.length-1];
        String hostname = msgArray[msgArray.length-2];

        if(order.startsWith("register as worker")){
            System.out.println("registering worker");
            registerWorker(hostname,proxy);
        }
        else if(order.startsWith("dist_sorter")){
            String [] orderArray = msg.split(":");
            String filename = orderArray[orderArray.length-1];
            registerClient(hostname,proxy);
            int numberOfLines = getTheNumberOfFields(filename);
            TaskManager tm = divideWorkWithWorkers(new TaskManager(proxy,filename,workers),numberOfLines,workers.size());
            startSorting(tm);
        }

        return out;
    }

    private TaskManager divideWorkWithWorkers(TaskManager tm,int numberOfLines, int numWorkers){
        int segmentSize = numberOfLines / numWorkers;
        for (int i = 0; i < numWorkers; i++) {
            int start = i * segmentSize + 1;
            int end = (i + 1) * segmentSize;
            tm.addTask(new Task(start,end));
        }

        return tm;
    }

    private void registerWorker(String hostname,CallbackReceiverPrx proxy){
        workers.put(hostname,proxy);
    }
    private void registerClient(String hostname, CallbackReceiverPrx proxy){
        clients.putIfAbsent(hostname, proxy);
    }

    public static void startSorting(TaskManager taskManager) {
        
        CompletableFuture.runAsync(taskManager);

    }

    private Integer getTheNumberOfFields(String filename){
        String fileName = "/home/swarch/datamining/ejemplo.dat.txt";
        long noOfLines = -1;

        try (Stream<String> fileStream = Files.lines(Paths.get(fileName))) {
            noOfLines = (int) fileStream.count();
            return (int) noOfLines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    public void initiateCallback(CallbackReceiverPrx proxy, String message, Current current) {

    }

    @Override
    public void sendMessage(CallbackReceiverPrx proxy, String msg, Current current) {
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

    @Override
    public void makeWorker(CallbackReceiverPrx proxy, String msg, Current current) {

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

