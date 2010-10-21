package com.jeremymikkola.dfabuilder;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * The Node class is used to build the structure of a StringTree.
 * Each node has a unique id and prefix.
 * The ID corresponds to the number of the state in the
 * final transition table. The prefix is the string required to get
 * to this portion of the tree.
 * Each node also stores the connections to the nodes below it, along
 * with the letters that are taken when going to those nodes.
 * @version 2010.10.20 
 * @author Jeremy Mikkola
 */
public class Node {

    private TreeMap<Character, Node> below;
    private StringTree tree;
    private int id;
    private String prefix;

    /**
     * Creates a new node with all fields blank.
     * This should only be used to create a root of a tree.
     * @param tree The object containing the tree
     */
    public Node(StringTree tree) {
        this(tree, null, null);
    }

    /**
     * Creates a new node.
     * This adds necessary sub-nodes to complete the subtree for
     * the string that it is passed.
     * The Node also adds itself to the prefix table of the StringTree.
     * @param tree The object containing the tree
     * @param prefix The string encountered so far
     * @param string The remaining string to build a tree out of.
     */
    public Node(StringTree tree, String prefix, String string) {
        below = new TreeMap<Character, Node>();
        this.tree = tree;
        id = tree.count();
        if (prefix != null && prefix.length() > 0) {
            this.prefix = prefix;
            add(prefix, string);
        } else {
            this.prefix = "";
        }
        tree.addState(this.prefix, this);
    }

    /**
     * Adds a new string to the subtree rooted a this node.
     * All necessary child nodes are created and attached automatically. 
     * @param prefix The string encountered so far
     * @param string The remaining string to build a tree out of. 
     */
    public void add(String prefix, String string) {
        if (string != null && string.length() > 0) {
            // Remove the first character from string and
            // add it to the end of prefix
            Character c = string.charAt(0);
            prefix += c;
            string = string.substring(1, string.length());

            if (below.containsKey(c)) {
                // If the prefix so far is already in tree,
                // send the string down to child Node
                below.get(c).add(prefix, string);
            } else {
                // Otherwise, create the new sub tree for the
                // remaining string
                Node sub = new Node(tree, prefix, string);
                below.put(c, sub);
            }
        } else {
            tree.addFinal(prefix, this);
        }
    }

    /**
     * Returns a TreeMap between letters and IDs for the transitions
     * to the child nodes of this Node. If any of the letters in the
     * passed set are not transitions out of this Node, the ID given is
     * that of the root node.
     * Note that this does not produce the complete set of transitions out
     * of the state. Prefix transitions are added later.
     * @param letters The letters to give transitions for
     * @return A map giving an ID to transition to on each letter.
     */
    public TreeMap<Character, Integer> getTransitions(Set<Character> letters) {
        TreeMap<Character, Integer> out = new TreeMap<Character, Integer>();
        for (char c : letters) {
            if (below.containsKey(c)) {
                out.put(c, below.get(c).id);
            } else {
                out.put(c, tree.getRootId());
            }
        }
        return out;
    }

    /**
     * Returns the ID of the current node.
     * @return the id
     */
    public int id() {
        return id;
    }

    /**
     * Returns the prefix of the current node.
     * @return the prefix
     */
    public String prefix() {
        return prefix;
    }

    /**
     * Creates a string representation of the node and it's sub nodes.
     * Not to be used in place of .toString(), because this is dependent on
     * other Node objects. 
     * @return string representation
     */
    public String show() {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append('[');
        sb.append(id);
        sb.append(']');

        Set<Character> keys = below.keySet();
        if (keys.size() > 0) {
            sb.append(" (");
            Iterator<Character> keysIt = keys.iterator();
            while (keysIt.hasNext()) {
                char key = keysIt.next();
                sb.append(key);
                sb.append(": ");
                sb.append(below.get(key).show());
                if (keysIt.hasNext())
                    sb.append(", ");
            }
            sb.append(')');
        }

        return sb.toString();
    }

}
