package pack;

/**
 * 
 * @author Burtan-Ionut_325CB
 *         <p>
 *         This class describes the "nodes" of the Binary Tree
 *         {@link pack.SetNodes#SetNodes() Set}
 */
public class ArbNode {

	Node info;// Information of type Node stored in the "node"
	ArbNode c1;// Link to the "Left Child"
	ArbNode c2;// Link to the "Righ Child"

	public ArbNode() {

	}

	/**
	 * A constructor that initiates a {@link #ArbNode(Node) ArbNode} with a set
	 * information of type {@link pack.Node#Node(String, char, char, int) Node}
	 * <p>
	 * Note: The "children" of the node are set to "null" as we create a node
	 * that is considered a "leaf"
	 * 
	 * @param info
	 *            The {@link pack.Node#Node(String, char, char, int) Node}
	 *            information to be stored
	 */
	public ArbNode(Node info) {
		this.info = info;
		c1 = null;
		c2 = null;
	}

	/**
	 * Basic method of inserting a "left child" to the current node
	 * 
	 * @param nod
	 *            {@link pack.Node#Node(String, char, char, int) Node} to be
	 *            added
	 */
	public void addC1(Node nod) {

		c1 = new ArbNode(nod);
	}

	/**
	 * Basic method of inserting a "right child" to the current node
	 * 
	 * @param nod
	 *            {@link pack.Node#Node(String, char, char, int) Node} to be
	 *            added
	 */
	public void addC2(Node nod) {

		c2 = new ArbNode(nod);
	}
}
