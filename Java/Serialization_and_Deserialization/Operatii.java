package pack;

import java.io.*;

/**
 * 
 * @author Burtan-Ionut_325CB
 *         <p>
 *         This class holds a number of "higher-level" methods that use other
 *         previously implemented classes and methods.
 */
public class Operatii {
	/**
	 * Adds a "two-way"(see {@link #addM(Node, Node) addM method}) reference
	 * between the {@link pack.Node#Node(String, char, char, int) Node} and each
	 * element from the {@link pack.ListNodes#ListNodes() list}
	 * 
	 * @param n
	 *            The node t be added aas reference to each node in the list
	 * @param lst
	 *            The list of nodes to be referenced to the previously stated
	 *            node
	 */
	public static void addN(Node n, ListNodes lst) {

		Cell aux = lst.head;
		while (aux != null) {
			addM(aux.info, n);
			aux = aux.next;
		}
	}

	/**
	 * Creates the "two-way" reference between the 2
	 * {@link pack.Node#Node(String, char, char, int) Nodes}
	 * 
	 * @param n
	 *            First Node
	 * @param m
	 *            Second Node
	 */
	public static void addM(Node n, Node m) {

		n.addLink(m);
		m.addLink(n);

	}

	/**
	 * Deletes the "two-way" link between the 2
	 * {@link pack.Node#Node(String, char, char, int) Nodes}
	 * 
	 * @param n
	 *            First Node
	 * @param m
	 *            Second Node
	 */
	public static void delM(Node n, Node m) {

		n.delLink(m);
		m.delLink(n);
	}

	/**
	 * Iterates through all the elements of the
	 * {@link pack.ListNodes#ListNodes() list} and deletes all "2-way" links to
	 * the {@link pack.Node#Node(String, char, char, int) Node} specified by the
	 * first parameter. In the end, the reference from the "graf" to the Node is
	 * also deleted.
	 * 
	 * @param n
	 *            First parameter
	 * @param graf
	 *            List of nodes
	 */
	public static void delN(Node n, ListNodes graf) {

		Cell aux = graf.head;
		while (aux != null) {
			if (aux.info != n) {
				aux.info.delLink(n);
			}
			aux = aux.next;
		}
		graf.delLink(n);
	}

	/**
	 * Initializes the {@link pack.Serialize#Serialize() Serialization}
	 * operation
	 * 
	 * @param n
	 *            Node to be serialized
	 * @param f
	 *            Path to file to write information into
	 */
	public static void serializeaza(Node n, PrintWriter f) {

		serializeHelper serH = new serializeHelper();
		Serialize.serializeLoad(n, serH, 0, f);
	}

	/**
	 * Initializes the {@link pack.Deserialize#Deserialize() Deserialization}
	 * operation
	 * 
	 * @param f
	 *            Path to file from which to read information
	 * @param p
	 *            Path to the file to write information about the
	 *            Deserialization process
	 * @param settings
	 *            A vector containing the {@link pack.Node#version Versions} the
	 *            {@link pack.Node#Node(String, char, char, int) Nodes} will
	 *            adhere to
	 * @return Returns a {@link pack.ListNodes#ListNodes() graph} holding all
	 *         desrialized nodes
	 */
	public static ListNodes deserializeaza(BufferedReader f, PrintWriter p, char[] settings) {

		ListNodes graf = new ListNodes();
		BufferedReader buf = null;
		try {
			buf = f;
			String line = buf.readLine();
			Deserialize.deserialize(graf, line, buf, p, settings);
			Cell aux = graf.head;
			while (aux != null) {
				aux.info.id = -1;
				aux = aux.next;
			}
			buf.close();
		} catch (IOException e) {

		}
		return graf;
	}

	/**
	 * Get a {@link pack.Node#Node(String, char, char, int) Node} from a
	 * {@link pack.ListNodes#ListNodes() graph} by name
	 * 
	 * @param graf
	 *            The list in which to search for the node
	 * @param name
	 *            The name of the node to search for
	 * @return The node, if found
	 */
	public static Node getByName(ListNodes graf, String name) {

		Cell aux = graf.head;
		while (aux != null) {
			if (aux.info.name.compareTo(name) == 0) {
				return aux.info;
			}
			aux = aux.next;
		}
		return null;
	}

	/**
	 * Resets all the id's of the {@link pack.ListNodes#ListNodes() graph's}
	 * {@link pack.Node#Node(String, char, char, int) Nodes} to -1
	 * 
	 * @param graf
	 *            Graph to get its nodes id's reset
	 */
	public static void resetId(ListNodes graf) {

		Cell aux = graf.head;
		while (aux != null) {
			aux.info.id = -1;
			aux = aux.next;
		}
	}
}
