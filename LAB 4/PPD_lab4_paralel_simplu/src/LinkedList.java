import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedList {
    private Node first;
    private ReentrantLock lock = new ReentrantLock();

    public LinkedList() {}

    public Node insert(Node nod) {
        lock.lock();
        if (first == null) {    // adaug primul
            first = nod;
            lock.unlock();
        }
        else
        {
            if (first.exp >= nod.exp) {    // actualizez primul
                if (first.exp == nod.exp)
                    first.coef += nod.coef;
                else {                     // adaug inaintea primului
                    nod.next = first;
                    nod.next.prev = nod;
                    first = nod;
                }
                lock.unlock();
            }
            else {
                lock.unlock();
                Node current = first;

                while (current.next != null && current.next.exp <= nod.exp) {
                    current = current.next;
                }
                lock.lock();
                if (current.exp == nod.exp) {   // actualizez nodul curent
                    current.coef += nod.coef;
                }
                else {      // adaug dupa nodul curent
                    nod.next = current.next;
                    if (current.next != null)
                        nod.next.prev = nod;
                    current.next = nod;
                    nod.prev = current;
                }
                lock.unlock();
            }
        }
        return nod;
    }

    public void writeToFile(String filename) {
        try {
            FileWriter writer = new FileWriter(new File(filename));
            Node curent = first;
            String line;
            while(curent != null) {
                if (curent.coef != 0) {
                    line = "coef : " + curent.coef + "   exp : " + curent.exp + "\n";
                    writer.write(line);
                }
                curent = curent.next;
            }
            writer.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
