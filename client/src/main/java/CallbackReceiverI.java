import com.zeroc.Ice.Current;

public class CallbackReceiverI implements Demo.CallbackReceiver{


    @Override
    public void receiveMessage(String msg, Current current) {
        System.out.println(msg);
    }

    @Override
    public void startWorker(int from, int to, Current current) {

    }

    @Override
    public String getHalfAndRemove(Current current) {
        return null;
    }

    @Override
    public int verifyLength(Current current) {
        return 0;
    }
}
