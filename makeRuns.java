import java.io.*;
import java.util.Scanner;

class MakeRuns {
    public static void main(String[] args) {
        int runSize = Integer.parseInt(args[0]);
        String filename = args[1];

        File file = new File(filename);

        try {
            //FileReader reader = new FileReader(file);
            Scanner reader = new Scanner(file);
            String[] run = new String[runSize];
            //char[] currentChar = new char[1];

            int offset = 0;
            int length = 1;
            int linesRead = 0;
            while(reader.hasNextLine()) {
                run[linesRead] = reader.nextLine();
                //System.out.println(reader.nextLine());
                linesRead++;
                if(linesRead == runSize || !reader.hasNextLine()) {
                    //sort the run
                    //run size might not always be 128
                    //print the run to standard output
                    String[] output = heapSort(run, linesRead);
                    //for(String s : output) {
                    //    System.out.println(s);
                   // }

                    linesRead = 0;

                }
            }
            reader.close();
            System.out.println("Lines read: " + linesRead);
        } catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " + ex);
        } //catch (IOException ex) {
       //     System.err.println("IOException: " + ex);
        //}


        System.out.flush();

        return;
    }

    //convert array into a minheap
    public static String[] heapSort(String[] run, int runSize) {
        while(runSize != 0) {
            run = sortHeap(run, runSize);
            String top = run[0];
            run[0] = run[runSize - 1];
            runSize--;
            System.out.println(top);
        }
        return null;
    }
    private static String[] sortHeap(String[] run, int runSize) {
         //left child is index 2*parent
        //right child is index 2*parent + 1
        //you will get parent index if you divide by 2 no matter what
        //go through the tree multiple times until no swaps are performed
        //
        //make sure that the child index is actually part of the run
        boolean swapMade = true;
        int numParents = runSize / 2;
        while(swapMade == true) {
            swapMade = false;
            for(int currParent = 1; currParent <= numParents; currParent++) {
                //stupid arrays
                int parentIndex = currParent - 1;
                int leftChild = (currParent * 2) - 1;
                int rightChild = currParent * 2;
                //System.out.println(run[parentIndex]);
                //System.out.println(run[leftChild]);
                //System.out.println(run[rightChild]);
                if (run[parentIndex].compareTo(run[leftChild]) > 0) {
                    //parent is bigger than child
                    //swap parent and child
                    swapMade = true;
                    //System.out.println("compared left");
                    //swap parent and child around
                    String temp = run[parentIndex];
                    run[parentIndex] = run[leftChild];
                    run[leftChild] = temp;
                }

                //if there is actually a right child
                if(rightChild < runSize && run[rightChild] != null) {
                    if(run[parentIndex].compareTo(run[rightChild]) > 0) {
                        String temp = run[parentIndex];
                        run[parentIndex] = run[rightChild];
                        run[rightChild] = temp;
                        //System.out.println("compared right");
                        swapMade = true;
                    }
                }
            }
        }
        //for(String s : run) {
        //    System.out.println(s);
        //}
        //should be sorted at this point
        return run;
    }

}
