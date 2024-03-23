import java.io.*;

class strNode {
    String string;
    BufferedReader reader;
    int linesRead;

    /**
     * Stores a string, bufferedreader, and number of lines read from that
     * reader into a nodem, this is just for easy access when all 3
     * variables are needed.
     * @param s The string to store in the node
     * @param r The BufferedReader to store in the node
     * @param l The number of lines read from the bufferedreader
     *          to be stored
     */
    public strNode(String s, BufferedReader r, int l) {
        string = s;
        reader = r;
        linesRead = l;
    }
}
