import java.io.*;

/*
 * A class that takes in a file containing runs of strings and sorts them
 * over a given number of files
 */
class xSort {
    //global variable so the merge function can easily update the runsize after each merge
    public static int totalRunSize;

    /*
     * @param args Argument one must be the run size of the
     * runs in the runs file, argument two must be the name
     * of the file that contains the runs, and argument three
     * must contain the number of files to runs to be merged
     * per pass, i.e. 10 will merge runs over 10 different files.
     */
    public static void main(String args[]) {
        totalRunSize = Integer.parseInt(args[0]);
        String filename = args[1];
        //number of files to be merged on each pass
        int mergesPerPass = Integer.parseInt(args[2]);

        //left and right sides to make alternating between reading/writing more clear
        File[] left = new File[mergesPerPass];
        File[] right = new File[mergesPerPass];

        try {
            for(int i = 0; i < mergesPerPass; i++) {
                left[i] = new File("l_" + i);
                left[i].createNewFile();
                left[i].deleteOnExit();
                right[i] = new File("r_" + i);
                right[i].createNewFile();
                right[i].deleteOnExit();
            }
        } catch(IOException ex) {
            System.err.println("IOException: " + ex);
        }

        int maxFiles = distributeRuns(left, filename, totalRunSize);
        while(true) {
            try {
                maxFiles = merge(totalRunSize, left, right, maxFiles);
                if(maxFiles == 1) {
                    //r_0 must contain the sorted strings
                    BufferedReader reader = new BufferedReader(new FileReader("r_0"));
                    String next;
                    while((next = reader.readLine()) != null) {
                        System.out.println(next);
                    }
                    return;
                } else {
                    for(int i = 0; i < mergesPerPass; i++) {
                        left[i].delete();
                        left[i] = new File("l_" + i);
                        left[i].createNewFile();
                        left[i].deleteOnExit();
                    }
                }
                maxFiles = merge(totalRunSize, right, left, maxFiles);

                if(maxFiles == 1) {
                    //l_0 must contain the sorted strings
                    BufferedReader reader = new BufferedReader(new FileReader("l_0"));
                    String next;
                    while((next = reader.readLine()) != null) {
                        System.out.println(next);
                    }
                    return;
                } else {
                    for(int i = 0; i < mergesPerPass; i++) {
                        right[i].delete();
                        right[i] = new File("r_" + i);
                        right[i].createNewFile();
                        right[i].deleteOnExit();
                    }
                }
            } catch (IOException ex) {
                System.err.println("IOException: " + ex);
            }

        }

    }

