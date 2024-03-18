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

            minHeap heap;
            int linesRead = 0;
            while(reader.hasNextLine()) {
                run[linesRead] = reader.nextLine();
                linesRead++;
                if(linesRead == runSize || !reader.hasNextLine()) {
                    heap = new minHeap();
                    //sort the run
                    heap.createHeap(run, null, linesRead);
                    String next = "";
                    while((next = heap.pop()) != null) {
                        System.out.println(next);
                    }
                    linesRead = 0;
               }
            }
            reader.close();
            //System.out.println("Lines read: " + linesRead);
        } catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " + ex);
        }
        //System.out.flush();
        return;
    }

}
