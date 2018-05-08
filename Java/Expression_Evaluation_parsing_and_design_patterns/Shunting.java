package pckT3;

import java.util.*;

/**
 * 
 * @author Burtan Ionut-Lucian 325CB
 *         <p>
 *         This class describes the Shunting algorithm, applied on a String[] as
 *         input
 *
 */
public class Shunting {

	private LinkedList<String> expr = new LinkedList<>();

	public Shunting(String[] inp) {
		// generate a queue to hold the operators
		LinkedList<String> fifo = new LinkedList<>();
		for (int i = 0; i < inp.length; i++) {
			if (Statics.isOperation(inp[i])) {
				if (fifo.size() != 0) {
					while (Statics.hasPriority(fifo.getLast(), inp[i])) {
						// remove operators from the queue and add them to the
						// output expression as long as the current operator has
						// greater priority than the operators in the queue
						expr.add(fifo.getLast());
						fifo.removeLast();
						if (fifo.size() == 0) {
							break;
						}
					}
				}
				// add the current operator to queue
				fifo.add(inp[i]);
			} else if (inp[i].compareTo("(") == 0) {
				fifo.add(inp[i]);
			} else if (inp[i].compareTo(")") == 0) {
				while (fifo.getLast().compareTo("(") != 0) {
					expr.add(fifo.getLast());
					fifo.removeLast();
				}
				fifo.removeLast();
				// adds the operand to the output expression
			} else {
				expr.add(inp[i]);
			}
		}
		// insert remaining operators from the queue to the output expression
		while (fifo.size() != 0) {
			expr.add(fifo.getLast());
			fifo.removeLast();
		}
	}

	/**
	 * 
	 * @return the RPN of the read expression
	 */
	public LinkedList<String> getExpresion() {

		return expr;
	}
}
