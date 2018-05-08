package pack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Burtan-Ionut_325CB
 *         <p>
 *         Main Class of the program
 *
 */
public class Main {

	public static void main(String[] args) {

		ListNodes graf = new ListNodes();
		BufferedReader f = null;
		BufferedReader p = null;
		PrintWriter w = null;
		char typeA = 'a';
		char typeB = 'a';
		char typeC = 'a';
		char[] settings = new char[3];
		String[] splat;
		String[] aux;
		String name;
		String fileName;
		char type = 'a';
		char version = 'a';
		Node n = null;
		Node m = null;
		int i = 0;
		try {
			f = new BufferedReader(new FileReader(args[0])); // Path to file
																// containing
																// the
																// instructions
			String line = f.readLine();
			while (line != null) { // Process every instruction
				splat = line.split(" ");
				switch (splat[0]) {
				case "Settings": // In case a Setting instruction is read,
									// retain the versions each node type will
									// respect
					typeA = splat[1].charAt(0);
					typeB = splat[2].charAt(0);
					typeC = splat[3].charAt(0);
					settings[0] = typeA;
					settings[1] = typeB;
					settings[2] = typeC;
					break;
				case "Add": // In case an Add instruction is read, create a new
							// node and set it's links to the other specified
							// nodes
					name = splat[2];
					type = splat[1].charAt(3);
					if (type == 'A') {
						version = typeA;
					} else if (type == 'B') {
						version = typeB;
					} else if (type == 'C') {
						version = typeC;
					}
					n = new Node(name, version, type, -1); // Creates a new
															// node, based on
															// the
															// specifications
															// read from the
															// file
					i = 3;
					while (i < splat.length) {
						m = Operatii.getByName(graf, splat[i]);
						if (m != null) {
							Operatii.addM(n, m); // Adds references between the
													// node to be added and the
													// specified nodes
						}
						i++;
					}
					graf.addLink(n);// Inserts the node into the graph
					break;
				case "AddM": // In case an AddLink operation in read
					Operatii.addM(Operatii.getByName(graf, splat[1]), Operatii.getByName(graf, splat[2]));
					break;
				case "Serialize":// In case a Serialize operation is read
					w = new PrintWriter(new BufferedWriter(new FileWriter(splat[2])));
					Operatii.serializeaza(Operatii.getByName(graf, splat[1]), w);
					Operatii.resetId(graf);
					w.close();
					break;
				case "Deserialize": // In case a Deserialized operation is read
					p = new BufferedReader(new FileReader(splat[1])); // File to
																		// deserialize
																		// from
					fileName = splat[1];
					aux = fileName.split("\\.");
					fileName = aux[0];
					w = new PrintWriter(new BufferedWriter(new FileWriter("Deserialize_" + fileName + "_CAST.log"))); // Creates
																														// the
																														// file
																														// to
																														// write
																														// the
																														// result
																														// of
																														// the
																														// deserialization
																														// process
					graf = Operatii.deserializeaza(p, w, settings); // Deserializez
																	// the node
																	// from the
																	// specified
																	// file
					w.close();
					break;
				case "Del": // In case a Delete Node operation is read
					m = Operatii.getByName(graf, splat[2]);
					if (m != null) {
						if (m.type == splat[1].charAt(3)) { // Delete node only
															// if
							// the type of node
							// specified by the
							// instruction matches
							// the one of the node
							// to be deleted
							Operatii.delN(m, graf);
						}
					}
					break;
				case "DelM": // In case a Delete Link operator is read
					Operatii.delM(Operatii.getByName(graf, splat[1]), Operatii.getByName(graf, splat[2]));
					break;
				}
				line = f.readLine();
			}
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}
}
