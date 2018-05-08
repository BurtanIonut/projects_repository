package pckT3;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         This class extends the class {@link pckT3.Node#Node() Node} and is
 *         used to create nodes in the binary-tree, holding information on the
 *         operands(class {@link pckT3.Element#Element(String, Object) Element})
 *         of the expression. Also, it has the required methods to make an
 *         Object of its type "visitable" by the {@link pckT3.Visitor Visitor}.
 */
public class NodeElement extends Node {

	public NodeElement(Element<?> elem) {

		setInfo(elem);

	}

	/**
	 * @see pckT3.Visitable#getResult(pckT3.Visitor)
	 */
	@Override
	public Object getResult(Visitor<?> visitor) {

		return visitor.visit(this);
	}

	/**
	 * 
	 * @return the value of the field "value" in the Element Object, equal to
	 *         the value of the variable described in the input file
	 */
	public Object getValue() {

		Element<?> aux = (Element<?>) (this.getInfo());
		return aux.getValue();
	}
}
