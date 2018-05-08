package pckT3;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         Class describing the NaN data type
 */
public class NaN {
	private boolean isInt;
	private boolean isDouble;
/**
 * 
 * @param type the type of the result of the operation generating the "NaN"
 */
	public NaN(String type) {
		switch (type) {
		case "Integer":
			setInt();
			break;
		case "Double":
			setDouble();
			break;
		}
	}

	/**
	 * Set the "isInt" field of NaN Object to true if the NaN is created by an
	 * operation generating an Integer type result.
	 */
	public void setInt() {

		isInt = true;
	}

	/**
	 * Set the "isDouble" field of NaN Object to true if the NaN is created by an
	 * operation generating a Double type result.
	 */
	public void setDouble() {

		isDouble = true;
	}

	public Object getType() {

		if (isInt) {
			return new Integer(0);
		} else if (isDouble) {
			return new Double(0.0);
		} else {
			return null;
		}
	}
}
