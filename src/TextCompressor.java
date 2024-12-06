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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static Map<Integer, String> codeToSequence = new HashMap<>();

    private static void buildTable(String input) {
        for (int i = 0; i < input.length(); i++) {
            for (int j = i + 1; j <= input.length() && j <= i + MAX_SEQUENCE_LENGTH; j++) {
                String sequence = input.substring(i, j);
                sequenceCounts.put(sequence, sequenceCounts.getOrDefault(sequence, 0) + 1);
            }
        }

        // Get the 2^n most common sequences
        int limit = (int) Math.pow(2, MAX_CODE_LENGTH);
        List<String> sequences = new ArrayList<>(sequenceCounts.keySet());
        // Used https://stackoverflow.com/questions/2839137/how-to-use-comparator-in-java-to-sort
        // To learn how to do this part
        sequences.sort((a, b) -> sequenceCounts.get(b) - sequenceCounts.get(a));

        for (int i = 0; i < limit; i++) {
            sequenceToCode.put(sequences.get(i), i);
        }
    }

    private static void compress() {
        String input = BinaryStdIn.readString();
        buildTable(input);

        // Write the size of the sequenceToCode map
        BinaryStdOut.write(sequenceToCode.size());

        // Write the sequenceToCode map
        for (String sequence : sequenceToCode.keySet()) {
            BinaryStdOut.write(sequence);
            BinaryStdOut.write(sequenceToCode.get(sequence), MAX_CODE_LENGTH);
        }

        // Write the compressed data
        for (int i = 0; i < input.length(); ) {
            boolean found = false;
            for (int j = i; j < input.length() && j < i + MAX_SEQUENCE_LENGTH; j++) {
                String sequence = input.substring(i, j + 1);
                if (sequenceToCode.containsKey(sequence)) {
                    BinaryStdOut.write(sequenceToCode.get(sequence), MAX_CODE_LENGTH);
                    i = j + 1;
                    found = true;
                    break;
                }
            }
            if (!found) {
                BinaryStdOut.write(input.charAt(i));
                i++;
            }
        }

        BinaryStdOut.close();
    }

    private static void expand() {
        // Read the size of the codeToSequence map
        int size = BinaryStdIn.readInt(MAX_CODE_LENGTH);

        // Read the codeToSequence map
        for (int i = 0; i < size; i++) {
            String sequence = BinaryStdIn.readString();
            int code = BinaryStdIn.readInt(MAX_CODE_LENGTH);
            codeToSequence.put(code, sequence);
        }

        // Read and write the compressed data
        while (!BinaryStdIn.isEmpty()) {
            int code = BinaryStdIn.readInt(MAX_CODE_LENGTH);
            if (codeToSequence.containsKey(code)) {
                String sequence = codeToSequence.get(code);
                BinaryStdOut.write(sequence);
            } else {
                BinaryStdOut.write((char) code);
            }
        }

        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}

