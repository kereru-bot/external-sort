import java.io.*;

class MakeRuns {
    public static void main(String[] args) {
        int numRuns = Integer.parseInt(args[0]);
        String filename = args[1];

        File file = new File(filename);

        try {
            FileReader reader = new FileReader(file);

            char[] currentChar = new char[1];
            int offset = 0;
            int length = 1;
            int linesRead = 0;
            while(reader.read(currentChar, offset, length) != -1) {
                //will this work on windows generated text files?
                if(currentChar[0] == '\n') {
                    linesRead++;
                }
                System.out.print(currentChar[0]);
            }
        } catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " + ex);
        } catch (IOException ex) {
            System.err.println("IOException: " + ex);
        }


        System.out.flush();

        return;
    }
}
