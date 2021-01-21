package runners;
import structures.ObjectsQueue;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Logger extends Thread {
    private ObjectsQueue q;
    private AtomicInteger status;

    public Logger(ObjectsQueue q, AtomicInteger status) {
        this.q = q;
        this.status = status;
    }

    @Override
    public void run() {
        while (!(q.isEmpty() && status.get() == 0)) {
            try {
                Thread.sleep(20);
                List<Integer> readOnly = q.getData();
                for(int each : readOnly) {
                    System.out.print(each + " ");
                }
                System.out.println("\n==================================");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
