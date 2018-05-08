package pckT3;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         This class is responsible for generating the Element objects holding
 *         the relevant information of the input file variables
 *
 */
public class ElementFactory {

	private static ElementFactory instance = null;

	private ElementFactory() {

	}

	/**
	 * Part of the Singleton design pattern, it also provides "lazy
	 * initialization"
	 * 
	 * @return the ElementFactory Object
	 */
	public static ElementFactory getInstance() {

		if (instance == null) {

			instance = new ElementFactory();
			return instance;
		}
		return instance;
	}

	/**
	 * This method generates an Element based on the "input" parameter
	 * 
	 * @param input
	 *            of one of 3 types: String, int, double
	 * @return generated {@link pckT3.Element#Element(String, Object) Element}
	 *         or null
	 */
	public Element<?> getElement(String[] input) {

		Element<?> el;
		switch (input[0]) {
		case "int":
			el = new Element<Integer>(input[1], Integer.parseInt(input[2]));
			return el;
		case "double":
			el = new Element<Double>(input[1], Double.parseDouble(input[2]));
			return el;
		case "string":
			el = new Element<String>(input[1], input[2]);
			return el;
		}
		return null;
	}

}