    /**
     * Merges the reading files into the writing files and returns the number of different files that were
     * written to
     * @param runSize The max size of the runs contained in the reading files
     * @param reading The files that will be read from (the ones that contain the runs)
     * @param writing The files that the merged runs will be written into
     * @param maxFiles The maximum number of files to be read from
     * @return The maximum number of files that were written to, so 3 is returned if 3 files are written to,
     * or 1 if one file is written to, (this means that the final merge has been completed)
     *  */
    public static int merge(int runSize, File[] reading, File[] writing, int maxFiles) {
        minHeap heap = populateHeap(reading, maxFiles);
        FileWriter[] writers =  new FileWriter[maxFiles];
        boolean stillReading = true;
        int currReading = 0;
        int currWriting = 0;
        int numTempNodes = 0;
        int numWrittenFiles = 1;
        minHeap tempHeap;
        strNode[] tempNodes = new strNode[maxFiles];
        boolean[] notFirstLine = new boolean[maxFiles];
        try {
            for(int i = 0; i < writers.length; i++) {
                writers[i] = new FileWriter(writing[i]);
            }
            while(stillReading) {
                strNode next = heap.pop();
                //nothing left in the heap, check tempnodes
                if(next == null) {
                    //if there are temp nodes, merging hasn't finished
                    if(numTempNodes != 0) {
                        strNode[] nodes = new strNode[numTempNodes];
                        for(int i = 0; i < tempNodes.length; i++) {
                            if(tempNodes[i] != null) {
                                nodes[numTempNodes - 1] = tempNodes[i];
                                numTempNodes--;
                            }
                        }
                        heap = new minHeap();
                        heap.createHeap(nodes,nodes.length);
                        tempNodes = new strNode[maxFiles];
                        currReading = 0;
                        currWriting++;
                        numWrittenFiles++;
                        if(currWriting == maxFiles) {
                            currWriting = 0;
                        }
                    } else {
                        //merging must be done, declare new max run size
                        totalRunSize *= maxFiles;
                        for(int i = 0; i < writers.length; i++) {
                            writers[i].close();
                        }

                        if(numWrittenFiles > maxFiles) {
                            numWrittenFiles = maxFiles;
                        }
                        return numWrittenFiles;
                    }
                } else {
                    //make sure a new line character is only written if it isn't the
                    //first line in the file
                    if(notFirstLine[currWriting]) {
                        writers[currWriting].write("\n");
                    }
                    writers[currWriting].write(next.string);
                    notFirstLine[currWriting] = true;
                    next.linesRead++;
                    if(next.linesRead == runSize) {
                        next.string = next.reader.readLine();
                        //store the current node as a temp node
                        //as there are still strings left in it
                        if(next.string != null) {
                            tempNodes[currReading] = next;
                            numTempNodes++;
                            currReading++;
                            next.linesRead = 0;
                        } else {
                            next.reader.close();
                        }
                    } else {
                        next.string = next.reader.readLine();
                        if(next.string == null) {
                            next.reader.close();
                            currReading++;
                        } else {
                            heap.insert(next);
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " + ex);
        } catch (IOException ex) {
            System.err.println("IOException: " + ex);
        }
        return -1;
    }
    
    /**
     * Distributes all of the runs in the runs file into as many of the given files as it can
     * @param files The files that the runs will be distributed between
     * @param filenameRuns The name of the file that contains the runs
     * @param runSize The size of each run
     * @return The total number of files that the runs were distributed across, i.e. 5 if the runs only went across
     *         5 files
     */
    public static int distributeRuns(File[] files, String filenameRuns, int runSize) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filenameRuns));
            String next;
            FileWriter[] writers = new FileWriter[files.length];
            int linesRead = 0;
            int currFile = 0;
            int maxFiles = 1;
            boolean newLine = false;
            boolean firstPass = true;
            for(int i = 0; i < files.length; i++) {
                writers[i] = new FileWriter(files[i]);
            }

            while((next = reader.readLine()) != null) {
                writers[currFile].write(next);

                linesRead++;
                if(linesRead == runSize) {
                    currFile++;
                    maxFiles++;
                    if(currFile == files.length) {
                        currFile = 0;
                        firstPass = false;
                    }
                    //to avoid writing a new line at the beginning of
                    //the file
                    if(!firstPass) {
                        writers[currFile].write("\n");
                    }
                    linesRead = 0;
                } else {
                    writers[currFile].write("\n");
                }
            }
            if(maxFiles > files.length) {
                maxFiles = files.length;
            }
            for(int i = 0; i < files.length; i++) {
                writers[i].close();
            }
            reader.close();
            return maxFiles;
        } catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " + ex);
        } catch (IOException ex) {
            System.err.println("IOException: " + ex);
        }
        return -1;
    }

    /**
     * Takes in an array of files and creates a minheap using the first string from each file
     * @param files The files that will be read from
     * @param maxFiles The max number of files that can be read from in the array
     * @return A sorted minheap that contains readers for the given files
     */
    public static minHeap populateHeap(File[] files, int maxFiles) {
        strNode[] nodes = new strNode[maxFiles];
        minHeap heap = new minHeap();
        try {
            for(int i = 0; i < maxFiles; i++) {
                BufferedReader reader = new BufferedReader(new FileReader(files[i]));
                String s = reader.readLine();
                nodes[i] = new strNode(s,reader,0);
            }
            heap.createHeap(nodes, maxFiles);
            return heap;
        } catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " + ex);
        } catch (IOException ex) {
            System.err.println("IOException: " + ex);
        }
        return null;
    }
}
