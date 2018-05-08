package pckT3;

import java.util.*;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         This class is used to create a Tree objects with nodes of type
 *         {@link pckT3.Node#Node() Node} using a {@link pckT3.Shunting
 *         Shunting} Object and a {@link pckT3.HashTable#HashTable() HashTable}
 *
 */
public class Tree {

	Node root;

	/**
	 * This method creates a binary-tree with the information presented by the
	 * inputs
	 * 
	 * @param shunting
	 *            the List to be converted to a Tree, holding the RPN version of
	 *            the expression to be evaluated
	 * @param hash
	 *            the {@link pckT3.HashTable#HashTable() HashTable} containing
	 *            the values of the variables in the expression
	 */
	public void buildTree(LinkedList<String> shunting, HashTable hash) {

		// This stack holds references to the initiated Nodes that have not been
		// added to
		// the Tree
		LinkedList<Node> auxStack = new LinkedList<Node>();
		String auxId;
		Element<?> auxElem;
		Node auxNode;
		while (shunting.size() != 0) {
			auxId = shunting.getFirst();
			// If an operator is read than the previous two values on the
			// auxStack will become it's children, after which the stack is
			// cleared of these two values and a reference to the current node
			// is
			// added
			if (Statics.isOperation(auxId)) {
				auxNode = new NodeOp(new Op(auxId));
				auxNode.setRChild(auxStack.getLast());
				auxStack.removeLast();
				auxNode.setLChid(auxStack.getLast());
				auxStack.removeLast();
				auxStack.add(auxNode);
				shunting.removeFirst();
				root = auxNode;
				// If an operator is read, add it to the auxStack
			} else {
				auxElem = hash.getElement(auxId);
				auxNode = new NodeElement(auxElem);
				auxStack.add(auxNode);
				shunting.removeFirst();
			}
		}
	}
}
