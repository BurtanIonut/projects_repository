package pack;
/**
 * 
 * @author Burtan-Ionut_325CB
 *         <p>
 *         This Class describes a Set implemented as a basic Binary Search Tree,
 *         similar to the {@link pack.ListNodes#ListNodes() ListNodes} and {@link pack.VectNodes#VectNodes() VectNodes}, it also extends the
 *         {@link pack.AdjacentNodes#AdjacentNodes() AdjacentNodes} class and Overrides its methods.
 *
 */
public class SetNodes extends AdjacentNodes {

	ArbNode root;// The "root" of the Binary Tree

	/**
	 * This method adds a child to the current tree and also maintains an order
	 * <p>
	 * Order is maintained by the rule: The parent's name is alphabetically
	 * greater than the name of the left child and lesser than the value of the
	 * right child's name. Until the condition is fulfilled, the method will
	 * keep recursively calling itself. The method {@link #addChild(ArbNode, Node) addChild} is
	 * responsible for inserting the "children". The 3 "if" statements maintain
	 * the order.
	 * 
	 * @param parent
	 *            {@link pack.ArbNode#ArbNode(Node) ArbNode} "parent"
	 * @param nod
	 *            {@link pack.Node#Node(String, char, char, int) Node} "child" to be added to "parent" with regard to
	 *            the rule
	 */
	public void addChild(ArbNode parent, Node nod) {

		if (nod.name.compareTo(parent.info.name) == 0) {
			return; // No duplicates allowed in a Set
		}
		if (nod.name.compareTo(parent.info.name) < 0) {
			if (parent.c1 == null) {
				parent.c1 = new ArbNode(nod);
				return;
			}
			addChild(parent.c1, nod);
			return;
		}
		if (nod.name.compareTo(parent.info.name) > 0) {
			if (parent.c2 == null) {
				parent.c2 = new ArbNode(nod);
				return;
			}
			addChild(parent.c2, nod);
			return;
		}
	}

	@Override
	public void addLink(Node nod) {
		if (root == null) {
			root = new ArbNode(nod);
			return;
		}
		addChild(root, nod);
	}

	/**
	 * Given a tree of elements {@link pack.ArbNode#ArbNode(Node) ArbNode}, the method gets the leftmost
	 * leaf of the tree
	 * 
	 * @param arb
	 *            reference to the tree itself
	 * @return
	 *         leftmost leaf of the tree
	 */
	public ArbNode getLastLeft(ArbNode arb) {
		if (arb == null) {
			return null;
		}
		ArbNode aux = arb;
		while (aux.c1 != null) {
			aux = aux.c1;
		}
		return aux;
	}

	@Override
	public void delLink(Node node) {

		int target = root.info.name.compareTo(node.name);
		// Case: node to be deleted is the root
		if (target == 0) {// Node is root
			ArbNode saveRef = root.c1;// Save reference to root's left child
			if (this.root.c2 == null) {// If root has no right child
				this.root = this.root.c1;// The new root of the tree becomes the
											// left child
				return;
			}
			this.root = this.root.c2;// If root has a right child, that child
										// becomes the root
			ArbNode aux = getLastLeft(root);// "aux" becomes the leftmost leaf
											// of the current root
			if (aux == null) {// Return if the current root has no left child
				return;
			}
			aux.c1 = saveRef;// Append the left child of the initial root to
								// "aux"
			return;
		}
		// Case: node to be deleted is not root
		int prevTarget = target; // prevTarget and Target store the last 2
									// results of the node comparison operation
		ArbNode prev = root;// "prev" stores the previous value of "aux"
		ArbNode aux = root;
		ArbNode aux2;
		while (true) {
			if (target < 0) { // If true, search for the node in the right
								// subtree of the current tree
				prev = aux;
				aux = aux.c2;
			} else if (target > 0) { // If true, search for the node in the left
										// subtree of the current tree
				prev = aux;
				aux = aux.c1;
			} else if (target == 0) { // If true, node found
				if (prevTarget > 0) { // True if the previous visited node's
										// name was "greater" than the node to
										// be deleted
					prev.c1 = aux.c2;// Previous node's left child becomes the
										// node's right child
					aux2 = getLastLeft(prev.c1); // Get the new Previous node's
													// left child's leftmost
													// leaf
					if (aux2 == null) { // True if the leaf does not exist (is
										// "null")
						if (prev.c1 == null) { // True if the new previous's
												// left child is "null"
												// ...which means the leftmost
												// leaf is actually the left
												// child and therefore equal to
												// "null"
							prev.c1 = aux.c1; // Previous nodes's left child
												// becomes the node's left
												// child
							return;
						}
						prev.c1.c1 = aux.c1; // If the new previous node's left
												// child exists but that node's
												// left child does not then that
												// node's left child becomes the
												// left child of the node to be
												// deleted
						return;
					}
					aux2.c1 = aux.c1; // The more general case, in which both
										// the node and it's leftmost leaf exist
					return;
				} else if (prevTarget < 0) { // True if the previous visited
												// node's name was "lesser" than
												// the name of the node to be
												// deleted
					prev.c2 = aux.c2; // The previous node's right child becomes
										// the right child of the node to be
										// deleted
					aux2 = getLastLeft(prev.c2); // "aux2" is the leftmost leaf
													// of the new previous's
													// node right "child"

					if (aux2 == null) { // ...this condition behaves like it's
										// "counterpart" from above
						if (prev.c2 == null) {
							prev.c2 = aux.c1;
							return;
						}
						prev.c2.c1 = aux.c1;
						return;
					}
					aux2.c1 = aux.c1;
					return;
				}
			}
			if (aux == null) { // True if the node to be deleted does not exist
				return;
			}
			prevTarget = target;
			target = aux.info.name.compareTo(node.name);
		}
	}
}
