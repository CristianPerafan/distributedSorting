import Demo.CallbackReceiverPrx;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.zeroc.Ice.Value;

public class TaskManager implements Runnable {

    private CallbackReceiverPrx client;

    private String filename;

    private Queue<Task> tasks = new LinkedList<>();

    private final Map<String, CallbackReceiverPrx> workers;


    public TaskManager(CallbackReceiverPrx client, String filename, Map<String, CallbackReceiverPrx> workers) {
        this.client = client;
        this.filename = filename;
        this.workers = workers;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }


    @Override
    public void run() {

        long startTime = System.nanoTime();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Map.Entry<String, CallbackReceiverPrx> entry : workers.entrySet()) {
            CallbackReceiverPrx worker = entry.getValue();
            Task task = tasks.poll();

            if (task != null) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    worker.startWorker(task.getFrom(), task.getTo());
                });

                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        System.out.println("All tasks are completed");

        List<Container> containers = new ArrayList<>();
        StringBuilder result = new StringBuilder();
    
        boolean areAllWorkersEmpty;

        do {
            areAllWorkersEmpty = true;

            for (Map.Entry<String, CallbackReceiverPrx> entry : workers.entrySet()) {
                System.out.println("Getting half from " + entry.getKey());

                String elements = entry.getValue().getHalfAndRemove();

                containers.add(new Container(entry.getValue(), elements));

                if (entry.getValue().verifyLength() != 0) {
                    areAllWorkersEmpty = false;
                    break;
                }
            }

            System.out.println("All workers are empty: " + areAllWorkersEmpty);

            PriorityQueue<Pair<String, Container>> priorityQueue = new PriorityQueue<>(Comparator.comparing(Pair::getFirst));


            for (Container container : containers) {
                
                if (!container.getQueue().isEmpty()) {
                    String elementToAdd = container.getQueue().poll();

                    elementToAdd = elementToAdd.trim();
                    elementToAdd = elementToAdd.replaceAll("\\s+", "");
                    priorityQueue.add(new Pair<>(elementToAdd, container));
                }
            }

            while (!priorityQueue.isEmpty()) {
                Pair<String, Container> pair = priorityQueue.poll();
                Container container = pair.getSecond();
                String elementToSave = pair.getFirst();

                result.append(elementToSave).append("\n");
            
                if (!container.getQueue().isEmpty()) {
                    String element = container.getQueue().poll();
                    priorityQueue.add(new Pair<>(element, container));
                }
            }

            containers.clear();
        } while (!areAllWorkersEmpty);

        System.out.println("All elements are sorted");
        try {
            saveSortedData(result.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long endTime = System.nanoTime();

        long durationInNano = (endTime - startTime);  
        long durationInMillis = durationInNano / 1_000_000;  

        System.out.println("Tiempo de ejecución en milisegundos: " + durationInMillis);

    }

    private void saveSortedData(String result) throws IOException {
        //File file = new File("C:/Users/Cristian Perafan/Desktop/sortedData.txt");

        File file = new File("/home/swarch/datamining/sorted.dat.txt");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter writer = new FileWriter(file);
        writer.write(result);
        writer.close();
    }

}
