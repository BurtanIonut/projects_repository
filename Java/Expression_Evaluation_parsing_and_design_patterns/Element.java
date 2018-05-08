package pckT3;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         Class Element inherits class {@link pckT3.Term#Term() Term}, it is used
 *         to create Element Objects, containing information on the operands.
 * @param <T>
 *            This is a general type, when created, the Object will have one of
 *            the following types: String, Integer, Double
 */
public class Element<T> extends Term {

	private T value;

	public Element(String id, T value) {

		setId(id);
		setValue(value);

	}

	public T getValue() {

		return value;
	}

	public void setValue(T val) {

		this.value = val;
	}
}
