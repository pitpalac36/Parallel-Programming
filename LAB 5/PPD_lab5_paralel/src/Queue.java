import java.util.concurrent.atomic.AtomicInteger;

public class Queue {
    private Node head;
    private Node tail;
    private AtomicInteger size;

    public Queue() {
        head = tail = null;
        size = new AtomicInteger(0);
    }

    public void pushBack(Node node) {
        synchronized (this) {
            if (tail == null)
                head = tail = node;
            else {
                tail.next = node;
                node.prev = tail;
                tail = node;
            }
        }
        size.incrementAndGet();
    }

    public Node popFront() {
        if (size.get() > 0) {
            synchronized (this) {
                if (head.next == null)
                    tail = null;
                else
                    head.next.prev = null;
                Node toReturn = head;
                head = head.next;
                size.decrementAndGet();
                return toReturn;
            }
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