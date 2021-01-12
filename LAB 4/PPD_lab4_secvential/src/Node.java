class Node {
    protected int coef;
    protected int exp;
    protected Node prev;
    protected Node next;

    public Node(int coef, int exp) {
        this.coef = coef;
        this.exp = exp;
        this.prev = this.next = null;
    }
}