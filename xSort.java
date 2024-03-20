import java.io.*;

class xSort {
    public static int totalRunSize;
    public static void main(String args[]) {
        totalRunSize = Integer.parseInt(args[0]);
        String filename = args[1];
        //number of files to be merged on each pass
        int mergesPerPass = Integer.parseInt(args[2]);
        //file and scanner arrays for the number of merges to do

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
       //int j = 1;
        //while(true) {
         //   if(j == 0) {
          //      break;
         //  }
       // }
        while(true) {
            try {
                maxFiles = merge(totalRunSize, left, right, maxFiles);
                //int j = 1;
                //while(true) {
               //     if(maxFiles == -1) {
               //         System.out.println("true");
                //    }
               //     if(j == 0) {

                 //       break;
              //      }
             //   }
                if(maxFiles == 1) {
                    BufferedReader reader = new BufferedReader(new FileReader("r_0"));
                    String next;
                    while((next = reader.readLine()) != null) {
                    //write file 1 from the right side
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
                //System.out.println(maxFiles);


                maxFiles = merge(totalRunSize, right, left, maxFiles);

                    //write from file 1 on the left side
                if(maxFiles == 1) {
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

    //return max files written to
    public static int merge(int runSize, File[] reading, File[] writing, int maxFiles) {
        //System.out.println(maxFiles);
        minHeap heap = populateHeap(reading, maxFiles);
        FileWriter[] writers =  new FileWriter[maxFiles];
        boolean stillReading = true;
        int currReading = 0;
        int currWriting = 0;
        int numTempNodes = 0;
        int numWrittenFiles = 1;
        minHeap tempHeap;
        strNode[] tempNodes = new strNode[maxFiles];
        //boolean firstLine = true;
        boolean[] notFirstLine = new boolean[maxFiles];
        try {
            for(int i = 0; i < writers.length; i++) {
                writers[i] = new FileWriter(writing[i]);
            }
            //FileWriter writer = new FileWriter(writing[currWriting], true);
            while(stillReading) {
                //System.out.println(currReading);
                strNode next = heap.pop();
                if(next == null) {
                    //nothing left in the heap, check tempnodes

                    if(numTempNodes != 0) {
                        strNode[] nodes = new strNode[numTempNodes];
                        //System.out.println(nodes.length);
                        for(int i = 0; i < tempNodes.length; i++) {
                            if(tempNodes[i] != null) {
                                //System.out.println("READING");
                                nodes[numTempNodes - 1] = tempNodes[i];
                                //nodes[numTempNodes - 1].string = nodes[numTempNodes - 1].reader.readLine();
                                //System.out.println(nodes[numTempNodes - 1].string == null);
                                numTempNodes--;
                                //System.out.println("MADE IT");
                            }
                        }
                        for(strNode node : nodes) {
                            //System.out.println(node);
                        }
                        heap = new minHeap();
                        heap.createHeap(nodes,nodes.length);
                        tempNodes = new strNode[maxFiles];
                        currReading = 0;
                        currWriting++;
                        numWrittenFiles++;
                        //writer.close();
                        if(currWriting == maxFiles) {
                            //System.out.println("this is okay " + maxFiles);
                            //numWrittenFiles = maxFiles;
                            currWriting = 0;
                            //firstPass = false;
                        }

                        //if(!firstPass) {
                        //    writers[currWriting].write("\n");
                        //}
                        //writer = new FileWriter(writing[currWriting]);
                    } else {
                        //merging must be done
                       // writer.close();
                        //System.out.println(totalRunSize);
                        totalRunSize *= maxFiles;
                        for(int i = 0; i < writers.length; i++) {
                            writers[i].close();
                        }

                        if(numWrittenFiles > maxFiles) {
                            numWrittenFiles = maxFiles;
                        }
                        //System.out.println(maxFiles);
                        return numWrittenFiles;
                    }
                } else {
                    //System.out.println(next.string);
                    if(notFirstLine[currWriting]) {
                        writers[currWriting].write("\n");
                    }
                    writers[currWriting].write(next.string);
                    notFirstLine[currWriting] = true;
                    next.linesRead++;
                    if(next.linesRead == runSize) {
                        //writer.write("\n");
                        //System.out.println("READING");
                        next.string = next.reader.readLine();
                        //System.out.println("MADE IT");
                        if(next.string != null) {
                            tempNodes[currReading] = next;
                            numTempNodes++;
                            //System.out.println(currReading);
                            currReading++;
                            next.linesRead = 0;
                        } else {
                            //System.out.println("WHY HERE?");
                            next.reader.close();
                        }
                        //store node to be used again next run
                    } else {
                        next.string = next.reader.readLine();
                        if(next.string == null) {
                            //writers[currWriting].write("\n");
                            //System.out.println("CLOSE HERE");
                            next.reader.close();
                            //System.out.println(currReading);
                            //end of file, no need to do anything
                            currReading++;
                        } else {
                            //writers[currWriting].write("\n");
                            heap.insert(next);
                        }
                    }
                }

                //write lines from reading file to writing file
            }
        } catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " + ex);
            return -1;
        } catch (IOException ex) {
            System.err.println("IOException: " + ex);
            return -1;
        }
        return -1;
    }
    

    public static int distributeRuns(File[] files, String filenameRuns, int runSize) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filenameRuns));
            String next;
            FileWriter[] writers = new FileWriter[files.length];
            //FileWriter writer = new FileWriter(files[0], true);
            int linesRead = 0;
            int currFile = 0;
            int maxFiles = 1;
            boolean newLine = false;
            boolean firstPass = true;
            for(int i = 0; i < files.length; i++) {
                writers[i] = new FileWriter(files[i]);
            }


            while((next = reader.readLine()) != null) {
                //if(next.compareTo("\n") == 0) {
                //    newLine = true;
                //}
                writers[currFile].write(next);

                linesRead++;
                if(linesRead == runSize) {

                    currFile++;
                    maxFiles++;
                    if(currFile == files.length) {
                        currFile = 0;
                        firstPass = false;
                    }
                    if(!firstPass) {
                        writers[currFile].write("\n");
                    }
                    //System.out.println(currFile);
                    linesRead = 0;
                } else {
                    writers[currFile].write("\n");
                    //new line characters on everything but the last line
                    //if(!newLine) {
                    //    writer.write("\n");
                    //}
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
        //now create and return initial heap
    }

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
