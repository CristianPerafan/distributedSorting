import Demo.CallbackReceiverPrx;
import com.zeroc.Ice.Current;

public class CallbackSender implements Demo.CallbackSender {




    @Override
    public void initiateCallback(CallbackReceiverPrx proxy, String message, Current current) {

    }

    @Override
    public void sendMessage(CallbackReceiverPrx proxy, String msg, Current current) {

    }

    @Override
    public void makeWorker(CallbackReceiverPrx proxy, String msg, Current current) {

    }

    @Override
    public void shutdown(Current current) {

    }


}

