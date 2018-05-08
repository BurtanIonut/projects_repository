package pack;

/**
 * 
 * @author Burtan-Ionut_325CB
 *         <p>
 *         This class describes the "Node" object. It has a constructor and 2
 *         methods for the basic operations of linking 2 nodes together
 *         (addLink) and deleting a link between 2 nodes (delLink).
 *
 */
public class Node {

	String name; // Unique String identifier
	int id; // Used when Serializing
	char version; // One of the digits: '1', '2', '3'
	char type; // One of the letters: 'A', 'B', 'C'
	String setVersion; // Represents how the ajacent nodes are stored
	ListNodes references = new ListNodes();
	// to-do link d-ala...., folosit la serializare
	AdjacentNodes adj; // Adjacent nodes of the current node

	/**
	 * The constructor of the Node class
	 * <p>
	 * It creates a node object given the specified information Note: when i
	 * create a new Node to insert into a graph, I will initialize all id-s with
	 * '-1'. The id value becomes relevant only when the node is to be
	 * serialized.
	 * 
	 * @param name
	 *            The 'name' of the
	 *            {@link pack.Node#Node(String, char, char, int) Node} to be
	 *            instanced
	 * @param version
	 *            This field is relevant for the serialization operation, other
	 *            methods will use as means to identify the Node's version by
	 *            the char field 'version'.
	 * @param type
	 *            The 'type' of the
	 *            {@link pack.Node#Node(String, char, char, int) Node} to be
	 *            instanced
	 * @param id
	 *            The 'id' of the {@link pack.Node#Node(String, char, char, int)
	 *            Node} to be instanced
	 */
	public Node(String name, char version, char type, int id) {

		this.name = name;
		this.version = version;
		this.type = type;
		this.id = id;
		switch (version) {
		case '1':
			this.setVersion = "LIST";
			adj = new ListNodes();
			break;
		case '2':
			this.setVersion = "VECTOR";
			adj = new VectNodes();
			break;
		case '3':
			this.setVersion = "SET";
			adj = new SetNodes();
			break;
		}
	}

	/**
	 * This method creates a 'two-way' link between the current node and the one
	 * given as argument. This method uses the external method of the
	 * 'AdjacentNodes' class, 'addLink', Overridden according to the type of
	 * class that "handles" the adjacent nodes
	 * 
	 * @param nod
	 *            The argument of type Node
	 */
	public void addLink(Node nod) {
		adj.addLink(nod);
	}

	/**
	 * This method deletes the linkage between the current node and the one
	 * given as argument
	 * 
	 * @param nod
	 *            Argument of type Node
	 */
	public void delLink(Node nod) {
		adj.delLink(nod);
	}
}
