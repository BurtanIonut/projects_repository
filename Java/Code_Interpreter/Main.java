

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class Main {

	public static void main(String[] args) {

		BufferedReader f = null;
		PrintStream w = null;
		String line = null;
		String rez = "";
		String expr = "";
		String fileIn = args[0];
		String fileOut = args[1];
		Node arb;
		Node_V v;	//Visitor pentru Node
		try {
			f = new BufferedReader(new FileReader(fileIn));
			w = new PrintStream(new FileOutputStream(fileOut));
			line = f.readLine();
			while (line != null) {			
				line = Statics.process(line);	//Scot taburi, newline-uri si caracterul '['
				expr += line;					//Concatenez toate liniile procesate la un string ce
				line = f.readLine();			 //reprezinta expresia cu care se construieste arborele
			}
			arb = Statics.createArb(expr);
			v = new Node_V();
			rez = arb.accept(v);				//In rez se retine rezultatul evaluarii expresiei
			w.print(rez);
			w.close();
			f.close();
		} catch (IOException e) {
			System.out.println("Unavailable file");
		}
	}
}
