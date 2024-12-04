/******************************************************************************
 *  Compilation:  javac TextCompressor.java
 *  Execution:    java TextCompressor - < input.txt   (compress)
 *  Execution:    java TextCompressor + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   abra.txt
 *                jabberwocky.txt
 *                shakespeare.txt
 *                virus.txt
 *
 *  % java DumpBinary 0 < abra.txt
 *  136 bits
 *
 *  % java TextCompressor - < abra.txt | java DumpBinary 0
 *  104 bits    (when using 8-bit codes)
 *
 *  % java DumpBinary 0 < alice.txt
 *  1104064 bits
 *  % java TextCompressor - < alice.txt | java DumpBinary 0
 *  480760 bits
 *  = 43.54% compression ratio!
 ******************************************************************************/

import java.util.HashMap;
import java.util.Map;

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, Landon Moceri
 */
public class TextCompressor {

    private static final int MAX_SEQUENCE_LENGTH = 10;
    private static final int MAX_CODE_LENGTH = 8;

    private static Map<String, Integer> sequenceCounts = new HashMap<>();
    private static Map<String, Integer> sequenceToCode = new HashMap<>();

    private static void buildTable(String input) {
        for (int i = 0; i < input.length(); i++) {
            for (int j = i + 1; j <= input.length() && j <= i + MAX_SEQUENCE_LENGTH; j++) {
                String sequence = input.substring(i, j);
                sequenceCounts.put(sequence, sequenceCounts.getOrDefault(sequence, 0) + 1);
            }
        }

        // Get the 2^n most common sequences
        

    }

    private static void compress() {

        // Plan:
        // 1. Read in the entire input file as a string
        // 2. For each character in the string, create a sequence of characters up to length 10
        // 3. If the sequence is not in the map, add it with a count of 1
        // 4. If the sequence is in the map, increment the count
        // 5. Figure out some way to choose an optimal size for the codes corresponding to each sequence
        // 6. Find the 2^n most common sequences and assign them codes of length n
        // 7. Create a header with this information
        // 8. Write the header to the output file
        // 9. Compress using the codes

        // TODO: Complete the compress() method

        BinaryStdOut.close();
    }

    private static void expand() {

        // TODO: Complete the expand() method

        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
