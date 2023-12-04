import Demo.CallbackReceiverPrx;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.LocalException;

import java.io.*;
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

    static MergeSort<ComparableClass> mergeSort = new MergeSort<ComparableClass>();

    public static final String BASE_PATH = "/home/swarch/datamining/";


    public String evaluateOrder(String msg,CallbackReceiverPrx proxy) throws IOException {
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

            if(!verifyFileExits(filename)){
                out = "File does not exist";
            }else{
                if(verifyFileWasSorted(filename)) {
                    out = "File was already sorted";
                }else{
                    if(workers.size() <= 1){
                        monolithicSort(filename,proxy);
                        out = "The file was sorted monolithic by one node";
                    } else{
                        int numberOfLines = getTheNumberOfFields(filename);
                        TaskManager tm = divideWorkWithWorkers(new TaskManager(proxy,filename,BASE_PATH,workers),numberOfLines,workers.size());
                        startSorting(tm);
                        out = "The file was sorted  by "+workers.size()+" nodes";
                    }
                }
            }


        }
        else{
            out = "Order not recognized";
        }

        return out;
    }

    private static void monolithicSort(String filename,CallbackReceiverPrx proxy) throws IOException {

        long startTime = System.currentTimeMillis();

        List<ComparableClass> dataList = readFile(BASE_PATH+filename);

        dataList = mergeSort.mergeSort(dataList);

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("Sorting time of " +filename+": "+ elapsedTime + " milliseconds");
        proxy.receiveMessage("Sorting time of " +filename+": "+ elapsedTime + " milliseconds");
        saveSortedData(dataList,filename);

    }

    private static void saveSortedData(List<ComparableClass> result, String filename) throws IOException {
        File file = new File(BASE_PATH+"sorted."+filename);

        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter writer = new FileWriter(file);

        for(ComparableClass cc : result){
            writer.write(cc.getData()+"\n");
        }
        writer.close();
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

    private static boolean verifyFileExits(String filename){
        File f = new File(BASE_PATH+filename);

        return f.exists() && !f.isDirectory();
    }

    private static boolean verifyFileWasSorted(String filename){
        File f = new File(BASE_PATH+"sorted."+filename);
        return f.exists() && !f.isDirectory();
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
        String fileName = BASE_PATH+filename;
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
        catch(LocalException | IOException ex)
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

