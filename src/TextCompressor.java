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
public class TextCompressor {

    static TST tst;
    static final int CODE_SIZE = 12;
    static int code = 257;
    static final int EOF = 256;

    /*
        read data into String text
index = 0
while index < text.length:
	prefix = longest coded word that matches text @ index
	write out that code
	if possible, look ahead to the next character
	append that character to prefix
	associate prefix with the next code (if available)
	index += prefix.length
write out EOF and close
     */
    private static void compress() {
        String input = BinaryStdIn.readString();
        int index = 0;
        while (index < input.length()) {
            String prefix = tst.getLongestPrefix(input.substring(index));
            int code = tst.lookup(prefix);
            BinaryStdOut.write(code, CODE_SIZE);
            if (index + prefix.length() < input.length()) {
                prefix += input.charAt(index + prefix.length());
                tst.insert(prefix, code);
                code++;
            }
            index += prefix.length();
        }

        BinaryStdOut.write(EOF, CODE_SIZE);
        BinaryStdOut.close();
    }

    /*
    When expanding, if we see a code that doesn't exist yet, we know it must be the next code.

    Its String is given to us by appending to our current prefix, p:

	new String = p + p's first letter
     */
    private static void expand() {
        int current_base = BinaryStdIn.readInt(CODE_SIZE);
        while (code != EOF) {
            int next_base = BinaryStdIn.readInt(CODE_SIZE);
            // The current code is a character, so we can just write it out
            if (current_base < 256)
            {
                BinaryStdOut.write(current_base, 8);
                tst.insert((char)current_base + , code);
            }
        }
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}

