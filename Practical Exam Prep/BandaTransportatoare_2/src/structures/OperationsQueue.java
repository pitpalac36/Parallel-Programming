package structures;
import models.Node;

public class OperationsQueue extends GenericQueue<Node> {

    public OperationsQueue() {
        super();
    }

    public synchronized void push(Node elem) {
        super.push(elem);
    }

    public synchronized Node pop() {
        return super.pop();
    }

    public synchronized boolean isEmpty() {
        return super.size() == 0;
    }

    public synchronized int size() {
        return super.size();
    }
}
