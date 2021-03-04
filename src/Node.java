public class Node {
    protected char data;
    protected int freq;
    protected Node left, right;

    Node() {
        left = right = null;
        freq = 0;
    }


    Node(char e, int c) {
        data = e;
        left = right = null;
        freq = c;
    }

    public boolean notLeaf() {
        return (left != null) || (right != null);
    }


    private void printPostorder() {
        if (left != null)
            left.printPostorder();
        if (right != null)
            right.printPostorder();
        if (data != 0)
            System.out.print(data + "♣" + freq + " ");
        else
            System.out.print(freq + " ");
    }

    private void printInorder() {
        if (left == null)
            if (data != 0)
                System.out.println(data);
        if (left != null) {
            left.printInorder();
            if (data != 0)
                System.out.println(data);
        }
        if (right != null)
            right.printInorder();
    }

    public void printPreorder() {
        System.out.println(data);
        if (left != null)
            left.printPostorder();
        if (right != null)
            right.printPostorder();
    }
}
