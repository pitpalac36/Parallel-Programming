package structures;
import java.util.ArrayList;
import java.util.List;

public class GenericQueue<T> {
    private List<T> list;

    public GenericQueue() {
        list  = new ArrayList<>();
    }

    public void push(T obj) {
        list.add(obj);
    }

    public T pop() {
        return list.remove(0);
    }

    public int size() {
        return list.size();
    }

    public List<T> getData() {
       return new ArrayList<>(list);
    }
}