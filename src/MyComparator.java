import java.util.Comparator;

class MyComparator implements Comparator<Node> {// Priority Queue Comparator
    public int compare(Node x, Node y) {
        return x.freq - y.freq;
    }
}