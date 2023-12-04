//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
// Copyright (c) ZeroC, Inc. All rights reserved.
//


import Demo.CallbackReceiverPrx;
import Demo.CallbackSenderPrx;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Worker
{
    public static void main(String[] args)
    {

        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "worker.cfg", extraArgs))
        {

            if(!extraArgs.isEmpty())
            {
                System.err.println("too many arguments");

            }
            else
            {
                run(communicator);
            }
        }


    }

    private static void run(com.zeroc.Ice.Communicator communicator)
    {
        Demo.CallbackSenderPrx sender = Demo.CallbackSenderPrx.checkedCast(
                communicator.propertyToProxy("CallbackSender.Proxy")).ice_twoway().ice_timeout(-1).ice_secure(false);
        if(sender == null)
        {
            System.err.println("invalid proxy");

        }
        else {
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Worker");
            adapter.add(new CallbackReceiverI(), com.zeroc.Ice.Util.stringToIdentity("callbackReceiver"));
            adapter.activate();

            Demo.CallbackReceiverPrx receiver =
                    Demo.CallbackReceiverPrx.uncheckedCast(adapter.createProxy(
                            com.zeroc.Ice.Util.stringToIdentity("callbackReceiver")));


            //Get system ids
            String username = System.getProperty("user.name");
            String hostname = "";

            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            String message = username + "-" + hostname + "-" + "register as worker";


            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            serviceMaster(message, sender, receiver);

            while(true){

            }

        }
    }

    public static void serviceMaster(String msg, CallbackSenderPrx sender, CallbackReceiverPrx receiver){
        sender.sendMessage(receiver,msg);
    }



    public static void asyncTask(String msg, CallbackSenderPrx sender, CallbackReceiverPrx receiver) {
        CompletableFuture.runAsync(() -> {
            sender.sendMessage(receiver, msg);
        });
    }





}
