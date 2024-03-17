import java.io.*;
import java.util.Scanner;

class xSort {
    public static void main(String args[]) {
        int runSize = Integer.parseInt(args[0]);
        String filename = args[1];
        //number of files to be merged on each pass
        int numMerges = Integer.parseInt(args[2]);
        int currentRunningFiles = numMerges;
        //file and scanner arrays for the number of merges to do
        File[] files = new File[numMerges * 2];
        Scanner[] scanners = new Scanner[numMerges];
        FileWriter[] writers = new FileWriter[numMerges];

        try {
            File runs = new File(filename);
            Scanner reader = new Scanner(runs);
            String[] run = new String[runSize];
            //create the files to be used for the merge
            int totalLinesRead = 0;
            int linesRead = 0;
            int currentRun = 0;

            for(File file : files) {
                file = new File("r_" + currentRun);
                files[currentRun] = file;
                currentRun++;
            }

            currentRun = 0;
            for(FileWriter writer : writers) {
                writer = new FileWriter(files[currentRun]);
                writers[currentRun] = writer;
                currentRun++;
            }

            //WRITE MY RUNS TO DIFFERENT FILES
            currentRun = 0;
            while(reader.hasNextLine()) {
                run[linesRead] = reader.nextLine();
                linesRead++;
                if(linesRead == runSize || !reader.hasNextLine()) {
                    //write strings to a run file

                    for(int i = 0; i < linesRead; i++) {
                        //System.out.println(run[i]);
                        writers[currentRun].write(run[i]);
                    }

                    totalLinesRead += linesRead;
                    linesRead = 0;
                    currentRun++;
                    if(currentRun > 14) {
                        currentRun = 0;
                    }
                } else {
                    run[linesRead - 1] += "\n";
                }
            }
            reader.close();
            for(FileWriter writer : writers) {
                writer.close();
            }



            //switch filewriters to the latter k files and set scanners to
            //first k files
            int currFile = 15;
            for(FileWriter writer : writers) {
                writer = new FileWriter(files[currFile]);
                writers[currFile - 15] = writer;
                currFile++;
            }

            currFile = 0;
            for(Scanner scanner : scanners) {
                scanner = new Scanner(files[currFile]);
                scanners[currFile] = scanner;
                currFile++;
            }

            currFile = 0;
            //the top of each run file
            String[] currentLines = new String[numMerges];
            String[] fileReferences = new String[numMerges];
            int[] fileLinesRead = new int[numMerges];

            for(String s : currentLines) {
                if(scanners[currFile].hasNextLine()) {
                    currentLines[currFile] = scanners[currFile].nextLine();
                    fileReferences[currFile] = currentLines[currFile];
                    System.out.println(fileReferences[currFile]);
                    fileLinesRead[currFile]++;
                } else {
                    if(currFile == 0) {
                        System.out.println("bug");
                        return;
                    }
                    currentRunningFiles = currFile;
                    break;
                }
                currFile++;
            }


            minHeap heap = new minHeap();
            heap.createHeap(currentLines,currentRunningFiles);
            boolean waitNextRun[] = new boolean[numMerges];
            int currentWritingFile = 0;

            while(true) {
                String next = heap.pop();
                if(next == null) {
                    System.out.println("next is null");
                    //no more files to read from, move to next file
                    if(currentRunningFiles == 0) {
                        System.out.println("here");
                        break;
                    }

                    if(currentWritingFile == 15) {
                        System.out.println("or here");
                        currentWritingFile = 0;
                    } else {
                        System.out.println(currentRunningFiles);
                        currentWritingFile++;
                    }
                }

                //check for where the string came from
                for(int i = 0; i < currentRunningFiles; i++) {
                    if(next.compareTo(fileReferences[i]) == 0) {
                        if(!scanners[i].hasNextLine()) {
                            currentRunningFiles--;
                            break;
                        }

                        if(fileLinesRead[i] == 128) {
                            waitNextRun[i] = true;
                            break;
                        }

                        heap.insert(scanners[i].nextLine());
                        fileLinesRead[i]++;
                    }
                }
                //System.out.println(next);
                writers[currentWritingFile].write(next);
            }


            for(FileWriter writer : writers) {
                writer.close();
            }


        } catch(IOException ex) {
            System.out.println("IOException ex: " + ex);
        }
    }

    public static void populateHeap() {

    }
}
