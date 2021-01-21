package runners;
import models.Node;
import models.OperationType;
import structures.ObjectsQueue;
import structures.OperationsQueue;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer extends Thread {
    private ObjectsQueue q;
    private OperationsQueue ops;
    private String name;
    private AtomicInteger status;
    private int capacitateBanda;

    public Producer (ObjectsQueue q, OperationsQueue ops, AtomicInteger status, String name, int capacitateBanda) {
        this.q = q;
        this.ops = ops;
        this.name = name;
        this.status = status;
        this.capacitateBanda = capacitateBanda;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (q) {
                while (q.size() + 4 > capacitateBanda) {   // daca nu are unde pune obiecte pe banda => suspenda executia
                    System.out.println("Producator " + name + " : banda plina..");
                    try {
                        q.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            synchronized (q) {
                int obiect1 = new Random().nextInt(1000);
                int obiect2 = new Random().nextInt(1000);
                int obiect3 = new Random().nextInt(1000);
                int obiect4 = new Random().nextInt(1000);
                q.push(obiect1);
                q.push(obiect2);
                q.push(obiect3);
                q.push(obiect4);
                ops.push(new Node(name, OperationType.PUSH, q.size()));
                System.out.println("Producatorul " + name + " adauga pe banda obiectele : " + obiect1 + " " + obiect2 + " " + obiect3 + " " + obiect4 + " la " + LocalDateTime.now() + " --- nr obiecte pe banda : " + q.size());
                q.notifyAll();
            }
        }
        System.out.println("Producatorul " + name + " iese.");
        status.decrementAndGet();
        if (status.get() == 0) {
            synchronized (q) {
                q.notifyAll();  // altfel threadurile care asteapta sa se umple coada nu isi termina executia niciodata
            }
        }
    }
}
