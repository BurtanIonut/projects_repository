package pckT3;

import java.io.*;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         This class is responsible for processing the input Strings
 *
 */
public class StringProcessor {

	private HashTable hash;

	public StringProcessor(HashTable hash) {

		this.hash = hash;
	}

	public void processString(PrintWriter f, String inp) {
		// If the read String is an expression to be evaluated
		if (inp.substring(0, 4).compareTo("eval") == 0) {
			String refined = inp;
			refined = refined.substring(4, refined.length());
			Shunting sh = new Shunting(refined.split("[ ;]"));
			Tree arb = new Tree();
			arb.buildTree(sh.getExpresion(), hash);
			SolverVisitor vs = new SolverVisitor();
			Object rez = arb.root.getResult(vs);
			if (rez instanceof NaN) {
				f.print("NaN");
				f.print("\n");
				return;
			} else if (rez instanceof Double) {
				f.print((Math.round((Double) rez * 100.0) / 100.0));
				f.print("\n");
			} else {
				f.print(rez.toString());
				f.print("\n");
			}
		}
		// If the read String is a variable of type String
		else if (inp.charAt(0) == 's') {
			String[] aux1 = inp.split("[\";]");
			String aux2 = aux1[1];
			String[] auxSplat = aux1[0].split("[ =]");
			String[] splat = new String[3];
			splat[0] = auxSplat[0];
			splat[1] = auxSplat[1];
			splat[2] = aux2;
			ElementFactory elF = ElementFactory.getInstance();
			Element<?> el = elF.getElement(splat);
			this.hash.addElement(el);
			return;
		}
		// If the read String is either an "int" or "double" variable
		else {
			String[] splat = inp.split("[ ;]");
			splat[2] = splat[3];
			splat[3] = null;
			ElementFactory elF = ElementFactory.getInstance();
			Element<?> el = elF.getElement(splat);
			this.hash.addElement(el);
			return;
		}
	}

}
