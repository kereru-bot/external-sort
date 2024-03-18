import java.io.*;
import java.util.Scanner;

class xSort {
    static int runSize;
    static String filename;
    static int numMerges;
    static int maxRunningFiles;
    static File[] files;
    static Scanner[] scanners;
    static FileWriter[] writers;
    static String[] fileReferences;
    static String[] currentLines;
    static boolean[] dontReadFile;
    static int[] fileLinesRead;
    static int currentRunningFiles;

    public static void main(String args[]) {
        runSize = Integer.parseInt(args[0]);
        filename = args[1];
        //number of files to be merged on each pass
        numMerges = Integer.parseInt(args[2]);
        maxRunningFiles = numMerges;
        //file and scanner arrays for the number of merges to do
        files = new File[numMerges * 2];
        scanners = new Scanner[numMerges];
        writers = new FileWriter[numMerges];

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
                    if(currentRun == 15) {
                        currentRun = 0;
                        maxRunningFiles = 15;
                    } else {
                        maxRunningFiles = currentRun;
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

            //POPULATE MY HEAP TO BE USED HERE

            dontReadFile = new boolean[numMerges];
            minHeap heap = populateHeap();
            //heap.createHeap(currentLines,currentRunningFiles);
            boolean waitNextRun[] = new boolean[numMerges];

            int currentWritingFile = 0;

            while(true) {
                String next = heap.pop();
                if(next == null) {
                    System.out.println("next is null");
                    //no more files to read from, move to next file
                    if(maxRunningFiles == 0) {
                        System.out.println("here");
                        break;
                    }

                    if(currentWritingFile == 15) {
                        System.out.println("or here");
                        currentWritingFile = 0;
                    } else {
                       // System.out.println(currentRunningFiles);
                        currentWritingFile++;
                    }

                    heap = populateHeap();
                    if(heap == null) {
                        System.out.println("BREAKING");
                        break;
                    }
                    next = heap.pop();
                    System.out.println("NEXT: " + next);

                }

                //check for where the string came from
                for(int i = 0; i < maxRunningFiles; i++) {
                    System.out.println(i);
                    if(!dontReadFile[i] && next.compareTo(fileReferences[i]) == 0) {
                        if(!scanners[i].hasNextLine()) {
                            dontReadFile[i] = true;
                            currentRunningFiles--;
                            break;
                        }

                        if(fileLinesRead[i] == 128) {
                            waitNextRun[i] = true;
                            break;
                        }
                        //not working properly
                        String s = scanners[i].nextLine();
                        fileReferences[i] = s;
                        //System.out.println(s + " i" + i);
                        //System.out.print("NEXT IS: " + s);
                        heap.insert(s);
                        fileLinesRead[i]++;
                        break;
                    }
                }
                System.out.println(next);
                writers[currentWritingFile].write(next + "\n");
            }


            for(FileWriter writer : writers) {
                writer.close();
            }


        } catch(IOException ex) {
            System.out.println("IOException ex: " + ex);
        }
    }

    public static minHeap populateHeap() {
        int currFile = 0;
        //the top of each run file
        currentLines = new String[numMerges];
        fileReferences = new String[numMerges];
        fileLinesRead = new int[numMerges];
        int filesReadFrom = 0;
        for(String s : currentLines) {
            if(!dontReadFile[currFile]) {
                if(scanners[currFile].hasNextLine()) {
                    currentLines[currFile] = scanners[currFile].nextLine();
                    fileReferences[currFile] = currentLines[currFile];
                    System.out.println("FILE REF: ");
                    filesReadFrom++;
                    System.out.println(fileReferences[currFile]);
                    fileLinesRead[currFile]++;
                } else {
                    if(areRunsRead() == true) {
                        System.out.println("bug");
                        return null;
                    }
                    //currentRunningFiles = currFile;
                    break;
                }
                currFile++;
            }
        }
        //bandaid solution lmao
        if(filesReadFrom == 0) {
            return null;
        }
        System.out.println("F READ FROM: " + filesReadFrom);
        minHeap heap = new minHeap();
        heap.createHeap(currentLines,filesReadFrom);
        return heap;
    }

    public static boolean areRunsRead() {
        for(int i = 0; i < maxRunningFiles; i++) {
            if(dontReadFile[i] == false) {
                return false;
            }
        }
        return true;
    }
}
