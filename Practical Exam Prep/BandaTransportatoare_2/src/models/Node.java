package models;

public class Node {
    private String ID;
    private OperationType opType;
    private int noObjects;

    public Node(String ID, OperationType opType, int noObjects) {
        this.ID = ID;
        this.opType = opType;
        this.noObjects = noObjects;
    }

    @Override
    public String toString() {
        return "Node{" +
                "ID=" + ID +
                ", opType=" + opType +
                ", noObjects=" + noObjects +
                '}';
    }
}

