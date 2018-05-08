package pack;

import java.io.*;

/**
 * 
 * @author Burtan-Ionut_325CB
 *         <p>
 *         This Class describes the serialization operation on a
 *         {@link pack.Node#Node(String, char, char, int) Node} object.
 */
public class Serialize {
	/**
	 * This method writes "tab" characters into a file
	 * 
	 * @param offset
	 *            How many "tabs" to write
	 * @param f
	 *            Where to write the "tabs"
	 */
	public static void tabPad(int offset, PrintWriter f) {

		for (int i = 1; i <= offset; i++) {
			f.print("\t");
		}
	}

	/**
	 * This method writes into a file information based on the "switch"
	 * statement
	 * 
	 * @param tag
	 *            A String given as parameter to "switch"
	 * @param nod
	 *            Where to extract the relevant information from
	 * @param offset
	 *            "Tabs" to be written as offset before the relevant
	 *            information.
	 * @param f
	 *            Where to write
	 */
	public static void printTag(String tag, Node nod, int offset, PrintWriter f) {

		switch (tag) {
		case "Obj":
			tabPad(offset, f);
			f.print("<Object class=" + "\"Nod" + nod.type + "\" " + "Version=\"" + nod.version + "\"" + " id=\""
					+ nod.id + "\">");
			f.print("\n");
			tabPad(offset + 1, f);
			f.print("<Nume>" + nod.name + "</Nume>" + "\n");
			tabPad(offset + 1, f);
			f.print("<" + nod.setVersion + ">");
			f.print("\n");
			break;
		case "Ref":
			tabPad(offset, f);
			f.print("<Reference class=" + "\"Nod" + nod.type + "\" " + "Version=\"" + nod.version + "\"" + " id=\""
					+ nod.id + "\">");
			f.print("\n");
			break;
		}
	}

	/**
	 * Similar to the {@link #printTag(String, Node, int, PrintWriter) printTag}
	 * method.
	 * 
	 * @param nod
	 *            Where to get information from
	 * @param offset
	 *            "Tab" character offset
	 * @param f
	 *            Path to file to write in
	 */
	public static void closeTag(Node nod, int offset, PrintWriter f) {

		tabPad(offset + 1, f);
		f.print("</" + nod.setVersion + ">" + "\n");
		tabPad(offset, f);
		f.print("</Object>" + "\n");
	}

	/**
	 * Checks if the first {@link pack.Node#delLink(Node) Node} has a reference
	 * to the second
	 * 
	 * @param n
	 *            Node to be checked for reference to the second node
	 * @param m
	 *            Node to search for reference of on the first Node
	 * @return Return 0 if a reference has not been found, 1 otherwise
	 */
	public static int hasReference(Node n, Node m) {
		ListNodes lst = n.references;
		Cell aux = lst.head;
		while (aux != null) {
			if (aux.info == m) {
				return 1;
			}
			aux = aux.next;
		}
		return 0;
	}

	/**
	 * This method "initializes" the Serialization operation
	 * <p>
	 * The {@link #Serialize() Serialize} method has a recursive behavior, it
	 * requires an already serialized
	 * {@link pack.Node#Node(String, char, char, int) Node} to "start"
	 * 
	 * @param n
	 *            {@link pack.Node#Node(String, char, char, int) Node} object to
	 *            be serialized
	 * @param serH
	 *            The "Serialization Helper"
	 *            {@link pack.serializeHelper#serializeHelper() serH}
	 * @param offset
	 *            The "tab" character offset
	 * @param f
	 *            File to write in
	 */
	public static void serializeLoad(Node n, serializeHelper serH, int offset, PrintWriter f) {

		n.id = serH.idCrt;
		serH.idCrt++;
		printTag("Obj", n, offset, f);
		if (n.adj == null) {
			closeTag(n, offset, f);
			return;
		}
		serialize(n, serH, offset + 2, f);
		closeTag(n, offset, f);
	}

	/**
	 * Serializes the adjacent nodes of the
	 * {@link pack.Node#Node(String, char, char, int) Node} given as parameter
	 * and their adjacent nodes, recursively
	 * <p>
	 * 
	 * @param n
	 *            Already serialized node
	 * @param serH
	 *            The ({@link pack.serializeHelper#serializeHelper()
	 *            serializeHelper}
	 * @param offset
	 *            "Tab" offset
	 * @param f
	 *            Path to file to write in
	 */
	public static void serialize(Node n, serializeHelper serH, int offset, PrintWriter f) {

		ListNodes lst = convertAdjacent(n);
		Cell aux = lst.head;
		while (aux != null) {
			if (aux.info.id == -1) {
				serializeLoad(aux.info, serH, offset, f);
			} else if (aux.info.id < serH.idCrt) { // If
				if (hasReference(aux.info, n) == 0) {
					printTag("Ref", aux.info, offset, f);
					n.references.addLink(aux.info);
					aux.info.references.addLink(n);
				}
			}
			aux = aux.next;
		}
	}

	/**
	 * Converts the Adjacent nodes of the
	 * {@link pack.Node#Node(String, char, char, int) parameter} to a
	 * {@link pack.ListNodes#ListNodes() ListNodes}
	 * 
	 * @param n
	 *            {@link pack.Node#Node(String, char, char, int) Node} to have
	 *            it's adjacent nodes returned to a list
	 * @return A ListNodes with all the Node's "neighbors"
	 */
	public static ListNodes convertAdjacent(Node n) {

		ListNodes lst = new ListNodes();
		switch (n.version) { // Depending on the Node's way of "storing" it's
								// adjacent nodes
		case '1': // For the LIST way
			lst = (ListNodes) (n.adj);
			return lst;
		case '2': // For the VECTOR way
			if (n.adj == null) {
				break;
			}
			VectNodes vct = (VectNodes) (n.adj);
			lst = new ListNodes();
			for (int i = 0; i < vct.pozCrt; i++) {
				lst.addLink(vct.vect[i]);
			}
			return lst;
		case '3': // For the SET way
			if (n.adj == null) {
				break;
			}
			lst = new ListNodes();
			SetNodes set = (SetNodes) (n.adj);
			arbToList(lst, set.root);
			return lst;
		}
		return null;
	}

	/**
	 * Converts an {@link pack.ArbNode#ArbNode() ArbNode} to a
	 * {@link pack.ListNodes#ListNodes() list}
	 * 
	 * @param lst
	 *            A ListNodes
	 * @param arb
	 *            An ArbNode
	 */
	public static void arbToList(ListNodes lst, ArbNode arb) {
		if (arb == null) {
			return;
		}
		lst.addLink(arb.info);
		arbToList(lst, arb.c1);
		arbToList(lst, arb.c2);
		return;
	}
}
