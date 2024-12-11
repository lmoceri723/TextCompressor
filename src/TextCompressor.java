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

    static final int CODE_SIZE = 12;
    static final int EOF = 256;

    private static void compress() {
        TST tst = new TST();
        for (int i = 0; i < 256; i++) {
            tst.insert("" + (char) i, i);
        }
        int code = 257;

        String input = BinaryStdIn.readString();
        int index = 0;
        while (index < input.length()) {
            String prefix = tst.getLongestPrefix(input.substring(index));
            int local_code = tst.lookup(prefix);
            BinaryStdOut.write(local_code, CODE_SIZE);
            int prefixLength = prefix.length();
            if (index + prefixLength < input.length() && code < 4096) {
                tst.insert(input.substring(index, index + prefixLength + 1), code);
                code++;
            }
            index += prefixLength;
        }
        BinaryStdOut.write(EOF, CODE_SIZE);  // Write EOF
        BinaryStdOut.close();
    }

    private static void expand() {
        String[] codeToString = new String[(int) Math.pow(2, CODE_SIZE)];
        for (int i = 0; i < 256; i++) {
            codeToString[i] = "" + (char) i;
        }
        int code = 257;

        int current_base = BinaryStdIn.readInt(CODE_SIZE);
        if (current_base == EOF) return; // expanded message is empty string
        String current_string = codeToString[current_base];

        while (true) {
            BinaryStdOut.write(current_string);
            int next_base = BinaryStdIn.readInt(CODE_SIZE);
            if (next_base == EOF) {
                break;
            }
            String next_string;
            if (next_base < code) {
                next_string = codeToString[next_base];
            } else {
                next_string = current_string + current_string.charAt(0);
            }
            if (code < codeToString.length) {
                codeToString[code] = current_string + next_string.charAt(0);
                code++;
            }
            current_string = next_string;
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}

