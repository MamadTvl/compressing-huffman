import javafx.scene.control.ProgressBar;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class HuffmanTree {
    private Node root;
    private FileReader Text;
    protected String Address;
    private int Encodelength; //Encoded File bites


    HuffmanTree(String address) {
        Address = address;
        try {
            root = Huffman(ReadingFile(Address));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Creating Huffman - Tree
    private Node Huffman(PriorityQueue<Node> queue) {
        int n = queue.size();
        for (int i = 1; i <= n - 1; i++) {
            Node z = new Node();
            z.left = queue.remove();
            z.right = queue.remove();
            z.freq = z.left.freq + z.right.freq;
            queue.add(z);
        }
        if (n == 1){
            Node z = new Node();
            z.right = queue.remove();
            z.freq = z.right.freq;
            return z;
        }
        return queue.remove();
    }

    // Reading File and Put Characters in Priority - Queue
    private PriorityQueue<Node> ReadingFile(String address) throws IOException {
        LinkedList<Node> t = new LinkedList<>();
        PriorityQueue<Node> queue;
        try {
            Text = new FileReader(address);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }
        int i;
        while ((i = Text.read()) != -1) {
            Node z;
            if (isNew(t, i)) {
                z = new Node((char) i, 1);
                t.add(z);
            } else {
                t.get(index(t, i)).freq = count(t, i) + 1;
            }

        }
        Text.close();
        queue = new PriorityQueue<>(t.size(), new MyComparator());
        queue.addAll(t);
        return queue;
    }

    // Writing new Characters To new File for compressing !
    public void Encoding(String address, ProgressBar bar) throws IOException {
        FileWriter Encode = new FileWriter(address);
        try {
            Text = new FileReader(Address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int i;
        StringBuilder s = new StringBuilder();
        int MaxLeaf = 0;
        while ((i = Text.read()) != -1) {
            if (MaxLeaf < i)
                MaxLeaf = i;
            s.append((char) i);
        }
        Text.close();
        String[] st = new String[MaxLeaf + 1];
        buildCode(st, root, "");
        ArrayList<Integer> DecimalCodes = Encode(st, s, bar);
        System.out.println("----- Writing Codes-----");
        for (Integer DecimalCode : DecimalCodes) {
            Encode.write((char) DecimalCode.intValue());
            System.out.print((char)DecimalCode.intValue());
        }
        Encode.close();
    }

    // Take bits together (16 bit - 16 bit)
    private ArrayList<Integer> Encode(String[] st, StringBuilder s, ProgressBar bar) {
        System.out.println("----- Building Codes -----");
        StringBuilder f = new StringBuilder();
        for (int j = 0; j < s.length(); j++) {
            System.out.println(s.charAt(j) + " : " + st[s.charAt(j)]);
            f.append(st[s.charAt(j)]);
        }
        Encodelength = f.length();
        System.out.println("----Binary codes---");
        ArrayList<StringBuilder> BinaryCodes = new ArrayList<>();
        StringBuilder a = new StringBuilder();
//        ArrayList<Integer> DecimalCodes = new ArrayList<>();
//        Task task  = new Task() {
//            @Override
//            protected Object call() throws Exception {
//                StringBuilder a = new StringBuilder();
//                final int max = f.length();
//                for (int i = 1; i <= f.length(); i++) {
//                    a.append(Integer.parseInt(String.valueOf(f.toString().charAt(i - 1))));
//                    if (i % 16 == 0) {
//                        BinaryCodes.add(new StringBuilder().append(a));
//                        System.out.println(a);
//                        a = new StringBuilder();
//                    }
//                    updateProgress(i, max/2);
//                }
//                if (!a.toString().equals(""))
//                    BinaryCodes.add(new StringBuilder().append(a));
//                System.out.println(a);
////                ArrayList<Integer> DecimalCodes = new ArrayList<>();
//                buildCode(st, root, "");
//                int i = f.length()/2;
//                for (StringBuilder binary : BinaryCodes) {
//                    DecimalCodes.add(Integer.parseInt(binary.toString(), 2));
//                    updateProgress(max/2 + i, max);
//                    i++;
//                }
//                return DecimalCodes;
//            }
//        };
//        bar.progressProperty().bind(task.progressProperty());
//        new Thread(task).start();
//        if (!task.isRunning())
//            return DecimalCodes;
        int j = 0;
        for (int i = 1; i <= f.length(); i++) {
            a.append(Integer.parseInt(String.valueOf(f.toString().charAt(i - 1))));
            if (i % 16 == 0) {
                BinaryCodes.add(new StringBuilder().append(a));
                System.out.println(a);
                a = new StringBuilder();
                j++;

            }
            bar.setProgress(i / (f.length() / 2));
        }
        if (!a.toString().equals(""))
            BinaryCodes.add(new StringBuilder().append(a));
        System.out.println(a);
        ArrayList<Integer> DecimalCodes = new ArrayList<>();
        int i = 1;
        for (StringBuilder binary : BinaryCodes) {
            DecimalCodes.add(Integer.parseInt(binary.toString(), 2));
            bar.setProgress(0.5 + i / (j + 1));
            i++;
        }
        return DecimalCodes;
    }

    // build new Codes For characters
    private void buildCode(String[] st, Node x, String s) {
        if (x.notLeaf() && root.freq != root.right.freq) {
            buildCode(st, x.left, s + '0');
            buildCode(st, x.right, s + '1');
        } else {
            if (root.freq == root.right.freq)
                st[x.right.data] = "1";
            else
                st[x.data] = s;
        }
    }

    // Decoding with HuffmanTree
    public void Decoding(String address, ProgressBar bar) throws IOException {
        System.out.println("---- Finding Characters ----");
        String Encode = Decode(address, bar);
        FileWriter fileWriter = new FileWriter(address);
        for (int i = 0; i < Encode.length(); i++) {
            Node tmpNode = root;
            while (tmpNode.notLeaf() && i < Encode.length()) {
                System.out.print(Encode.charAt(i));
                if (Encode.charAt(i) == '1')
                    tmpNode = tmpNode.right;
                else
                    tmpNode = tmpNode.left;
                i++;
            }
            System.out.println(" : " + tmpNode.data);
            fileWriter.write(tmpNode.data);
            i--;
            bar.setProgress((3 + i / Encode.length()) * 25 / 100);
        }
        fileWriter.close();
    }

    //Converting new Characters to binary codes and combine them for Decoding method
    private String Decode(String address, ProgressBar bar) throws IOException {
        System.out.println("-----convert characters to binary------");
        FileReader fileReader = new FileReader(address);
        int o, i = 0;
        String[] Encode;
        if (Encodelength % 16 == 0)
            Encode = new String[Encodelength / 16];
        else
            Encode = new String[Encodelength / 16 + 1];
        while ((o = fileReader.read()) != -1) {
            Encode[i] = Integer.toString(o, 2);
            bar.setProgress((i / (Encodelength % 16 + 1)) * 25 / 100);
            i++;
        }
        fileReader.close();
        for (int k = 0; k < Encode.length - 1; k++) {
            if (Encode[k].length() < 16) {
                int n = 16 - Encode[k].length();
                StringBuilder c = new StringBuilder("");
                for (int l = 0; l < n; l++) {
                    c.append(0);
                }
                c.append(Encode[k]);
                Encode[k] = c.toString();
            }
            bar.setProgress((k / Encode.length + 1) * 25 / 100);
        }
        if (Encodelength % 16 != 0 || Encodelength < 16) {
            int n = Encodelength - (Encodelength / 16) * 16;
            if (n > Encode[Encode.length - 1].length()) {
                StringBuilder c = new StringBuilder("");
                for (int k = 0; k < n - Encode[Encode.length - 1].length(); k++) {
                    c.append(0);
                }
                c.append(Encode[Encode.length - 1]);
                Encode[Encode.length - 1] = c.toString();
            }
        }
        StringBuilder BinaryEncoded = new StringBuilder();
        for (int i1 = 0; i1 < Encode.length; i1++) {
            String aEncode = Encode[i1];
            System.out.println(aEncode);
            BinaryEncoded.append(aEncode);
            bar.setProgress((2 + i1 / Encode.length) * 25 / 100);
        }
        return BinaryEncoded.toString();
    }

    //--------------- Helping ReadingFile Method-------------------------------------//

    private boolean isNew(LinkedList<Node> t, int c) {
        for (Node aT : t) {
            if ((char) c == aT.data) {
                return false;
            }
        }
        return true;
    }

    private int count(LinkedList<Node> t, int c) {
        int cnt = 0;
        for (Node aT : t) {
            if ((char) c == aT.data) {
                cnt = aT.freq;
            }
        }
        return cnt;
    }

    private int index(LinkedList<Node> t, int c) {
        for (int i = 0; i < t.size(); i++) {
            if ((char) c == t.get(i).data) {
                return i;
            }
        }
        return -1;
    }

    //--------------------Print Tree--------------------------------------------------//
    private void printBinaryTree(Node root, int level) {
        if (root == null)
            return;
        printBinaryTree(root.right, level + 1);
        if (level != 0) {
            for (int i = 0; i < level - 1; i++)
                System.out.print("|\t");
            if (root.data != 0 && root.data != ' ')
                System.out.println("|-------" + root.data);
            else if (root.data == ' ')
                System.out.println("|-------" + "space");
            else
                System.out.println("|-------" + root.freq);
        } else if (root.data != 0 && root.data != ' ')
            System.out.println(root.data);
        else if (root.data == ' ')
            System.out.println("space");
        else
            System.out.println(root.freq);
        printBinaryTree(root.left, level + 1);
    }

    public void Print() {
        printBinaryTree(root, 0);
    }
}
