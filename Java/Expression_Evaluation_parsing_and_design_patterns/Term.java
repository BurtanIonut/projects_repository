package pckT3;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         Abstract class, inherited by the Classes
 *         {@link pckT3.Element#Element(String, Object) Element} and
 *         {@link pckT3.Op#Op(String) Op}, which are associated with the
 *         operands and operators in an expression.
 *
 */
public abstract class Term {

	// The string describing either the variable name of the operand or the
	// symbol of the operator in an expression
	private String id;

	public String getId() {

		return id;
	}

	public void setId(String id) {

		this.id = id;
	}

}
