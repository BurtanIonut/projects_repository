package pckT3;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         This class extends the class {@link pckT3.Node#Node() Node} and is
 *         used to create nodes in the binary-tree, holding information on the
 *         operators(class {@link pckT3.Op#Op(String) Op}) in an expression.
 *
 */
public class NodeOp extends Node {

	// This value will be set to true is the node has been visited once.
	private boolean fixed;
	// This object holds the result of the operation described by the operator
	// held by this node and it's "children" nodes as operands.
	private Object value = null;

	public NodeOp(Op operator) {

		setInfo(operator);
	}

	/**
	 * @see pckT3.Visitable#getResult(pckT3.Visitor)
	 */
	@Override
	public Object getResult(Visitor<?> visitor) {

		return visitor.visit(this);
	}

	/**
	 * Set the boolean field "fixed" equal to true if the node has been visited
	 * once.
	 */
	public void setFixed() {
		this.fixed = true;
	}

	/**
	 * 
	 * @return the value of the boolean "fixed" which is set to true if the node
	 *         has been visited once
	 */
	public boolean isFixed() {
		if (fixed == true) {
			return true;
		}
		return false;
	}

	public void setValue(Object val) {

		this.value = val;
	}

	public Object getValue() {

		return value;
	}
}
