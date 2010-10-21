package com.jeremymikkola.dfabuilder;

import java.io.PrintStream;

/**
 * The Main class handles the user input (command line arguments)
 * and orchestrates the rest of the program.
 * @version 2010.10.20 
 * @author Jeremy Mikkola
 */
public class Main {

    /**
     * Runs the program.
     * If there are no arguments or there are invalid arguments,
     * this prints the usage. When there are invalid arguments,
     * the errors are also printed. 
     * Otherwise, it creates a program that counts the words
     * given as arguments and prints it. 
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0 && checkArgs(args)) {
            StringTree tree = new StringTree();
            for (String s : args)
                tree.add(s);

            ProgramBuilder pb = new ProgramBuilder(tree,
                    new PrintStream(System.out));
            pb.printProgram();
        } else {
            printUsage();
        }
    }

    /**
     * Prints the program's usage
     */
    private static void printUsage() {
        System.out.println("\nUsage: ");
        System.out.println("Run with the words as the arguments " + 
                "to create a C++ file.");
        System.out.println("The resulting program is written to "
                + "standard output.\n");
    }

    /**
     * Checks the supplied arguments to see if they contain
     * anything invalid.
     * Returns false if there is an error, true if there is not.
     * Prints details of every error to std err. 
     * @param args
     * @return true if arguments are OK. 
     */
    private static boolean checkArgs(String[] args) {
        boolean allGood = true;
        for (int i = 0; i < args.length; i++) {
            allGood &= check(args[i], i);
        }
        return allGood;
    }

    /**
     * Checks a single argument, printing any errors.
     * if there is an error, this returns false. Otherwise,
     * it returns true.
     * @param argument The argument to check
     * @param i The position in the list of arguments that this
     * one appears in.
     * @return true if the argument is OK. 
     */
    private static boolean check(String argument, int i) {
        boolean result = true;
        if (argument.isEmpty()) {
            System.err.printf("Argument %d was empty\n", i);
            result = false;
        }
        if (argument.matches(".*\\s.*")) {
            System.err.printf("Argument \"%s\" "
                    + "contained a whitespace character.\n",
                    argument);
            result = false;
        }
        return result;
    }

}
