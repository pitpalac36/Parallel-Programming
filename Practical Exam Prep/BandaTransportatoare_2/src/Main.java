import runners.Consumer;
import runners.Producer;
import structures.ObjectsQueue;
import structures.OperationsQueue;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static AtomicInteger status = new AtomicInteger();
    private static ObjectsQueue q = new ObjectsQueue();
    private static OperationsQueue ops = new OperationsQueue();

    public static void main(String[] args) throws InterruptedException {
        /*
            citire argumente de la tastatura
         */
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nr de roboti care produc : ");
        int p = scanner.nextInt();
        System.out.print("Nr de roboti care consuma : ");
        int c = scanner.nextInt();
        System.out.print("Capacitatea benzii : ");
        int n = scanner.nextInt();

        Thread[] t = new Thread[p + c];
        status.set(p);

        /*
            creare threaduri producatorare
         */
        for (int i = 0; i < p; i++) {
            String name = String.valueOf(i);
            t[i] = new Producer(q, ops, status, name, n);
        }

        /*
            creare threaduri consumatoare
         */
        for (int i = p; i < p + c; i++) {
            String name = String.valueOf(i);
            t[i] = new Consumer(q, ops, status, name);
        }

        //Thread logger = new Logger(q, status);
        //logger.start();

        for (int i = 0; i < p + c; i++)
            t[i].start();

        //logger.join();

        for (int i = 0; i < p + c; i++)
            t[i].join();

    }
}