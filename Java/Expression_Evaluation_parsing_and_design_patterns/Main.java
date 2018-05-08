package pckT3;

import java.io.*;

/**
 *
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         The main method, processes the input line by line using a
 *         {@link pckT3.StringProcessor#StringProcessor(HashTable)
 *         StringProcessor} Object and writes the output file.
 *
 */
public class Main {

	public static void main(String[] args) {

		BufferedReader f = null;
		PrintWriter w = null;
		String line = null;
		HashTable hash = new HashTable();
		StringProcessor sp = new StringProcessor(hash);
		try {
			f = new BufferedReader(new FileReader("arbore.in"));
			w = new PrintWriter(new BufferedWriter(new FileWriter("arbore.out")));
			line = f.readLine();
			while (line != null) {
				sp.processString(w, line);
				line = f.readLine();
			}
			w.close();
			f.close();
		} catch (IOException e) {
			System.out.println("Unavailable file");
		}
	}
}