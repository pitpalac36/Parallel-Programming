import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedList {
    private Node first;
    private ReentrantLock lock = new ReentrantLock();

    public LinkedList() {}

    public Node insert(Node nod) {
        if (first != null)
            first.lock();
        if (first == null) {    // adaug primul
            lock.lock();
            first = nod;
            lock.unlock();
        }
        else
        {
            if (first.exp >= nod.exp) {    // actualizez primul
                if (first.exp == nod.exp) {
                    first.coef += nod.coef;
                    first.unlock();
                }
                else {                     // adaug inaintea primului
                    nod.next = first;
                    nod.next.prev = nod;
                    first = nod;
                    first.next.unlock();
                }
            }
            else {
                Node current = first;

                if (current.next != null)
                    current.next.lock();

                while (current.next != null && current.next.exp <= nod.exp) {
                    current = current.next;
                    if (current.next != null)
                        current.next.lock();
                    current.prev.unlock();
                }

                // current si current.next sunt blocate
                if (current.exp == nod.exp) {   // actualizez nodul curent
                    current.coef += nod.coef;

                    // deblochez current si current.next
                    if (current.next != null) {
                        current.next.unlock();
                    }
                    current.unlock();
                }
                else {    // adaug dupa nodul curent
                    nod.next = current.next;
                    if (current.next != null)
                        nod.next.prev = nod;
                    current.next = nod;
                    nod.prev = current;

                    // deblochez current si current.next (care intre timp a devenit nod.next)
                    if (nod.next != null)
                        nod.next.unlock();
                    current.unlock();
                }
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
