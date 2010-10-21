package com.jeremymikkola.dfabuilder;

import java.util.*;

/**
 * The StringTree class does the heavy lifting in this program,
 * in conjuction with the Node class. The StringTree is essentially
 * the backbone of a substring DFA made using Node objects, with
 * some added functionality to create the final DFA. 
 * This class is used to transform a list of words into a transition table.
 * To use, create an instance, and call .add(String) for each word to be added
 * to the table. Once finished, the useful pieces of data can be retrieved with
 * transitionTable(), getWords(), and getIncrements(). 
 * @version 2010.10.20 
 * @author Jeremy Mikkola
 */
public class StringTree {

    private int count;
    private TreeMap<String, Node> increments;
    private TreeMap<String, Node> prefixTable;
    private TreeSet<Character> letters;
    private Node root;

    /**
     * Creates a new empty StringTree,
     * with a 1 state DFA.
     */
    public StringTree() {
        count = 0;
        increments = new TreeMap<String, Node>();
        prefixTable = new TreeMap<String, Node>();
        letters = new TreeSet<Character>();
        root = new Node(this);
    }

    /**
     * Adds another string to the DFA.
     * @param s
     */
    public void add(String s) {
        addLetters(letters, s);
        root.add("", s);
    }

    /**
     * Used for getting the next ID to be given to a Node when it
     * is created. This increments a counter, so do not call it outside
     * of the Node class. 
     * @return the id
     */
    public int count() {
        int out = count;
        count++;
        return out;
    }

    /**
     * Adds a new state to the transition table. This is to be
     * used by the node class.
     * @param prefix The prefix found when the node is reached.
     * @param node The state reached by the prefix
     */
    public void addState(String prefix, Node node) {
        prefixTable.put(prefix, node);
    }

    /**
     * Adds a final state. This so that the Node class can
     * tell this class which states are states where a whole string has been
     * read. 
     * @param prefix
     * @param node
     */
    public void addFinal(String prefix, Node node) {
        increments.put(prefix, node);
    }

    /**
     * Returns the ID of the root node. 
     * @return root ID
     */
    public int getRootId() {
        return root.id();
    }

    /**
     * Returns a TreeSet of the unique letters in the words
     * that the machine counts.
     * Note: do not modify the object returned, because it is
     * used internally. 
     * @return Set of unique letters
     */
    public TreeSet<Character> uniqueLetters() {
        return letters;
    }

    /**
     * Returns the number of unique letters in the words
     * that the machine counts.
     * @return number of letters
     */
    public int numLetters() {
        return letters.size();
    }

    /**
     * Returns the transition table for the machine.
     * This uses both the tree created using the Node objects
     * and the prefix table by means of the getTransition() function.
     * @return transition table
     */
    public int[][] transitionTable() {
        int[][] table = emptyTable();
        int start = root.id();
        for (String key : prefixTable.keySet()) {
            Node state = prefixTable.get(key);
            int num = state.id();
            TreeMap<Character, Integer> tranList
                    = state.getTransitions(letters);
            
            int i = 0;
            for (char c : letters) {
                int to = tranList.get(c);
                // If it loops back to root, search for a prefix transition
                if (to == start)
                    to = getTransition(state, c);
                table[num][i] = to;
                i++;
            }
        }
        return table;
    }

    /**
     * Adds the letters from the string to the set.
     * @param letters The set to add letters to.
     * @param s The words containing the letters to add.
     */
    private void addLetters(Set<Character> letters, String s) {
        char[] arr = s.toCharArray();
        for (Character c : arr) {
            letters.add(c);
        }
    }

    /**
     * Creates an empty table of the correct dimentions to be used
     * as a transition table.
     * @return empty transition table. 
     */
    private int[][] emptyTable() {
        int width = 1 + numLetters();
        return new int[count][width];
    }

    /**
     * Returns the transition from a given state on a given letter.
     * This assumes that the state does not have an explicit transition
     * on that letter, and a prefix transition must be found. 
     * @param state The state to find the transition from
     * @param letter The letter to transition on
     * @return The state to transition to
     */
    private int getTransition(Node state, char letter) {
        String prefix = state.prefix() + letter;
        int length = prefix.length();
        for (int i = 0; i < length; i++) {
            if (prefixTable.containsKey(prefix)) {
                return prefixTable.get(prefix).id();
            }
            prefix = prefix.substring(1, prefix.length());
        }
        return 0;
    }

    /**
     * Returns the string of root.show(). This is used to display
     * the tree.
     * @return a string representation of the StringTree
     */
    public String show() {
        return root.show();
    }

    /**
     * Returns an array containing the words counted in the machine.
     * This array may be safely modified. 
     * @return the words counted.
     */
    public String[] getWords() {
        Set<String> wordSet = increments.keySet();
        int size = wordSet.size();
        String[] words = new String[size];
        Iterator<String> it = wordSet.iterator();
        for (int i = 0; i < size; i++) {
            words[i] = it.next();
        }
        return words;
    }

    /**
     * Returns a map between words and the nodes where
     * the words are incremented.
     * Node: do not modify the object that this returns,
     * because it is used internally.
     * @return The increment states. 
     */
    public TreeMap<String, Node> getIncrements() {
        return increments;
    }

}
