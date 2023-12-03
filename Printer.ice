
module Demo
{
    exception RequestCanceledException{

    }

    interface Printer
    {
        ["amd"] idempotent string sendMsgAsync(string msg)
                throws RequestCanceledException;
         void shutdown();



    }


        interface CallbackReceiver
           {
               void receiveMessage(string msg);
           }
           interface CallbackSender
           {
               void sendMessage(CallbackReceiver* proxy,string msg);
               void makeWorker(CallbackReceiver* proxy,string msg);
               void shutdown();
           }






}
