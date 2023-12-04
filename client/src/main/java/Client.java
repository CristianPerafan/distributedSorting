//
// Copyright (c) ZeroC, Inc. All rights reserved.
//


import Demo.CallbackReceiverPrx;
import Demo.CallbackSenderPrx;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Client
{
    public static void main(String[] args)
    {

        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "client.cfg", extraArgs))
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
        else{
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Client");
            adapter.add(new CallbackReceiverI(), com.zeroc.Ice.Util.stringToIdentity("callbackReceiver"));
            adapter.activate();

            Demo.CallbackReceiverPrx receiver =
                    Demo.CallbackReceiverPrx.uncheckedCast(adapter.createProxy(
                            com.zeroc.Ice.Util.stringToIdentity("callbackReceiver")));



            Scanner sc = new Scanner(System.in);
            String line = null;

            //Get system ids
            String username = System.getProperty("user.name");
            String hostname = "";

            try {
                hostname = InetAddress.getLocalHost().getHostName();
            }catch (UnknownHostException e){
                e.printStackTrace();
            }

            while(true){
                menu();


                line = sc.nextLine();

                if(line.toUpperCase().equals("EXIT")){
                    sender.shutdown();
                    break;
                }
                else{
                    String message = username+"-"+hostname+"-"+line;

                    asyncTask(message,sender,receiver);

                }


            }

        }





    }

    public static void menu(){
        System.out.println("==== MENU ====\n"+
                "(1) dist_sorter:<filename>");
    }

    public static void asyncTask(String msg, CallbackSenderPrx sender, CallbackReceiverPrx receiver) {
        /*
        CompletableFuture.runAsync(() -> {
            sender.sendMessage(receiver, msg);

        });
        */
        sender.sendMessage(receiver, msg);
    }





}