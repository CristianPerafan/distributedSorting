import Demo.RequestCanceledException;
import com.zeroc.Ice.Current;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletionStage;


public class PrinterI implements Demo.Printer {


    private WorkQueue workQueue;

    public PrinterI(WorkQueue workQueue) {
        this.workQueue = workQueue;
    }




    @Override
    public CompletionStage<String> sendMsgAsyncAsync(String msg, Current current) throws RequestCanceledException {
        java.util.concurrent.CompletableFuture<String> r = new java.util.concurrent.CompletableFuture<>();
        workQueue.add(r,msg);

        return r;
    }

    @Override
    public void shutdown(com.zeroc.Ice.Current current)
    {
        System.out.println("Shutting down...");

        workQueue.destroy();
        current.adapter.getCommunicator().shutdown();
    }




}