package pack;

/**
 * 
 * @author Burtan-Ionut_325CB
 *         <p>
 *         This class describes a basic "cell" that stores a
 *         {@link pack.Node#Node(String, char, char, int) Node} object The
 *         "cells" will be used to describe the Linked List class
 *         {@link pack.ListNodes#ListNodes() ListNodes}
 *
 */
public class Cell {

	Node info;
	Cell next;

	/**
	 * A basic constructor.
	 * <p>
	 * It copies the {@link pack.Node#Node(String, char, char, int) Node} 'nod'
	 * argument's reference to the field 'info'. It also sets the "next"
	 * {@link #Cell(Node) Cell} value to "null" as we consider a 'freshly' made
	 * cell to not be linked to any other cell.
	 * 
	 * @param nod
	 *            Adds the {@link pack.Node#Node(String, char, char, int) Node}
	 *            as "information" to the {@link #Cell(Node) Cell}
	 */
	public Cell(Node nod) {
		info = nod;
		next = null;
	}
}
