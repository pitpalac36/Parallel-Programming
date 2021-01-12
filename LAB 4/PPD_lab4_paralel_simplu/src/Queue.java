import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Queue {
    private Node head;
    private Node tail;
    private AtomicInteger size;
    private ReentrantLock lock = new ReentrantLock();

    public Queue() {
        head = tail = null;
        size = new AtomicInteger(0);
    }

    public void pushBack(Node node) {
        lock.lock();
        if (head == null)
            head = tail = node;
        else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        lock.unlock();
        size.incrementAndGet();
    }

    public Node popFront() {
        if (size.get() > 0) {
            lock.lock();
            Node toReturn = head;
            if (head.next == null) {  // doar un element in coada -> ramane goala
                head = tail = null;
            }
            else {
                head.next.prev = null;
                head = head.next;
            }
            lock.unlock();
            size.decrementAndGet();
            return toReturn;
        }
        return null;
    }

    public int getSize() {
        return size.get();
    }

    public boolean isEmpty() {
        return size.get() == 0;
    }
}