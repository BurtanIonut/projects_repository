package pack;

/**
 * 
 * @author Burtan-Ionut_325CB
 *         <p>
 *         This abstract method specifies the headers of the 2 common methods
 *         for the classes: {@link pack.ListNodes#ListNodes() ListNodes},
 *         {@link pack.VectNodes#VectNodes() VectNodes},
 *         {@link pack.SetNodes#SetNodes() SetNodes}
 *
 */
public abstract class AdjacentNodes {
	/**
	 * This method adds a "one-way" link between 2 nodes
	 * 
	 * @param node
	 *            This is the node to be added as a new adjacent node to the
	 *            node that calls this method. It basically creates a "one way"
	 *            link between the "caller" node and this node.
	 */
	public abstract void addLink(Node node);

	/**
	 * This method removes a link from one node to another
	 * <p>
	 * It also behaves in a "one-way" manner, it only deletes the link from the
	 * "caller" {@link pack.Node#Node(String, char, char, int) Node} to the
	 * {@link pack.Node#Node(String, char, char, int) Node} specified by the
	 * argument of the method. It does not matter to this method if the
	 * parameter {@link pack.Node#Node(String, char, char, int) Node} has a link
	 * of it's own to the "caller".
	 * 
	 * @param node
	 * Adds a link between this node and the caller {@link pack.Node#Node(String, char, char, int) Node}
	 */
	public abstract void delLink(Node node);

}
