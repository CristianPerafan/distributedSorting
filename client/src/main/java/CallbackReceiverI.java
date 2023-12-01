import com.zeroc.Ice.Current;

public class CallbackReceiverI implements Demo.CallbackReceiver{


    @Override
    public void receiveMessage(String msg, Current current) {
        System.out.println(msg);
    }
}
