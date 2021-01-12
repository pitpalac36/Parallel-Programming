import java.util.concurrent.locks.ReentrantLock;

class Node {
    protected int coef;
    protected int exp;
    protected Node prev;
    protected Node next;
    protected ReentrantLock lock;

    public Node(int coef, int exp) {
        this.coef = coef;
        this.exp = exp;
        this.prev = this.next = null;
        lock = new ReentrantLock();
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}