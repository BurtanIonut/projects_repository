package pckT3;

import java.util.*;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         The HashTable class, basically a wrap over the predefined HashMap,
 *         used to store the input variables held in
 *         {@link pckT3.Element#Element(String, Object) Element} Objects
 */
public class HashTable {

	private HashMap<String, Element<?>> hash;

	public HashTable() {

		hash = new HashMap<>();
	}

	public void addElement(Element<?> elem) {

		hash.put(elem.getId(), elem);
	}

	/**
	 * 
	 * @param id
	 *            "name" of the variable
	 * @return the Element with the "id" identifier from the HashMap
	 */
	public Element<?> getElement(String id) {

		return hash.get(id);
	}
}
