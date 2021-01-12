import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    private static Queue q = new Queue();
    private static LinkedList list = new LinkedList();
    private static boolean end = false;

    public static void produce(String[] files, String name) throws IOException, InterruptedException {
            for (String each : files) {
                BufferedReader reader = new BufferedReader(new FileReader(each));
                String line;
                while ((line = reader.readLine()) != null) {
                    var fields = line.split(",");
                    Node nod = new Node(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]));
                    System.out.println("Threadul " + name + " adauga in coada nodul (" + nod.coef + ", " + nod.exp + ")");
                    synchronized (q) {
                        q.pushBack(nod);
                        q.notify();
                        while (q.getSize() == 10) {
                            System.out.println("coada plina..");
                            q.wait();
                        }
                    }
                }
                reader.close();
            }
        System.out.println("Thread 0 iese.");
        end = true;
        synchronized (q) {
            q.notifyAll();  // altfel threadurile care asteapta sa se umple coada nu isi termina executia niciodata
        }
    }

    public static void consume(String name) throws InterruptedException {
        while (true) {
            synchronized (q) {
                while (!end && q.isEmpty())
                    q.wait();
            }
            if (!q.isEmpty()) {
                Node node = list.insert(q.popFront());
                synchronized (q) {
                    System.out.println("Threadul " + name + " scoate din coada nodul (" + node.coef + ", " + node.exp + ")");
                    if (q.getSize() == 9) {
                        q.notify();
                    }
                }
            }
            if (end && q.isEmpty()) {
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

            // java Main nume_fisier1 .... nume_fisier_n  p
            // p -> nr threaduri

            int p = Integer.parseInt(args[args.length - 1]);
            Thread[] t = new Thread[p];

            long startTime = System.nanoTime();

            t[0] = new Thread(() -> {
                try {
                    String name = String.valueOf(0);
                    produce(Arrays.copyOf(args, args.length - 1), name);  // sterg p (raman doar numele fisierelor)
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            for (int i = 1; i < p; i++) {
                String name = String.valueOf(i);
                t[i] = new Thread(() -> {
                    try {
                        consume(name);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                });
            }

            for (int i = 0; i < p; i++)
                t[i].start();

            for (int i = 0; i < p; i++)
                t[i].join();

            long endTime = System.nanoTime();

            list.writeToFile("rezultat.txt");

            System.out.println((double)(endTime - startTime) / 1E6);    // ms
        }
    }
}