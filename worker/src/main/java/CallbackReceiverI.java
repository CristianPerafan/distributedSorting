import com.zeroc.Ice.Current;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CallbackReceiverI implements Demo.CallbackReceiver {

    @Override
    public void receiveMessage(String msg, Current current) {
        System.out.println(msg);
    }

    private  MergeSort<ComparableClass> mergeSort = new MergeSort<ComparableClass>();
    private  List<ComparableClass>  sortedList;


    @Override
    public void startWorker(int from, int to, Current current) {
        System.out.println("Starting worker from " + from + " to " + to);

        //readLinesFromFile("C:/Users/Cristian Perafan/Downloads/ejemplo.dat.txt", from, to);
        readLinesFromFile("/home/swarch/datamining/ejemplo.dat.txt",from, to);
    }



    @Override
    public String getHalfAndRemove(Current current) {
        int half = 50000;
        if(sortedList.size() < half){

            List<ComparableClass> halfList = sortedList.subList(0, sortedList.size());
            sortedList = new ArrayList<>();
            
            return halfList.toString();
        }
        else{
            List<ComparableClass> halfList = sortedList.subList(0, half);
            sortedList = sortedList.subList(half, sortedList.size());
            return halfList.toString();
        }

    }

    @Override
    public int verifyLength(Current current) {
        System.out.println(sortedList.size());
        return sortedList.size();
    }

    private void readLinesFromFile(String filePath, int from, int to){
        long startTime = System.currentTimeMillis();

        List<ComparableClass> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                if (lineNumber >= from && lineNumber <= to) {
                    dataList.add(new ComparableClass(line));
                }

                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("The file was read");

        sortedList = mergeSort.mergeSort(dataList);

        System.out.println("The list was ordered");

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("File reading time: " + elapsedTime + " milliseconds");

    }


}
