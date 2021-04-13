import java.util.Arrays;
import java.util.Scanner;

/**
 * Simple brainfuck interpreter for demonstration purposes. Does not support reading input from code (",").
 * 
 * @author stuebingerb
 */
public class BrainUnFucker {
    
    /**
     * Simple command line wrapper for BrainUnFucker.
     * 
     * @param args
     */
    public static void main(String[] args) {
        int memSize = 10;
        BrainUnFucker buf = new BrainUnFucker(memSize);

        Scanner scan = new Scanner(System.in);
        while (true) {
            buf.printMemory();
            System.out.println();
            System.out.print("Input:  ");
            String line = scan.nextLine();
            switch (line) {
                // "q" and empty input aborts the program
                case "q" -> System.exit(0);
                // "c" or "r" clears/resets memory and pointer position
                case "c", "r" -> buf.init();
                // "i" increases memory
                case "i" -> buf.increaseMemory();
                // Everything else is treated as brainfuck commands
                default -> {
                    String output = buf.interpret(line);
                    if (!output.isEmpty()) {
                        System.out.println("Output: " + output);
                    } else {
                        System.out.println("(No output)");
                    }
                }
            }
        }
    }

    /**
     * The available cells.
     */
    private int memSize;
    /**
     * The current memory.
     */
    private byte[] memory;
    /**
     * The current pointer.
     */
    private int ptr = 0;
    /**
     * The tab size to use when printing memory.
     */
    private int tabSize = 4;

    /**
     * Creates and initializes a new instance with the given number of cells.
     * 
     * @param memSize available memory size / number of cells
     */
    public BrainUnFucker(int memSize) {
        this.memSize = memSize;
        init();
    }

    /**
     * Initializes all cells with "0" and moves the pointer to the first cell.
     */
    public void init() {
        memory = new byte[memSize];
        ptr = 0;
    }

    /**
     * Prints the current memory and pointer position with the default heading and without indentation.
     */
    public void printMemory() {
        printMemory("Current memory:", 0);
    }

    /**
     * Prints the current memory and pointer position with the given heading and indentation.
     * 
     * @param heading the heading to use
     * @param indentation number of whitespace characters to prefix
     */
    public void printMemory(String heading, int indentation) {
        indent(indentation);
        System.out.println();
        indent(indentation);
        System.out.println(heading);
        indent(indentation);
        for (int i = 0; i < memory.length; i++) {
            System.out.printf("|#%-3d ", i);
        }
        System.out.println("|");
        indent(indentation);
        for (int i = 0; i < memory.length; i++) {
            // Simulate unsigned byte
            System.out.printf("| %3d ", memory[i] & 0xFF);
        }
        System.out.println("|");
        indent(indentation);
        for (int i = 0; i < memory.length; i++) {
            if (i == ptr) {
                System.out.print("   ^  ");
            } else {
                System.out.print("      ");
            }
        }
        System.out.println();
    }

    /**
     * Adds two (empty) memory cells.
     */
    public void increaseMemory() {
        memSize += 2;
        memory = Arrays.copyOf(memory, memSize);
    }

    /**
     * Indents the current output line by indentation.
     * 
     * @param indentation number of whitespace characters to prefix
     */
    private void indent(int indentation) {
        for (int i = 0; i < indentation; i++) {
            System.out.print(" ");
        }
    }

    /**
     * Interprets the current line as brainfuck commands and returns the corresponding output (if any).
     * A line starting with "vv" will switch to very verbose mode, where the memory content is printed after
     * each processed character.
     * A line starting with "v" will switch to a regular (lesser) verbose mode, where the memory content is
     * printed every time the processed character changes (i.e. for "+++++++-" there will be only 2 outputs).
     * 
     * @param line sequence of brainfuck commands to process
     * @return the output produced by line; empty string if line did not contain any output commands (".")
     */
    public String interpret(String line) {
        String output = "";
        int loopCount = 0;
        int loopIndent = 0;
        int verbosity = 0;
        int commandNo = 0;
        String commandInput = "";
        String commandExtra = "";
        int previousOutputLength = 0;

        if (line.startsWith("vv")) {
            verbosity = 2;
        } else if (line.startsWith("v")) {
            verbosity = 1;
        }

        for (int i = verbosity; i < line.length(); i++) {
            char next = (i < line.length() - 1) ? line.charAt(i + 1) : Character.MIN_VALUE;
            char current = line.charAt(i);
            commandNo++;
            commandInput += String.valueOf(current);
            commandExtra = "";
            // Process bf commands
            switch (current) {
                // Increment value at pointer; 255+ is 0
                case '+' -> memory[ptr]++;
                // Decrement value at pointer; 0- is 255
                case '-' -> memory[ptr]--;
                // Print value at pointer as char
                case '.' -> output += (char) memory[ptr];
                // Read from stdin (not implemented)
                case ',' -> {}
                // Move pointer to the right; stops at memory.length
                case '>' -> {
                    if (ptr < memory.length - 1) {
                        ptr++;
                    } else {
                        System.out.println("Tried to access cell #" + memory.length + " which exceeds current memory");
                        return output;
                    }
                }
                // Move pointer to the left; stops at 0
                case '<' -> {
                    if (ptr > 0) {
                        ptr--;
                    } else {
                        System.out.println("Tried to access cell #-1 which is illegal");
                        return output;
                    }
                }
                // Start a loopCount; [ jumps to the matching ] if value at pointer is 0
                case '[' -> {
                    loopIndent++;
                    if (verbosity == 2) {
                        commandExtra = " (memory[#" + ptr + "] = " + memory[ptr] + (memory[ptr] > 0 ? " >" : " ==") + " 0)";
                    }
                    if (memory[ptr] == 0) {
                        i++;
                        while (loopCount > 0 || line.charAt(i) != ']') {
                            if (line.charAt(i) == '[') {
                                loopCount++;
                            } else if (line.charAt(i) == ']') {
                                loopCount--;
                            }
                            i++;
                        }
                    }
                }
                // End a loopCount; ] jumps back to the matching [ if value at pointer is != 0
                case ']' -> {
                    loopIndent--;
                    if (verbosity == 2) {
                        commandExtra = " (memory[#" + ptr + "] = " + memory[ptr] + (memory[ptr] > 0 ? " >" : " ==") + " 0)";
                    }
                    if (memory[ptr] != 0) {
                        i--;
                        while (loopCount > 0 || line.charAt(i) != '[') {
                            if (line.charAt(i) == ']') {
                                loopCount++;
                            } else if (line.charAt(i) == '[') {
                                loopCount--;
                            }
                            i--;
                        }
                        i--;
                    }
                }
            }
            // Print verbose output if requested
            if (verbosity == 2 || (verbosity == 1 && next != current)) {
                int loopIndentToUse = loopIndent;
                if (current == '[') {
                    loopIndentToUse--;
                }
                printMemory("(" + commandNo + ") After processing '" + commandInput + "'" + commandExtra + ":", tabSize * (loopIndentToUse + 1));
                if (current == '.') {
                    indent(tabSize * (loopIndentToUse + 1));
                    System.out.println("Command output: " + output.substring(previousOutputLength));
                    previousOutputLength = output.length();
                }
                // Reset
                commandInput = "";
            }
        }
        if (verbosity > 0) {
            System.out.println();
        }
        return output;
    }
}
