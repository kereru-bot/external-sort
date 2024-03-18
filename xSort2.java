import java.io.*;
import java.util.Scanner;

class xSort2 {



    public static void main(String args[]) {
        int runSize = Integer.parseInt(args[0]);
        String filename = args[1];
        int mergesPerPass = Integer.parseInt(args[2]);
        File[] left = new File[mergesPerPass];
        File[] right = new File[mergesPerPass];
        for(int i = 0; i < mergesPerPass; i++) {
            left[i] = new File("l_" + i);
            right[i] = new File("r_" + i);
        }
        int uniqueFiles = distributeRuns(filename, left, runSize);
        //create k files based on mergesperpass values
        //use distributeRuns on the files to distribute runs
        //across all of them
        int writingSide = 1;
        while(true) {
            boolean done = false;
            if((writingSide % 2) == 0) {
                //reading side writing side
                done = merge(right, left, runSize);
                if(done) {
                    //left one has everything
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(left[0]));
                        String next;
                        while((next = reader.readLine()) != null) {
                            System.out.println(next);
                        }
                        reader.close();
                    } catch (IOException ex) {
                        System.err.println("FileNotFoundException: " + ex);
                    }
                } else {

                }
            } else {
                System.out.println("merge correct");
                done = merge(left, right, runSize);
                if(done) {
                    //right one has everthing
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(right[0]));
                        String next;
                        //System.out.println("done");
                        while((next = reader.readLine()) != null) {
                            System.out.println(next);
                        }
                        reader.close();
                    } catch (IOException ex) {
                        System.err.println("FileNotFoundException: " + ex);
                    }
                }
            }

            runSize*= uniqueFiles;
            writingSide++;

        }
        //after that, i will have up to k files with all of the runs inside them
    }


    //returns the number of unique files written to, or -1 if an exception occurs
    public static int distributeRuns(String initialRunsFile, File[] files, int runSize) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(initialRunsFile));
            FileWriter writer;
            int maxFiles = files.length;
            String next;
            int linesRead = 0;
            int currentFile = 0;
            //just to record how many files were actually used
            int totalFilesWrittenTo = 0;

            //WILL APPEND TO THE END OF FILES
            writer = new FileWriter(files[currentFile], true);
            while((next = reader.readLine()) != null) {
                writer.write(next);
                linesRead++;
                if(linesRead == runSize) {
                    //switch to next file
                    writer.close();
                    currentFile++;
                    if(currentFile == maxFiles) {
                        currentFile = 0;
                    }
                    totalFilesWrittenTo++;
                    writer = new FileWriter(files[currentFile], true);
                    linesRead = 0;
                } else {
                    writer.write("\n");
                }

            }
            writer.close();
            reader.close();

            if(totalFilesWrittenTo > files.length) {
                totalFilesWrittenTo = files.length;
            }
            return totalFilesWrittenTo;
        } catch(FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " + ex);
        } catch (IOException ex) {
            System.err.println("IOException: " + ex);
        }
        return -1;
    }

    //runsize*=mergesPerPass with each merge
    public static boolean merge(File[] readingSide, File[] writingSide, int runSize) {
        try {

            //initialise readers
            BufferedReader[] readers = new BufferedReader[readingSide.length];
            for(int i = 0; i < readers.length; i++) {
                readers[i] = new BufferedReader(new FileReader(readingSide[i]));
            }

            String[] references = new String[readingSide.length];
            //ASSUME THAT ALL READINGSIDE FILES HAVE SOMETHING TO READ
            int[] linesRead = new int[readingSide.length];

            //initialise heap
            minHeap heap = populateHeap(readers,references,linesRead);

            //NOW HEAP IS SET UP AND READY FOR MERGE
            //ASSUME THAT WRITING SIDE FILES ARE EMPTY FOR EACH PASS
            FileWriter writer = null;
            int currentReadingFile = 0;
            int currentWritingFile = 0;
            String next;
            boolean endOfStream[] = new boolean[readingSide.length];
            boolean endOfRun[] = new boolean[readingSide.length];
            boolean singleFileWritten = true;

            while(true) {
                next = heap.pop();
                System.out.println(next);
                if(next == null) {
                    boolean finished = true;
                    //check for end of runs/end of stream
                    for(boolean b : endOfStream) {
                        if(!b) {
                            finished = false;
                        } else {
                            for(BufferedReader buf : readers) {
                                buf.close();
                            }

                            if(writer != null) {
                                writer.close();
                            }
                            return singleFileWritten;
                            //return true if everything is done
                        }
                    }

                    //checks if another merge needs to be performed
                    boolean nextRun = false;
                    for(boolean b : endOfRun) {
                        if(b) {
                            nextRun = true;
                        }
                    }
                    if(nextRun) {
                        singleFileWritten = false;
                        if(currentWritingFile == writingSide.length) {
                            currentWritingFile = 0;
                        } else {
                            currentWritingFile++;
                        }
                        //reset linesread etc.
                        for(int i = 0; i < linesRead.length; i++) {
                            linesRead[i] = 0;
                            references[i] = null;
                            endOfRun[i] = false;
                        }
                        heap = populateHeap(readers,references,linesRead);
                        next = heap.pop();
                    }

                }

                writer = new FileWriter(writingSide[currentWritingFile], true);

                //locate where the string came from and grab the next from the file
                for(int i = 0; i < references.length; i++) {
                    if(references[i] != null && references[i].compareTo(next) == 0) {
                        String top = readers[i].readLine();
                        linesRead[i]++;
                        if(endOfRun[i]) {
                            references[i] = null;
                            break;
                        }

                        if(top == null) {
                            //end of stream is reached
                            endOfStream[i] = true;
                            references[i] = null;
                            break;
                        }

                        if(linesRead[i] == runSize) {
                            //wait until the next line of merges
                            endOfRun[i] = true;
                        }

                        references[i] = top;
                        heap.insert(top);
                        break;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " + ex);
            System.err.println("this one");
        } catch (IOException ex) {
            System.err.println("IOException: " + ex);
        }



        return false;
    }

    public static minHeap populateHeap(BufferedReader[] readers, String[] references, int[] linesRead) {
        try {
            String[] heapLines = new String[readers.length];
            for(int i = 0; i < readers.length; i++) {
                String next;
                if((next = readers[i].readLine()) != null) {
                    references[i] = next;
                    linesRead[i]++;
                    heapLines[i] = next;
                }
            }

            minHeap heap = new minHeap();
            heap.createHeap(heapLines, heapLines.length);
            return heap;
        } catch (IOException ex) {
            System.err.println("IOException ex: " + ex);
        }
        return null;
    }
}
