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

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, Landon Moceri
 */

// TODO final day is Wednesday in the morning
public class TextCompressor {

    // Constants
    static final int CODE_SIZE = 12;
    static final int MAX_CODE = 4096;
    static final int NUM_ASCII_CHARS = 256;
    static final int EOF = 256;

    // Compresses text input using LZW compression
    private static void compress() {
        // Initialize a TST to facilitate string to code translations
        TST tst = new TST();
        // Add all the ASCII characters to the TST
        for (int i = 0; i < NUM_ASCII_CHARS; i++) {
            tst.insert("" + (char) i, i);
        }
        // Initialize the code to be the first available code after ASCII and EOF
        int code = NUM_ASCII_CHARS + 1;

        // Read the input as a string
        String input = BinaryStdIn.readString();
        // Loop through each character in the input
        int index = 0;
        while (index < input.length()) {
            // Get the longest prefix in the TST that matches the current substring
            String prefix = tst.getLongestPrefix(input, index);
            // Get the code for the prefix
            int local_code = tst.lookup(prefix);

            // Write the code to the output
            BinaryStdOut.write(local_code, CODE_SIZE);
            // Add the next character to the TST
            int prefixLength = prefix.length();
            if (index + prefixLength < input.length() && code < MAX_CODE) {
                tst.insert(input.substring(index, index + prefixLength + 1), code);
                // Increment the code
                code++;
            }
            // Move the index to the next character
            index += prefixLength;
        }
        // Write EOF to the output and close the output
        BinaryStdOut.write(EOF, CODE_SIZE);
        BinaryStdOut.close();
    }

    // Expands LZW compressed text back to its original form
    private static void expand() {
        // Create a map from codes to strings
        String[] codeToString = new String[(int) Math.pow(2, CODE_SIZE)];
        // Fill it with the ASCII characters
        for (int i = 0; i < NUM_ASCII_CHARS; i++) {
            codeToString[i] = "" + (char) i;
        }

        // Initialize the code to be the first available code after ASCII and EOF
        int code = NUM_ASCII_CHARS + 1;

        // Read the first code from the input
        int current_base = BinaryStdIn.readInt(CODE_SIZE);
        // If the input is EOF, we're done
        if (current_base == EOF)
        {
            return;
        }
        // Get the string for the first code
        String current_string = codeToString[current_base];

        while (true) {
            // Write the current string to the output
            BinaryStdOut.write(current_string);
            // Get the next base, handle EOF, and get the next string
            int next_base = BinaryStdIn.readInt(CODE_SIZE);
            if (next_base == EOF) {
                break;
            }
            String next_string;
            if (next_base < code) {
                // If the next base is in the map, get the string
                next_string = codeToString[next_base];
            } else {
                // If the next base is not in the map, it has to be the edge case
                // That we talked about in class. The next string can only be the current string
                // Plus the first character of the current string
                next_string = current_string + current_string.charAt(0);
            }
            // Add the current string plus the first character of the next string to the map
            if (code < codeToString.length) {
                codeToString[code] = current_string + next_string.charAt(0);
                // Increment the code
                code++;
            }
            // Set the current string to the next string for the next iteration
            current_string = next_string;
        }
        // Close the output when done
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}

