import java.io.*;
import java.util.Scanner;

class xSort2 {



    public static void main(String args[]) {
        int runSize = Integer.parseInt(args[0]);
        String filename = args[1];
        int mergesPerPass = Integer.parseInt(args[2]);


        //create k files based on mergesperpass values
        //use distributeRuns on the files to distribute runs
        //across all of them

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
            String[] initLines = new String[readingSide.length];
            int[] linesRead = new int[readingSide.length];

            minHeap heap = populateHeap(references, readers, linesRead);

            for(int i = 0; i < readingSide.length; i++) {
                readers[i] = new BufferedReader(new FileReader(readingSide[i]));
                String next;
                if((next = readers[i].readLine()) != null) {
                    references[i] = next;
                    initLines[i] = next;
                    linesRead[i]++;
                }
            }
            //initialise heap
            //heap = new minHeap();
            //heap.createHeap(initLines, readingSide.length);

            //NOW HEAP IS SET UP AND READY FOR MERGE
            //ASSUME THAT WRITING SIDE FILES ARE EMPTY FOR EACH PASS
            FileWriter writer;
            int currentReadingFile = 0;
            int currentWritingFile = 0;
            String next;
            boolean endOfStream[] = new boolean[readingSide.length];
            boolean endOfRun[] = new boolean[readingSide.length];
            boolean singleFileWritten = true;

            while(true) {
                next = heap.pop();
                if(next == null) {
                    boolean finished = true;
                    //check for end of runs/end of stream
                    for(boolean b : endOfStream) {
                        if(!b) {
                            finished = false;
                        } else {
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
        } catch (IOException ex) {
            System.err.println("IOException: " + ex);
        }



        return false;
    }

    public static minHeap populateHeap(BufferedReader[] readers, String[] references, int[] linesRead) {


        return null;
    }
}
