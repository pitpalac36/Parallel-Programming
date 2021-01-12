import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static Queue q = new Queue();
    private static final Object headLock = new Object();
    private static final Object tailLock = new Object();
    private static LinkedList list = new LinkedList();
    private static AtomicInteger status;

    public static void produce(String[] files, String name) throws IOException, InterruptedException {
            for (String each : files) {
                BufferedReader reader = new BufferedReader(new FileReader(each));
                String line;
                while ((line = reader.readLine()) != null) {
                    var fields = line.split(",");
                    Node nod = new Node(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]));
                    System.out.println("Threadul " + name + " adauga in coada nodul (" + nod.coef + ", " + nod.exp + ")");
                    synchronized (headLock) {
                        while (q.getSize() == 10) {
                            System.out.println("coada plina..");
                            headLock.wait();
                        }
                    }
                    synchronized (tailLock) {
                        q.pushBack(nod);
                        tailLock.notify();
                    }
                }
                reader.close();
            }
        System.out.println("Thread " + name + " iese.");

        status.decrementAndGet();

        if (status.get() == 0) {
            synchronized (tailLock) {
                tailLock.notifyAll();  // altfel threadurile care asteapta sa se umple coada nu isi termina executia niciodata
            }
        }
    }

    public static void consume(String name) throws InterruptedException {
        while (true) {
            synchronized (tailLock) {
                while (status.get() > 0 && q.isEmpty())
                    tailLock.wait();
            }
            if (!q.isEmpty()) {
                Node node = list.insert(q.popFront());
                synchronized (headLock) {
                    System.out.println("Threadul " + name + " scoate din coada nodul (" + node.coef + ", " + node.exp + ")");
                    if (q.getSize() == 9) {
                        headLock.notify();
                    }
                }
            }
            if (status.get() == 0 && q.isEmpty()) {
                System.out.println("Thread " + name + " iese.");
                return;
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args[0].equals("generate")) {

            // java Main generate nume_fisier nr_elemente exponent_maxim

            Generator generator = Generator.getInstance();
            String filename = args[1];
            int numerOfElements = Integer.parseInt(args[2]);
            int maxExp = Integer.parseInt(args[3]);
            generator.generate(filename, numerOfElements, maxExp);
        }
        else {

            // java Main nume_fisier1 .... nume_fisier_n  p1  p2
            // p1 -> nr threaduri care produc
            // p2 -> nr threaduri care consuma

            int p1 = Integer.parseInt(args[args.length - 2]);
            int p2 = Integer.parseInt(args[args.length - 1]);
            Thread[] t = new Thread[p1 + p2];

            status = new AtomicInteger(p1);

            int offset = 0;     // offsetul de la care threadul i va citi din array-ul de fisiere
            args = Arrays.copyOf(args, args.length - 2);    // sterg p1 si p2 (raman doar numele fisierelor)
            int rest = 0;

            for (int i = 0; i < p1; i++) {
                if (i == p1 - 1 && args.length % p1 != 0)
                    rest = 1;
                String[] files = Arrays.copyOfRange(args, offset, args.length/p1 + offset + rest);
                String name = String.valueOf(i);
                t[i] = new Thread(() -> {
                    try {
                        System.out.print("Threadul " + name + " va citi din fisierele : ");
                        for(String each : files)
                            System.out.println(each);
                        produce(files, name);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                offset += args.length/p1;
            }

            for (int i = p1; i < p1 + p2; i++) {
                String name = String.valueOf(i);
                t[i] = new Thread(() -> {
                    try {
                        consume(name);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                });
            }

            long startTime = System.nanoTime();

            for (int i = 0; i < p1 + p2; i++)
                t[i].start();

            for (int i = 0; i < p1 + p2; i++)
                t[i].join();

            long endTime = System.nanoTime();

            list.writeToFile("rezultat.txt");

            System.out.println((double)(endTime - startTime) / 1E6);    // ms
        }
    }
}