package pack;

import java.io.*;

/**
 * 
 * @author Burtan-Ionut_325CB
 *         <p>
 *         This method is responsible for the Deserialization operation
 */
public class Deserialize {
	/**
	 * Splits the line into substrings given fixed delimiters
	 * 
	 * @param line
	 *            A line read from a file
	 * @return A vector of strings, extracted from the line for later use
	 *         eg.{@link #getInfo(String, String[])}
	 */
	public static String[] splitLine(String line) {

		return line.split("[<> ]");
	}

	/**
	 * Given a certain String as input it returns required information from a
	 * vector of strings
	 * 
	 * @param info
	 *            The string describing the required data
	 * @param splatLine
	 *            The vector of strings containing relevant information
	 * @return Returns the String "demanded" by the first parameter
	 */
	public static String getInfo(String info, String[] splatLine) {

		String[] aux;
		switch (info) { // each of this strings describe a specific piece of
						// information, which can be found in the vector of
						// strings on specific positions, though, a bit of
						// "processing" is still required (getting rid of the
						// quote marks)
		case "type":
			aux = splatLine[2].split("\"");
			return aux[1];
		case "ver":
			aux = splatLine[3].split("\"");
			return aux[1];
		case "id":
			aux = splatLine[4].split("\"");
			return aux[1];
		case "name":
			return splatLine[2];
		}
		return null;
	}

	/**
	 * Useful for getting the reference to a
	 * {@link pack.Node#Node(String, char, char, int) Node} from a
	 * {@link pack.ListNodes#ListNodes() ListNodes}
	 * 
	 * @param graf
	 *            A ListNodes
	 * @param id
	 *            Field to identify the Node by
	 * @return
	 * Return the {@link pack.Node#Node(String, char, char, int) Node} if found
	 */
	public static Node getNodeById(ListNodes graf, int id) {
		Cell aux = graf.head;
		while (aux != null) {
			if (aux.info.id == id) {
				return aux.info;
			}
			aux = aux.next;
		}
		return null;
	}

	/**
	 * It reads line by line from a file, processing every line and extracting
	 * the relevant information for the deserialization process. The new graph
	 * obtained will respect the "settings" received as argument
	 * 
	 * @param graf
	 *            Where the Node will be stored
	 * @param line
	 *            A line from a file
	 * @param f
	 *            Path to the processed file
	 * @param cast
	 *            Path to the log file, where the result of the deserialization
	 *            process is stored
	 * @param settings
	 *            Describes the new versions that the node types will adhere to
	 */
	public static void deserialize(ListNodes graf, String line, BufferedReader f, PrintWriter cast, char[] settings) {

		String lineAux = line;
		try {
			String[] splat = splitLine(lineAux); // Line to be processed
			char type = getInfo("type", splat).charAt(3); // Get the type of
															// the node in
															// process of
															// deserialization
			char version = getInfo("ver", splat).charAt(0); // Get the version
															// of the node
			int id = Integer.parseInt(getInfo("id", splat)); // Get the id of
																// the node
			String offset = splat[0];// Get "tab" offset
			lineAux = f.readLine();
			splat = splitLine(lineAux); // Line to be processed
			String name = getInfo("name", splat); // Get the name of the node
			switch (type) {
			case 'A': // Decides for the type 'A' of node what to write into the
						// "log" file, based on the node's version and the one
						// in the "setting"
				if (version > settings[0]) {
					cast.print("Fail cast Nod" + type + " " + name + " from Version=" + "\"" + version
							+ "\" to Version=" + "\"" + settings[0] + "\"" + "\n");
					version = settings[0];
				} else if (version < settings[0]) {
					cast.print("Ok cast Nod" + type + " " + name + " from Version=" + "\"" + version + "\" to Version="
							+ "\"" + settings[0] + "\"" + "\n");
					version = settings[0];
				}
				break;
			case 'B':
				if (version > settings[1]) {
					cast.print("Fail cast Nod" + type + " " + name + " from Version=" + "\"" + version
							+ "\" to Version=" + "\"" + settings[1] + "\"" + "\n");
					version = settings[1];
				} else if (version < settings[1]) {
					cast.print("Ok cast Nod" + type + " " + name + " from Version=" + "\"" + version + "\" to Version="
							+ "\"" + settings[1] + "\"" + "\n");
					version = settings[1];
				}
				break;
			case 'C':
				if (version > settings[2]) {
					cast.print("Fail cast Nod" + type + " " + name + " from Version=" + "\"" + version
							+ "\" to Version=" + "\"" + settings[2] + "\"" + "\n");
					version = settings[2];
				} else if (version < settings[2]) {
					cast.print("Ok cast Nod" + type + " " + name + " from Version=" + "\"" + version + "\" to Version="
							+ "\"" + settings[2] + "\"" + "\n");
					version = settings[2];
				}
				break;
			}
			Node n = new Node(name, version, type, id); // Finally creates the
														// Node based on the
														// information received
														// from the file
			graf.addLink(n);// Inserts the node into the graph
			lineAux = f.readLine();
			splat = splitLine(lineAux);
			while (splat[0].compareTo(offset) != 0) { // For each consecutive
														// node in the file
														// decide what to do
				if (splat[1].compareTo("Object") == 0) {// Deserializez a new
														// node
					deserialize(graf, lineAux, f, cast, settings);
				} else if (splat[1].compareTo("Reference") == 0) {// A node
																	// already
																	// deserialized,
																	// creates
																	// references
																	// between
																	// it and
																	// the
																	// previously
																	// deserialized
																	// node
					Node ref = getNodeById(graf, Integer.parseInt(getInfo("id", splat)));
					if (ref != null) {
						n.adj.addLink(ref);
						ref.adj.addLink(n);
					}
				}
				lineAux = f.readLine();
				splat = splitLine(lineAux);
			}
			return;
		} catch (IOException e) {

		}
	}
}
