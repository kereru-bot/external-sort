import java.io.*;

class strNode {
    String string;
    BufferedReader reader;
    int linesRead;

    public strNode(String s, BufferedReader r, int l) {
        string = s;
        reader = r;
        linesRead = l;
    }
}
