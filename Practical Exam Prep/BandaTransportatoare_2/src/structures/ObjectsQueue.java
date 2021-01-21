package structures;
import java.util.List;

public class ObjectsQueue extends GenericQueue<Integer> {

    public ObjectsQueue() {
        super();
    }

    public synchronized void push(int elem) {
        super.push(elem);
    }

    public synchronized Integer pop() {
        return super.pop();
    }

    public synchronized boolean isEmpty() {
        return super.size() == 0;
    }

    public synchronized int size() {
        return super.size();
    }

    public synchronized final List<Integer>  getData() {
        return super.getData();
    }
}
