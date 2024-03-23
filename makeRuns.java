import java.io.*;
import java.util.Scanner;

/**
 * A class that takes in a run size and a filename and sorts the contents
 * of the files into runs of the given size to be used in a k-way sort
 * merge and prints them to the standard output.
 *  */
class MakeRuns {
    /**
     * @param args Argument one must be the size of the runs to be created
     *             and the second argument must be the name of the file
     *             that will be sorted
     */
    public static void main(String[] args) {
        int runSize = Integer.parseInt(args[0]);
        String filename = args[1];
        File file = new File(filename);
        try {
            Scanner reader = new Scanner(file);
            String[] run = new String[runSize];
            strNode[] nodes = new strNode[runSize];
            minHeap heap;
            int linesRead = 0;
            while(reader.hasNextLine()) {

                String s = reader.nextLine();
                nodes[linesRead] = new strNode(s,null,0);
                linesRead++;
                if(linesRead == runSize || !reader.hasNextLine()) {
                    heap = new minHeap();
                    //sort the run
                    heap.createHeap(nodes,linesRead);
                    strNode next = null;
                    while((next = heap.pop()) != null) {
                        System.out.println(next.string);
                    }
                    linesRead = 0;
               }
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " + ex);
        }
        return;
    }

}
