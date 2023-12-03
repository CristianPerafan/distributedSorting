import Demo.CallbackReceiverPrx;
import com.zeroc.Ice.Current;

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

    static Map<String,CallbackReceiverPrx> workers = new HashMap<>();


    public String evaluateOrder(String msg,CallbackReceiverPrx proxy){
        String out = "";

        String [] msgArray = msg.split("-");
        String order = msgArray[msgArray.length-1];
        String hostname = msgArray[msgArray.length-2];

        if(order.startsWith("register")){
            registerWorker(hostname,proxy);
        }


        return out;
    }

    private void registerWorker(String hostname,CallbackReceiverPrx proxy){
        workers.put(hostname,proxy);
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

