package runners;
import models.Node;
import models.OperationType;
import structures.ObjectsQueue;
import structures.OperationsQueue;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer extends Thread {
    private ObjectsQueue q;
    private OperationsQueue ops;
    private String name;
    private AtomicInteger status;

    public Consumer(ObjectsQueue q, OperationsQueue ops, AtomicInteger status, String name) {
        this.q = q;
        this.ops = ops;
        this.name = name;
        this.status = status;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(8);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (q) {
                while (status.get() > 0 && q.isEmpty()) {
                    try {
                        q.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            synchronized (q) {
                if (q.size() >= 3) {
                    int obiect1 = q.pop();
                    int obiect2 = q.pop();
                    int obiect3 = q.pop();
                    ops.push(new Node(name, OperationType.POP, q.size()));
                    System.out.println("Consumatorul " + name + " scoate de pe banda obiectele : " + obiect1 + " " + obiect2 + " " + obiect3 + " la " + LocalDateTime.now() +" --- nr obiecte pe banda : " + q.size());
                    if (q.size() >= 4) {
                        System.out.println("Consumatorul " + name + " notifica producatorii");
                        q.notifyAll();
                    }
                }
            }
            if (status.get() == 0 && (q.isEmpty() || q.size() < 3)) {
                System.out.println("Consumatorul " + name + " iese.");
                return;
            }
        }
    }
}
