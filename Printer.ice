
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
               void startWorker(int from, int to,string filename,string basepath);
               string getHalfAndRemove();
               int verifyLength();
           }
           interface CallbackSender
           {
               void initiateCallback(CallbackReceiver* proxy, string message);

               void sendMessage(CallbackReceiver* proxy,string msg);
               void makeWorker(CallbackReceiver* proxy,string msg);
               void shutdown();
           }






}
