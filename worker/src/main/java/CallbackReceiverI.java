import com.zeroc.Ice.Current;
import com.zeroc.Ice.Object;

public class CallbackReceiverI implements Demo.CallbackReceiver {

    @Override
    public void receiveMessage(String msg, Current current) {
        System.out.println(msg);
    }
}
