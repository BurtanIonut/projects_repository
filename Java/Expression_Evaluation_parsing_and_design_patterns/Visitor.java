package pckT3;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *
 * @param <T>
 *            The Visitor interface of the Visitor design pattern, over
 *            {@link pckT3.Node#Node() Nodes}, containing the "visit" methods
 */
public interface Visitor<T> {
	/**
	 * 
	 * @param nodeEl
	 *            a {@link pckT3.NodeElement#NodeElement(Element) NodeElement}
	 * @return the value of the variable stored in the {@link pckT3.Node#Node()
	 *         nodeEl}
	 */
	public T visit(NodeElement nodeEl);

	/**
	 * 
	 * @param nodeOp
	 *            a {@link pckT3.NodeOp#NodeOp(Op) NodeOp}
	 * @return Recursively calculate the return values of the left and right
	 *         children of the current node and evaluate the expression based on
	 *         the operator symbol held by the current node, return the result
	 *         of said evaluation.
	 */
	public T visit(NodeOp nodeOp);
}
